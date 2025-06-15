package net.alex9849.cocktailpi.model.system.settings;

import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailpi.model.gpio.Pin;

public class ReversePumpSettings {
    private boolean enable;
    private Config settings;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Config getSettings() {
        return settings;
    }

    public void setSettings(Config settings) {
        this.settings = settings;
    }

    @Getter @Setter
    public static class Config {
        private Pin directorPin;
        private int overshoot;
        private int autoPumpBackTimer;
        private boolean forwardStateHigh;
    }


}
