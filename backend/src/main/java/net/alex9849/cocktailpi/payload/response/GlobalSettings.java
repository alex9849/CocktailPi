package net.alex9849.cocktailpi.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GlobalSettings {
    private boolean allowReversePumping;
    private Donation donation;
    private String projectName;
    private boolean isDisableUpdater;
    private boolean isHideProjectLinks;
    private boolean isHideDonationButton;

    @Getter @Setter
    public static class Donation {
        private boolean donated;
        private boolean showDisclaimer;
        private Integer disclaimerDelay;
    }
}
