package net.alex9849.cocktailpi.endpoints;

import jakarta.validation.Valid;
import net.alex9849.cocktailpi.model.Category;
import net.alex9849.cocktailpi.payload.dto.category.CategoryDto;
import net.alex9849.cocktailpi.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/category/")
public class CategoryEndpoint {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories()
                .stream().map(CategoryDto.Duplex.Detailed::new).collect(Collectors.toList()));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getCategory(@PathVariable(value = "id") long id) {
        Category category = categoryService.getCategory(id);
        if(category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new CategoryDto.Duplex.Detailed(category));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDto.Request.Create categoryDto, UriComponentsBuilder uriBuilder) {
        Category category = categoryService.createCategory(categoryService.fromDto(categoryDto));
        UriComponents uriComponents = uriBuilder.path("/api/category/{id}").buildAndExpand(category.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(new CategoryDto.Duplex.Detailed(category));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCategory(@PathVariable(value = "id") long id, @Valid @RequestBody CategoryDto.Request.Create categoryDto) {
        Category oldCategory = categoryService.getCategory(id);
        if(oldCategory == null) {
            return ResponseEntity.notFound().build();
        }
        Category category = categoryService.fromDto(categoryDto);
        category.setId(id);
        categoryService.updateCategory(category);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCategory(@PathVariable(value = "id") long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

}
