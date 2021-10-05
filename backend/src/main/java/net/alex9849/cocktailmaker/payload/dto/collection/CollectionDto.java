package net.alex9849.cocktailmaker.payload.dto.collection;

import net.alex9849.cocktailmaker.model.Collection;
import net.alex9849.cocktailmaker.payload.dto.OwnerDto;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CollectionDto {
    private long id;

    @NotNull
    @Min(3)
    @Max(20)
    private String name;
    @NotNull
    @Max(2000)
    private String description;
    private boolean completed;
    private OwnerDto owner;
    private int size;

    public CollectionDto() {}

    public CollectionDto(Collection collection) {
        BeanUtils.copyProperties(collection, this);
        this.owner = new OwnerDto(collection.getOwner());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public OwnerDto getOwner() {
        return owner;
    }

    public void setOwner(OwnerDto owner) {
        this.owner = owner;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
