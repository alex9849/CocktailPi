package net.alex9849.cocktailmaker.payload.dto.category;

import net.alex9849.cocktailmaker.model.Category;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CategoryDto {

    private Long id;

    @NotNull
    @Size(min = 1, max = 15)
    private String name;

    public CategoryDto() {}

    public CategoryDto(Category category) {
        BeanUtils.copyProperties(category, this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
