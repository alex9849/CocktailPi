package net.alex9849.cocktailpi.model;

import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.service.UserService;
import net.alex9849.cocktailpi.utils.SpringUtility;

import java.util.Date;

public class Collection {
    private long id;
    private String name;
    private boolean hasImage;
    private String description;
    private long ownerId;
    private User owner;
    private int size;
    private Date lastUpdate;

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public String getNormalName() {
        return SpringUtility.normalize(getName());
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        if(ownerId != this.ownerId) {
            this.owner = null;
        }
        this.ownerId = ownerId;
    }

    public User getOwner() {
        if(owner == null) {
            UserService uService = SpringUtility.getBean(UserService.class);
            owner = uService.getUser(ownerId);
        }
        return owner;
    }

    public void setOwner(User owner) {
        this.ownerId = owner.getId();
        this.owner = owner;
    }
}
