package net.alex9849.cocktailpi.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class ExportRequest {
    private boolean exportAllRecipes;
    private List<Long> exportRecipeIds;
    private boolean exportAllCollections;
    private List<Long> exportCollectionIds;
}
