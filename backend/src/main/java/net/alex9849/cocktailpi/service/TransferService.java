package net.alex9849.cocktailpi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.alex9849.cocktailpi.model.recipe.Recipe;
import net.alex9849.cocktailpi.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailpi.payload.request.ExportRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.zip.*;

@Service
public class TransferService {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final RecipeService recipeService;

    public TransferService(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    public Path newImport(MultipartFile zipFile) throws IOException {
        Path tempDir = Files.createTempDirectory("import_");

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
            deleteDirectory(tempDir);
            throw new IOException("Error unpacking ZIP file.", e);
        }

        if (!Files.exists(tempDir.resolve("recipes.json")) ||
                !Files.exists(tempDir.resolve("ingredients.json")) ||
                !Files.isDirectory(tempDir.resolve("images"))) {
            deleteDirectory(tempDir);
            throw new IOException("Incorrect format.");
        }
        scheduler.schedule(() -> deleteDirectory(tempDir), 10, TimeUnit.MINUTES);
        return tempDir;
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
        List<Recipe> recipesToExport = exportRequest.isExportAllRecipes()
                ? recipeService.getAll()
                : recipeService.getByIds(exportRequest.getExportRecipeIds().toArray(new Long[0]));

        List<RecipeDto.Response.Detailed> recipeDtos = recipesToExport.stream()
                .map(RecipeDto.Response.Detailed::toDto)
                .toList();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry("recipes.json"));
            ObjectMapper mapper = new ObjectMapper();
            byte[] jsonBytes = mapper.writeValueAsBytes(recipeDtos);
            zos.write(jsonBytes);
            zos.closeEntry();

            for (RecipeDto.Response.Detailed dto : recipeDtos) {
                if (dto.isHasImage()) {
                    long recipeId = dto.getId();
                    byte[] image = recipeService.getImage(recipeId);
                    if (image != null) {
                        zos.putNextEntry(new ZipEntry("images/" + recipeId + ".jpg"));
                        zos.write(image);
                        zos.closeEntry();
                    }
                }
            }
        }
        return baos.toByteArray();
    }
}