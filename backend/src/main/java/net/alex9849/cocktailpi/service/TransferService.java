package net.alex9849.cocktailpi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.alex9849.cocktailpi.model.Category;
import net.alex9849.cocktailpi.model.Glass;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.model.recipe.ingredient.Ingredient;
import net.alex9849.cocktailpi.model.recipe.ingredient.IngredientGroup;
import net.alex9849.cocktailpi.model.recipe.productionstep.AddIngredientsProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStep;
import net.alex9849.cocktailpi.model.recipe.productionstep.ProductionStepIngredient;
import net.alex9849.cocktailpi.model.transfer.ExportContents;
import net.alex9849.cocktailpi.model.transfer.ImportConfirmRequest;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.payload.dto.category.CategoryDto;
import net.alex9849.cocktailpi.payload.dto.collection.CollectionDto;
import net.alex9849.cocktailpi.model.Collection;
import net.alex9849.cocktailpi.payload.dto.glass.GlassDto;
import net.alex9849.cocktailpi.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.AddableIngredientDto;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.IngredientDto;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.IngredientGroupDto;
import net.alex9849.cocktailpi.payload.dto.recipe.productionstep.AddIngredientsProductionStepDto;
import net.alex9849.cocktailpi.payload.dto.recipe.productionstep.ProductionStepDto;
import net.alex9849.cocktailpi.payload.dto.recipe.productionstep.ProductionStepIngredientDto;
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
import java.util.stream.Collectors;
import java.util.zip.*;

@Service
public class TransferService {
    private final GlassService glassService;
    private final CategoryService categoryService;
    private Map<Long, Path> exportPaths = new HashMap<>();
    private long lastExportId = 0;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    private final CollectionService collectionService;
    private final SystemService systemService;

    public TransferService(RecipeService recipeService, IngredientService ingredientService, CollectionService collectionService, SystemService systemService, GlassService glassService, CategoryService categoryService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.collectionService = collectionService;
        this.systemService = systemService;
        this.glassService = glassService;
        this.categoryService = categoryService;
    }

