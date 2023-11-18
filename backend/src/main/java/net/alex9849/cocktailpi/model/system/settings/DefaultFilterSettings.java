package net.alex9849.cocktailpi.model.system.settings;

import net.alex9849.cocktailpi.service.RecipeService;

public class DefaultFilterSettings {
    private boolean enable;
    private Filter filter;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public static class Filter {
        private RecipeService.FabricableFilter fabricable;

        public RecipeService.FabricableFilter getFabricable() {
            return fabricable;
        }

        public void setFabricable(RecipeService.FabricableFilter fabricable) {
            this.fabricable = fabricable;
        }
    }
}
