package net.alex9849.cocktailmaker.payload.dto.eventaction;

import lombok.*;
import net.alex9849.cocktailmaker.model.eventaction.PlayAudioEventAction;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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
        public static class Detailed extends FileEventActionDto.Response.Detailed {

            protected Detailed(PlayAudioEventAction eventAction) {
                super(eventAction);
            }

            public String getType() {
                return "playAudio";
            }
        }
    }
}