    public long newImport(MultipartFile zipFile) throws IOException {
        Path tempDir = Files.createTempDirectory("import_");
        lastExportId++;
        long importId = lastExportId;
        exportPaths.put(importId, tempDir);

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

    public ExportContents readExport(long importId) {
        Path importPath = exportPaths.get(importId);
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
                throw new RuntimeException("Error reading collections from import file.", e);
            }
        }
        ExportContents exportContents = new ExportContents();
        exportContents.setRecipes(recipes);
        exportContents.setCategories(new ArrayList<>(categories.values()));
        exportContents.setGlasses(new ArrayList<>(glasses.values()));
        exportContents.setIngredients(ingredients);
        exportContents.setCollections(collections);
        exportContents.setImportId(importId);
        return exportContents;

    }

    private void deleteImport(long importId) {
        Path path = exportPaths.remove(importId);
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

    public void confirmImport(long importId, User importUser, ImportConfirmRequest importRequest) {
        ExportContents exportContents = readExport(importId);
        List<RecipeDto.Response.Detailed> recipesToImport = exportContents.getRecipes();
        if (!importRequest.isImportAllRecipes()) {
            recipesToImport = recipesToImport.stream()
                    .filter(recipe -> importRequest.getImportRecipeIds().contains(recipe.getId()))
                    .toList();
        }
        List<CollectionDto.Response.Detailed> collectionsToImport = exportContents.getCollections();
        if (!importRequest.isImportAllCollections()) {
            collectionsToImport = collectionsToImport.stream()
                    .filter(collection -> importRequest.getImportCollectionIds().contains(collection.getId()))
                    .toList();
        }
        List<GlassDto.Duplex.Detailed> glassesToImport = exportContents.getGlasses();
        List<CategoryDto.Duplex.Detailed> categoriesToImport = exportContents.getCategories();



        List<IngredientDto.Response.Detailed> ingredientsToImport = new ArrayList<>();
        Set<Long> seenIngredientIds = new HashSet<>();
        Map<Long, IngredientDto.Response.Detailed> ingredientMap = exportContents.getIngredients()
                .stream().collect(Collectors.toMap(IngredientDto.Response.Detailed::getId, v -> v));
        Map<Long, List<IngredientDto.Response.Detailed>> ingredientGroupMap = exportContents.getIngredients()
                .stream().filter(ing -> ing.getParentGroupId() != null)
                .collect(Collectors.groupingBy(IngredientDto.Response.Detailed::getParentGroupId, Collectors.toList()));

        for (RecipeDto.Response.Detailed recipeDto : recipesToImport) {
            for(ProductionStepDto.Response.Detailed stepDto : recipeDto.getProductionSteps()) {
                if (!(stepDto instanceof AddIngredientsProductionStepDto.Response.Detailed addStep)) {
                    continue;
                }
                for (ProductionStepIngredientDto.Response.Detailed ingredientDto : addStep.getStepIngredients()) {
                    IngredientDto.Response.Detailed root = ingredientDto.getIngredient();
                    while (root.getParentGroupId() != null) {
                        root = ingredientMap.get(root.getParentGroupId());
                    }
                    collectIngredientTree(root, seenIngredientIds, ingredientGroupMap, ingredientsToImport);
                }
            }
        }

        Map<Long, Long> oldGlassedToNewGlassIdMap = new HashMap<>();
        Map<Long, Long> oldCategoryToNewCategoryIdMap = new HashMap<>();
        Map<Long, Long> oldIngredientToNewIngredientIdMap = new HashMap<>();

        for (int i = 0; i < glassesToImport.size(); i++) {
            GlassDto.Duplex.Detailed dto = glassesToImport.get(i);
            Glass glass = glassService.fromDto(dto);
            Glass existingGlass = glassService.getByName(glass.getName());
            if (!importRequest.isImportAllGlasses()) {
                oldGlassedToNewGlassIdMap.put(dto.getId(), existingGlass.getId());
                continue;
            }
            if (existingGlass != null) {
                if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.OVERWRITE) {
                    glass.setId(existingGlass.getId());
                    glassService.updateGlass(glass);
                    oldGlassedToNewGlassIdMap.put(dto.getId(), existingGlass.getId());
                } else if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.SKIP) {
                    oldGlassedToNewGlassIdMap.put(dto.getId(), existingGlass.getId());
                } else if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.KEEP_BOTH) {
                    glass.setName(glass.getName() + " (imported)");
                    glassesToImport.add(new GlassDto.Duplex.Detailed(glass));
                }
            } else {
                Glass newGlass = glassService.createGlass(glass);
                oldGlassedToNewGlassIdMap.put(glass.getId(), newGlass.getId());
            }
        }

        for (int i = 0; i < categoriesToImport.size(); i++) {
            CategoryDto.Duplex.Detailed dto = categoriesToImport.get(i);
            CategoryDto.Request.Create dtoCreate = new CategoryDto.Request.Create(dto);
            Category category = categoryService.fromDto(dtoCreate);
            category.setId(dto.getId());
            Category existingCategory = categoryService.getCategoryByName(dto.getName());
            if (!importRequest.isImportAllCategories()) {
                oldCategoryToNewCategoryIdMap.put(dto.getId(), existingCategory.getId());
                continue;
            }
            if (existingCategory != null) {
                if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.OVERWRITE) {
                    category.setId(existingCategory.getId());
                    categoryService.updateCategory(category);
                    oldCategoryToNewCategoryIdMap.put(dto.getId(), existingCategory.getId());
                } else if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.SKIP) {
                    oldCategoryToNewCategoryIdMap.put(dto.getId(), existingCategory.getId());
                } else if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.KEEP_BOTH) {
                    category.setName(category.getName() + " (imported)");
                    categoriesToImport.add(new CategoryDto.Duplex.Detailed(category));
                }
            } else {
                categoryService.createCategory(category);
                oldCategoryToNewCategoryIdMap.put(dto.getId(), dto.getId());
            }
        }
        for (int i = 0; i < ingredientsToImport.size(); i++) {
            IngredientDto.Response.Detailed dto = ingredientsToImport.get(i);
            IngredientDto.Request.Create dtoCreate = IngredientDto.Request.Create.fromDetailedDto(dto);
            Ingredient ingredient = ingredientService.fromDto(dtoCreate);
            Ingredient existingIngredient = ingredientService.getByName(ingredient.getName());
            if (existingIngredient != null) {
                if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.OVERWRITE) {
                    // We cannot change the type of an ingredient from group to non-group or vice versa.
                    if (existingIngredient instanceof IngredientGroup || ingredient instanceof IngredientGroup) {
                        if (!(existingIngredient instanceof IngredientGroup) || !(ingredient instanceof IngredientGroup)) {
                            ingredient.setName(ingredient.getName() + " (imported)");
                            ingredientsToImport.add(i + 1, IngredientDto.Response.Detailed.toDto(ingredient));
                            continue;
                        }
                    }
                    ingredient.setId(existingIngredient.getId());
                    ingredientService.updateIngredient(ingredient);
                    oldIngredientToNewIngredientIdMap.put(dto.getId(), existingIngredient.getId());
                } else if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.SKIP) {
                    oldIngredientToNewIngredientIdMap.put(dto.getId(), existingIngredient.getId());
                } else if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.KEEP_BOTH) {
                    ingredient.setName(ingredient.getName() + " (imported)");
                    ingredientsToImport.add(i + 1, IngredientDto.Response.Detailed.toDto(ingredient));
                }
            } else {
                Ingredient newIngredient = ingredientService.createIngredient(ingredient);
                oldIngredientToNewIngredientIdMap.put(dto.getId(), newIngredient.getId());
            }
        }

        for (int i = 0; i < recipesToImport.size(); i++) {
            RecipeDto.Response.Detailed dto = recipesToImport.get(i);
            RecipeDto.Request.Create createDto = new RecipeDto.Request.Create(dto);

            if (dto.getDefaultGlass() != null) {
                Long newGlassId = oldGlassedToNewGlassIdMap.get(dto.getDefaultGlass().getId());
                createDto.setDefaultGlassId(newGlassId);
            }
            Set<Long> newCategoryIds = new HashSet<>();

            for (CategoryDto.Duplex.Detailed category : dto.getCategories()) {
                Long newCategoryId = oldCategoryToNewCategoryIdMap.get(category.getId());
                if (newCategoryId != null) {
                    newCategoryIds.add(newCategoryId);
                }
            }
            createDto.setCategoryIds(newCategoryIds);

            for (ProductionStepDto.Request.Create ps : createDto.getProductionSteps()) {
                if (!(ps instanceof AddIngredientsProductionStepDto.Request.Create aiPs)) {
                    continue;
                }
                for (ProductionStepIngredientDto.Request.Create psi : aiPs.getStepIngredients()) {
                    Long newIngredientId = oldIngredientToNewIngredientIdMap.get(psi.getIngredientId());
                    psi.setIngredientId(newIngredientId);
                }
            }
            createDto.setOwnerId(importUser.getId());
            Recipe recipe = recipeService.fromDto(createDto);
            List<Recipe> existingRecipes = recipeService.getRecipeByName(createDto.getName());
            if (!existingRecipes.isEmpty()) {
                Recipe existingRecipe = existingRecipes.get(0);
                if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.OVERWRITE) {
                    recipe.setId(existingRecipe.getId());
                    recipeService.updateRecipe(recipe);
                } else if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.SKIP) {
                    continue;
                } else if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.KEEP_BOTH) {
                    recipe.setName(recipe.getName() + " (imported)");
                    recipesToImport.add(i + 1, RecipeDto.Response.Detailed.toDto(recipe));
                    continue;
                }
            } else {
                recipeService.createRecipe(recipe);
            }
        }

    }

    private void collectIngredientTree(IngredientDto.Response.Detailed ingredient, Set<Long> visited, Map<Long, List<IngredientDto.Response.Detailed>> ingredientGroupMap, List<IngredientDto.Response.Detailed> result) {
        if (!visited.add(ingredient.getId())) {
            return;
        }
        result.add(ingredient);
        if (ingredient instanceof IngredientGroupDto.Response.Detailed group) {
            for (IngredientDto.Response.Detailed child : ingredientGroupMap.get(group.getId())) {
                collectIngredientTree(child, visited, ingredientGroupMap, result);
            }
        }
    }
}