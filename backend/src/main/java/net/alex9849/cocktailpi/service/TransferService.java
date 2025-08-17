package net.alex9849.cocktailpi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.IngredientGroup;
import net.alex9849.cocktailpi.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.model.transfer.ImportContents;
import net.alex9849.cocktailpi.payload.dto.category.CategoryDto;
import net.alex9849.cocktailpi.payload.dto.collection.CollectionDto;
import net.alex9849.cocktailpi.model.Collection;
import net.alex9849.cocktailpi.payload.dto.glass.GlassDto;
import net.alex9849.cocktailpi.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.AddableIngredientDto;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.IngredientDto;
import net.alex9849.cocktailpi.payload.request.ExportRequest;
import net.alex9849.cocktailpi.utils.SpringUtility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;

@Service
public class TransferService {
    private Map<Long, Path> importPaths = new HashMap<>();
    private long lastImportId = 0;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    private final CollectionService collectionService;
    private final SystemService systemService;

    public TransferService(RecipeService recipeService, IngredientService ingredientService, CollectionService collectionService, SystemService systemService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.collectionService = collectionService;
        this.systemService = systemService;
    }

    public long newImport(MultipartFile zipFile) throws IOException {
        Path tempDir = Files.createTempDirectory("import_");
        lastImportId++;
        long importId = lastImportId;
        importPaths.put(importId, tempDir);

        try (InputStream is = zipFile.getInputStream();
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path newPath = tempDir.resolve(entry.getName()).normalize();
                if (!newPath.startsWith(tempDir)) {
                    throw new IOException("Zip entry outside of target directory: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    Files.createDirectories(newPath.getParent());
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (Exception e) {
            deleteImport(importId);
            throw new IOException("Error unpacking ZIP file.", e);
        }
        scheduler.schedule(() -> deleteImport(importId), 10, TimeUnit.MINUTES);

        return importId;
    }

    public ImportContents readImport(long importId) {
        Path importPath = importPaths.get(importId);
        if (importPath == null) {
            throw new IllegalArgumentException("Import with ID " + importId + " does not exist.");
        }
        List<RecipeDto.Response.Detailed> recipes = new ArrayList<>();
        Map<Long, CategoryDto.Duplex.Detailed> categories = new HashMap<>();
        Map<Long, GlassDto.Duplex.Detailed> glasses = new HashMap<>();
        List<IngredientDto.Response.Detailed> ingredients = new ArrayList<>();
        List<CollectionDto.Response.Detailed> collections = new ArrayList<>();

        Path recipesFile = importPath.resolve("recipes.json");
        if (Files.exists(recipesFile)) {
            try (InputStream is = Files.newInputStream(recipesFile)) {
                recipes = SpringUtility.loadFromStream(is, RecipeDto.Response.Detailed.class);
            } catch (IOException e) {
                throw new RuntimeException("Error reading recipes from import file.", e);
            }
            for (RecipeDto.Response.Detailed detailedDto : recipes) {
                for (CategoryDto.Duplex.Detailed detailedCategory : detailedDto.getCategories()) {
                    categories.put(detailedCategory.getId(), detailedCategory);
                }
                GlassDto.Duplex.Detailed detailedGlass = detailedDto.getDefaultGlass();
                if (detailedGlass != null) {
                    glasses.put(detailedGlass.getId(), detailedGlass);
                }
            }
        }
        Path ingredientsFile = importPath.resolve("ingredients.json");
        if (Files.exists(ingredientsFile)) {
            try (InputStream is = Files.newInputStream(ingredientsFile)) {
                ingredients = SpringUtility.loadFromStream(is, IngredientDto.Response.Detailed.class);
            } catch (IOException e) {
                throw new RuntimeException("Error reading ingredients from import file.", e);
            }
        }
        Path collectionsFile = importPath.resolve("collections.json");
        if (Files.exists(collectionsFile)) {
            try (InputStream is = Files.newInputStream(collectionsFile)) {
                collections = SpringUtility.loadFromStream(is, CollectionDto.Response.Detailed.class);
            } catch (IOException e) {
                throw new RuntimeException("Error reading ingredients from import file.", e);
            }
        }
        ImportContents importContents = new ImportContents();
        importContents.setRecipes(recipes);
        importContents.setCategories(new ArrayList<>(categories.values()));
        importContents.setGlasses(new ArrayList<>(glasses.values()));
        importContents.setIngredients(ingredients);
        importContents.setCollections(collections);
        importContents.setImportId(importId);
        return importContents;

    }

    private void deleteImport(long importId) {
        Path path = importPaths.remove(importId);
        if (path != null) {
            deleteDirectory(path);
        }
    }

    private void deleteDirectory(Path path) {
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }

    public byte[] generateExport(ExportRequest exportRequest) throws IOException {
        List<Collection> collectionsToExport;
        if (exportRequest.isExportAllCollections()) {
            collectionsToExport = collectionService.getAll();
        } else {
            collectionsToExport = new ArrayList<>();
            for (Long collectionId : exportRequest.getExportCollectionIds()) {
                Collection collection = collectionService.getCollectionById(collectionId);
                if (collection != null) {
                    collectionsToExport.add(collection);
                }
            }
        }
        List<Recipe> recipesToExportAll = new ArrayList<>();
        for (Collection collection : collectionsToExport) {
            Sort sort = Sort.by(Sort.Direction.ASC, "lower(name)");
            int pageNr = 0;
            Page<Recipe> collectionPage = recipeService.getRecipesByFilter(null, collection.getId(), null,null,
                    null, RecipeService.FabricableFilter.ALL, pageNr, 16, sort);
            recipesToExportAll.addAll(collectionPage.getContent());
            while (!collectionPage.isLast()) {
                pageNr++;
                collectionPage = recipeService.getRecipesByFilter(null, collection.getId(), null, null,
                        null, RecipeService.FabricableFilter.ALL, pageNr, 16, sort);
                recipesToExportAll.addAll(collectionPage.getContent());
            }
        }
        List<CollectionDto.Response.Detailed> collectionDtos = collectionsToExport.stream()
                .map(CollectionDto.Response.Detailed::new)
                .toList();

        recipesToExportAll.addAll(exportRequest.isExportAllRecipes()
                ? recipeService.getAll()
                : recipeService.getByIds(exportRequest.getExportRecipeIds().toArray(new Long[0])));
        Set<Long> recipeIds = new HashSet<>();

        Set<Recipe> recipesToExport = new HashSet<>();
        for (Recipe recipe : recipesToExportAll) {
            if (recipeIds.add(recipe.getId())) {
                recipesToExport.add(recipe);
            }
        }


        List<RecipeDto.Response.Detailed> recipeDtos = recipesToExport.stream()
                .map(RecipeDto.Response.Detailed::toDto)
                .toList();

        Map<Long, Ingredient> ingredientsToExport = new HashMap<>();
        for (Recipe recipe : recipesToExport) {
            for (ProductionStep ps : recipe.getProductionSteps()) {
                if (!(ps instanceof AddIngredientsProductionStep)) {
                    continue;
                }
                AddIngredientsProductionStep aiPs = (AddIngredientsProductionStep) ps;
                for (ProductionStepIngredient psi : aiPs.getStepIngredients()) {
                    Ingredient current = psi.getIngredient();
                    while (current.getParentGroup() != null) {
                        current = current.getParentGroup();
                    }
                    ingredientsToExport.put(current.getId(), current);
                    List<Ingredient> work = new ArrayList<>();
                    work.add(current);
                    while (!work.isEmpty()) {
                        current = work.remove(0);
                        ingredientsToExport.put(current.getId(), current);
                        if (current instanceof IngredientGroup group) {
                            for (Ingredient child : group.getChildren()) {
                                if (!ingredientsToExport.containsKey(child.getId())) {
                                    work.add(child);
                                }
                            }
                        }

                    }
                }
            }
        }
        List<IngredientDto.Response.Detailed> ingredientDtos = ingredientsToExport.values().stream()
                .map(IngredientDto.Response.Detailed::toDto)
                .toList();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry("recipes.json"));
            ObjectMapper mapper = new ObjectMapper();
            byte[] jsonBytes = mapper.writeValueAsBytes(recipeDtos);
            zos.write(jsonBytes);
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry("ingredients.json"));
            jsonBytes = mapper.writeValueAsBytes(ingredientDtos);
            zos.write(jsonBytes);
            zos.closeEntry();

            for (RecipeDto.Response.Detailed dto : recipeDtos) {
                if (dto.isHasImage()) {
                    long recipeId = dto.getId();
                    byte[] image = recipeService.getImage(recipeId);
                    if (image != null) {
                        zos.putNextEntry(new ZipEntry("recipe_images/" + recipeId + ".jpg"));
                        zos.write(image);
                        zos.closeEntry();
                    }
                }
            }
            for (IngredientDto.Response.Detailed dto : ingredientDtos) {
                if (dto instanceof AddableIngredientDto.Response.Detailed addDto && addDto.isHasImage()) {
                    long ingredientId = addDto.getId();
                    byte[] image = ingredientService.getImage(ingredientId);
                    if (image != null) {
                        zos.putNextEntry(new ZipEntry("ingredient_images/" + ingredientId + ".jpg"));
                        zos.write(image);
                        zos.closeEntry();
                    }
                }
            }

            zos.putNextEntry(new ZipEntry("collections.json"));
            jsonBytes = mapper.writeValueAsBytes(collectionDtos);
            zos.write(jsonBytes);
            zos.closeEntry();

            for (CollectionDto.Response.Detailed dto : collectionDtos) {
                if (dto.isHasImage()) {
                    long collectionId = dto.getId();
                    byte[] image = collectionService.getImage(collectionId);
                    if (image != null) {
                        zos.putNextEntry(new ZipEntry("collection_images/" + collectionId + ".jpg"));
                        zos.write(image);
                        zos.closeEntry();
                    }
                }
            }

            zos.putNextEntry(new ZipEntry("schema.json"));
            String version = systemService.getVersion().getVersion();
            String schemaJson = "{\"version\": \"" + version + "\"}";
            zos.write(schemaJson.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
        return baos.toByteArray();
    }
}