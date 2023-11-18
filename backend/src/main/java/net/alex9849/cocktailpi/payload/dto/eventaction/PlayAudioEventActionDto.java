package net.alex9849.cocktailpi.payload.dto.eventaction;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.alex9849.cocktailpi.model.eventaction.PlayAudioEventAction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayAudioEventActionDto {
    private interface OnRepeat { boolean isOnRepeat(); }
    private interface Volume { @Min(0) @Max(100) int getVolume(); }
    private interface SoundDevice { @NotNull() String getSoundDevice(); }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Create extends FileEventActionDto.Request.Create implements OnRepeat, Volume, SoundDevice {
            boolean onRepeat;
            int volume;
            String soundDevice;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends FileEventActionDto.Response.Detailed implements OnRepeat, Volume, SoundDevice {
            boolean onRepeat;
            int volume;
            String soundDevice;

            protected Detailed(PlayAudioEventAction eventAction) {
                super(eventAction);
            }

            public String getType() {
                return "playAudio";
            }
        }
    }
}
