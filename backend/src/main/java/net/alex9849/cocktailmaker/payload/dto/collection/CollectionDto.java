package net.alex9849.cocktailmaker.payload.dto.collection;

import net.alex9849.cocktailmaker.model.Collection;
import net.alex9849.cocktailmaker.payload.dto.OwnerDto;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CollectionDto {
    private long id;

    @NotNull
    @Size(min = 3, max = 20)
    private String name;

    @NotNull
    @Size(max = 20)
    private String description;
    private boolean completed;
    private boolean hasImage;
    private OwnerDto owner;
    private int size;

    public CollectionDto() {}

    public CollectionDto(Collection collection) {
        BeanUtils.copyProperties(collection, this);
        this.owner = new OwnerDto(collection.getOwner());
    }

    public boolean hasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
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
