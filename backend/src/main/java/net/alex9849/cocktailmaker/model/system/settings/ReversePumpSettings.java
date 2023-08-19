package net.alex9849.cocktailmaker.model.system.settings;

import net.alex9849.cocktailmaker.model.gpio.GpioBoard;

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
        private GpioBoard.Pin directorPin;
        private int overshoot;
        private int autoPumpBackTimer;

        public GpioBoard.Pin getDirectorPin() {
            return directorPin;
        }

        public void setDirectorPin(GpioBoard.Pin directorPin) {
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
