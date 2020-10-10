package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.Category;
import net.alex9849.cocktailmaker.payload.dto.category.CategoryDto;
import net.alex9849.cocktailmaker.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/category")
public class CategoryEndpoint {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories()
                .stream().map(CategoryDto::new).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('Admin')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDto categoryDto, UriComponentsBuilder uriBuilder) {
        categoryDto.setId(null);
        Category category = categoryService.createCategory(categoryService.fromDto(categoryDto));
        UriComponents uriComponents = uriBuilder.path("/api/category/{id}").buildAndExpand(category.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PreAuthorize("hasRole('Admin')")
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCategory(@PathVariable(value = "id") long id, @Valid @RequestBody CategoryDto categoryDto) {
        categoryDto.setId(id);
        categoryService.updateCategory(categoryService.fromDto(categoryDto));
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('Admin')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCategory(@PathVariable(value = "id") long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

}
