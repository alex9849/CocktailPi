package net.alex9849.cocktailpi.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Glass {
    private long id;
    private String name;
    private long size;
    private Integer emptyWeight;
    private boolean isDefault;
    private boolean isUseForSingleIngredients;

}
