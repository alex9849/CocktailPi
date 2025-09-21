package net.alex9849.cocktailpi.service;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.zip.*;

@Service
@Transactional
public class TransferService {
    private ReadWriteLock runningImportLock = new ReentrantReadWriteLock();
    private final GlassService glassService;
    private final CategoryService categoryService;
    private Map<Long, Path> exportPaths = new HashMap<>();
    private long lastExportId = 0;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    private final CollectionService collectionService;
    private final SystemService systemService;


    @Value("${alex9849.app.demoMode}")
    private boolean isDemoMode;

    public TransferService(RecipeService recipeService, IngredientService ingredientService, CollectionService collectionService, SystemService systemService, GlassService glassService, CategoryService categoryService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.collectionService = collectionService;
        this.systemService = systemService;
        this.glassService = glassService;
        this.categoryService = categoryService;
    }

    public long newImport(MultipartFile zipFile) throws IOException {
        if(isDemoMode) {
            throw new IllegalArgumentException("Can't perform import in demomode!");
        }

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
        Lock readLock = runningImportLock.readLock();
        try {
            readLock.lock();
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

            Path collectionsRecipesFile = importPath.resolve("collections_recipes.json");
            Map<Long, List<Long>> collectionRecipes = new HashMap<>();
            if (Files.exists(collectionsRecipesFile)) {
                try (InputStream is = Files.newInputStream(collectionsRecipesFile)) {
                    collectionRecipes = new ObjectMapper().readValue(is, new TypeReference<>() {});
                } catch (IOException e) {
                    throw new RuntimeException("Error reading collections from import file.", e);
                }
            }

            recipes.sort(Comparator.comparing(RecipeDto.Response.Detailed::getName));
            collections.sort(Comparator.comparing(CollectionDto.Response.Detailed::getName));
            ExportContents exportContents = new ExportContents();
            exportContents.setRecipes(recipes);
            exportContents.setCategories(new ArrayList<>(categories.values()));
            exportContents.setGlasses(new ArrayList<>(glasses.values()));
            exportContents.setIngredients(ingredients);
            exportContents.setCollections(collections);
            exportContents.setCollectionRecipes(collectionRecipes);
            exportContents.setImportId(importId);
            return exportContents;
        } finally {
            readLock.unlock();
        }
    }

