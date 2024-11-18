package net.alex9849.cocktailpi.model.system.settings;

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

    public static class Config {
        private Pin directorPin;
        private int overshoot;
        private int autoPumpBackTimer;

        public Pin getDirectorPin() {
            return directorPin;
        }

        public void setDirectorPin(Pin directorPin) {
            this.directorPin = directorPin;
        }

        public int getOvershoot() {
            return overshoot;
        }

        public void setOvershoot(int overshoot) {
            this.overshoot = overshoot;
        }

        public int getAutoPumpBackTimer() {
            return autoPumpBackTimer;
        }

        public void setAutoPumpBackTimer(int autoPumpBackTimer) {
            this.autoPumpBackTimer = autoPumpBackTimer;
        }
    }


}
