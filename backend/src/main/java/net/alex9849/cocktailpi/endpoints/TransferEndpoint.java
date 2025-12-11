package net.alex9849.cocktailpi.endpoints;

import net.alex9849.cocktailpi.model.transfer.ExportContents;
import net.alex9849.cocktailpi.model.transfer.ImportConfirmRequest;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.payload.dto.recipe.RecipeDto;
import net.alex9849.cocktailpi.payload.request.ExportRequest;
import net.alex9849.cocktailpi.service.RecipeService;
import net.alex9849.cocktailpi.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transfer/")
public class TransferEndpoint {

    private final TransferService transferService;
    private final RecipeService recipeService;

    public TransferEndpoint(TransferService transferService, RecipeService recipeService) {
        this.transferService = transferService;
        this.recipeService = recipeService;
    }

    @RequestMapping(value = {"import"}, method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> uploadImport(@RequestPart(value = "file", required = false) MultipartFile importFile, UriComponentsBuilder uriBuilder) throws IOException {
        long id = transferService.newImport(importFile);
        ExportContents exportContents = transferService.readExport(id);
        UriComponents uriComponents = uriBuilder.path("/api/transfer/import/{id}").buildAndExpand(exportContents.getImportId());
        return ResponseEntity.created(uriComponents.toUri()).body(exportContents);
    }

    @RequestMapping(value = {"import/{id}"}, method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> confirmImport(
            @PathVariable("id") long importId,
            @RequestBody ImportConfirmRequest importRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        transferService.confirmImport(importId, user, importRequest);
        return ResponseEntity.ok().build();
    }


    @RequestMapping(value = {"export"}, method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> startExport(@RequestBody ExportRequest exportRequest) throws IOException {
        byte[] zipBytes = transferService.generateExport(exportRequest);
        String filename = "cocktailpi_export_" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".zip";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .header("Content-Type", "application/zip")
                .body(zipBytes);
    }

    @RequestMapping(value = {"export/recipes"}, method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getRecipes() throws IOException {
        List<RecipeDto.Response.Detailed> allRecipes = recipeService.getAll().stream().map(RecipeDto.Response.Detailed::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(allRecipes);
    }
}