    private void deleteImport(long importId) {
        Lock writeLock = runningImportLock.writeLock();
        try {
            writeLock.lock();
            Path path = exportPaths.remove(importId);
            if (path != null) {
                deleteDirectory(path);
            }
        } finally {
            writeLock.unlock();
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
        if(isDemoMode) {
            throw new IllegalArgumentException("Can't perform export in demomode!");
        }

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
        Map<Long, List<Long>> collectionRecipeMap = new HashMap<>();
        for (Collection collection : collectionsToExport) {
            collectionRecipeMap.put(collection.getId(), new ArrayList<>());
            Sort sort = Sort.by(Sort.Direction.ASC, "lower(name)");
            int pageNr = 0;
            Page<Recipe> collectionPage = recipeService.getRecipesByFilter(null, collection.getId(), null,null,
                    null, RecipeService.FabricableFilter.ALL, pageNr, 16, sort);
            recipesToExportAll.addAll(collectionPage.getContent());
            collectionRecipeMap.get(collection.getId())
                    .addAll(collectionPage.getContent().stream()
                            .mapToLong(Recipe::getId).boxed().toList());
            while (!collectionPage.isLast()) {
                pageNr++;
                collectionPage = recipeService.getRecipesByFilter(null, collection.getId(), null, null,
                        null, RecipeService.FabricableFilter.ALL, pageNr, 16, sort);
                recipesToExportAll.addAll(collectionPage.getContent());
                collectionRecipeMap.get(collection.getId())
                        .addAll(collectionPage.getContent().stream()
                                .mapToLong(Recipe::getId).boxed().toList());
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

            zos.putNextEntry(new ZipEntry("collections_recipes.json"));
            jsonBytes = mapper.writeValueAsBytes(collectionRecipeMap);
            zos.write(jsonBytes);
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry("schema.json"));
            String version = systemService.getVersion().getVersion();
            String schemaJson = "{\"version\": \"" + version + "\"}";
            zos.write(schemaJson.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
        return baos.toByteArray();
    }

    public byte[] readImageFromExport(long importId, String subFolder, long entryId) {
        Path importPath = exportPaths.get(importId);
        if (importPath == null) {
            throw new IllegalArgumentException("Import with ID " + importId + " does not exist.");
        }
        Path imagePath = importPath.resolve(subFolder + "/" + entryId + ".jpg");
        if (!Files.exists(imagePath)) {
            return null;
        }
        try {
            byte[] imgBytes = Files.readAllBytes(imagePath);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(imgBytes)) {
                BufferedImage image = ImageIO.read(bais);
                if (image == null) {
                    return null;
                }
                return imgBytes;
            }
        } catch (IOException e) {
            return null;
        }


    }

    public void confirmImport(long importId, User importUser, ImportConfirmRequest importRequest) {
        Lock readLock = runningImportLock.readLock();
        try {
            readLock.lock();
            ExportContents exportContents = readExport(importId);
            List<RecipeDto.Response.Detailed> recipesInExport = exportContents.getRecipes();
            Set<Long> recipeIdsToImport = recipesInExport.stream()
                    .map(RecipeDto.Response.Detailed::getId)
                    .collect(Collectors.toSet());
            if (!importRequest.isImportAllRecipes()) {
                recipeIdsToImport = recipesInExport.stream()
                        .map(RecipeDto.Response.Detailed::getId)
                        .filter(id -> importRequest.getImportRecipeIds().contains(id))
                        .collect(Collectors.toSet());
            }
            List<CollectionDto.Response.Detailed> collectionsToImport = exportContents.getCollections();
            if (!importRequest.isImportAllCollections()) {
                collectionsToImport = collectionsToImport.stream()
                        .filter(collection -> importRequest.getImportCollectionIds().contains(collection.getId()))
                        .toList();
            }
            for (CollectionDto.Response.Detailed collection : collectionsToImport) {
                List<Long> recipeIds = exportContents.getCollectionRecipes().getOrDefault(collection.getId(), List.of());
                recipeIdsToImport.addAll(recipeIds);
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

            for (RecipeDto.Response.Detailed recipeDto : recipesInExport) {
                if (!recipeIdsToImport.contains(recipeDto.getId())) {
                    continue;
                }
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
            Map<Long, Long> oldRecipeToNewRecipeIdMap = new HashMap<>();

            for (int i = 0; i < glassesToImport.size(); i++) {
                GlassDto.Duplex.Detailed dto = glassesToImport.get(i);
                Glass glass = glassService.fromDto(dto);
                Glass existingGlass = glassService.getByName(glass.getName());
                if (!importRequest.isImportAllGlasses()) {
                    if (existingGlass != null) {
                        oldGlassedToNewGlassIdMap.put(dto.getId(), existingGlass.getId());
                    }
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
                        dto.setName(dto.getName() + " (imported)");
                        glassesToImport.add(dto);
                    }
                } else {
                    Glass newGlass = glassService.createGlass(glass);
                    oldGlassedToNewGlassIdMap.put(dto.getId(), newGlass.getId());
                }
            }

            for (int i = 0; i < categoriesToImport.size(); i++) {
                CategoryDto.Duplex.Detailed dto = categoriesToImport.get(i);
                CategoryDto.Request.Create dtoCreate = new CategoryDto.Request.Create(dto);
                Category category = categoryService.fromDto(dtoCreate);
                category.setId(dto.getId());
                Category existingCategory = categoryService.getCategoryByName(dto.getName());
                if (!importRequest.isImportAllCategories()) {
                    if (existingCategory != null) {
                        oldCategoryToNewCategoryIdMap.put(dto.getId(), existingCategory.getId());
                    }
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
                        dto.setName(dto.getName() + " (imported)");
                        categoriesToImport.add(dto);
                    }
                } else {
                    Category newCategory = categoryService.createCategory(category);
                    oldCategoryToNewCategoryIdMap.put(dto.getId(), newCategory.getId());
                }
            }
            for (int i = 0; i < ingredientsToImport.size(); i++) {
                IngredientDto.Response.Detailed dto = ingredientsToImport.get(i);
                byte[] image = readImageFromExport(importId, "ingredient_images", dto.getId());
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
                        ingredientService.setImage(ingredient.getId(), image);
                        oldIngredientToNewIngredientIdMap.put(dto.getId(), existingIngredient.getId());
                    } else if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.SKIP) {
                        oldIngredientToNewIngredientIdMap.put(dto.getId(), existingIngredient.getId());
                    } else if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.KEEP_BOTH) {
                        dto.setName(dto.getName() + " (imported)");
                        ingredientsToImport.add(i + 1, dto);
                    }
                } else {
                    Ingredient newIngredient = ingredientService.createIngredient(ingredient);
                    ingredientService.setImage(newIngredient.getId(), image);
                    oldIngredientToNewIngredientIdMap.put(dto.getId(), newIngredient.getId());
                }
            }

            for (int i = 0; i < recipesInExport.size(); i++) {
                RecipeDto.Response.Detailed dto = recipesInExport.get(i);
                RecipeDto.Request.Create createDto = new RecipeDto.Request.Create(dto);

                List<Recipe> existingRecipes = recipeService.getRecipeByName(createDto.getName());
                if (!recipeIdsToImport.contains(dto.getId())) {
                    if (!existingRecipes.isEmpty()) {
                        Recipe existingRecipe = existingRecipes.get(0);
                        oldRecipeToNewRecipeIdMap.put(dto.getId(), existingRecipe.getId());
                    }
                    continue;
                }

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
                if (!existingRecipes.isEmpty()) {
                    Recipe existingRecipe = existingRecipes.get(0);
                    if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.OVERWRITE) {
                        recipe.setId(existingRecipe.getId());
                        recipeService.updateRecipe(recipe);
                        byte[] image = readImageFromExport(importId, "recipe_images", dto.getId());
                        recipeService.setImage(recipe.getId(), image);
                        oldRecipeToNewRecipeIdMap.put(dto.getId(), existingRecipe.getId());
                    } else if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.SKIP) {
                        oldRecipeToNewRecipeIdMap.put(dto.getId(), existingRecipe.getId());
                    } else if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.KEEP_BOTH) {
                        dto.setName(dto.getName() + " (imported)");
                        recipesInExport.add(i + 1, dto);
                    }
                } else {
                    recipe = recipeService.createRecipe(recipe);
                    byte[] image = readImageFromExport(importId, "recipe_images", dto.getId());
                    recipeService.setImage(recipe.getId(), image);
                    oldRecipeToNewRecipeIdMap.put(dto.getId(), recipe.getId());
                }
            }

            for (int i = 0; i < collectionsToImport.size(); i++) {
                CollectionDto.Response.Detailed dto = collectionsToImport.get(i);
                byte[] image = readImageFromExport(importId, "collection_images", dto.getId());
                CollectionDto.Request.Create createDto = new CollectionDto.Request.Create(dto);
                Collection collection = collectionService.fromDto(createDto, importUser.getId());
                List<Collection> collectionsWithName = collectionService.getCollectionsByName(collection.getName());
                if (!collectionsWithName.isEmpty()) {
                    Collection existingCollection = collectionsWithName.get(0);
                    if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.OVERWRITE) {
                        collection.setId(existingCollection.getId());
                        collectionService.updateCollection(collection, image, image == null);
                    } else if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.SKIP) {
                        continue;
                    } else if (importRequest.getDuplicateMode() == ImportConfirmRequest.DuplicateMode.KEEP_BOTH) {
                        dto.setName(dto.getName() + " (imported)");
                        collectionsToImport.add(i + 1, dto);
                    }
                } else {
                    collection = collectionService.createCollection(collection);
                    collectionService.updateCollection(collection, image, image == null);
                }
                for (Long recipeId : exportContents.getCollectionRecipes().getOrDefault(dto.getId(), List.of())) {
                    Long newRecipeId = oldRecipeToNewRecipeIdMap.get(recipeId);
                    if (newRecipeId != null) {
                        collectionService.addRecipe(newRecipeId, collection.getId());
                    }
                }
            }
        } finally {
            readLock.unlock();
        }
    }

    private void collectIngredientTree(IngredientDto.Response.Detailed ingredient, Set<Long> visited, Map<Long, List<IngredientDto.Response.Detailed>> ingredientGroupMap, List<IngredientDto.Response.Detailed> result) {
        if (!visited.add(ingredient.getId())) {
            return;
        }
        result.add(ingredient);
        if (ingredient instanceof IngredientGroupDto.Response.Detailed group) {
            for (IngredientDto.Response.Detailed child : ingredientGroupMap.getOrDefault(group.getId(), new ArrayList<>())) {
                collectIngredientTree(child, visited, ingredientGroupMap, result);
            }
        }
    }
}
