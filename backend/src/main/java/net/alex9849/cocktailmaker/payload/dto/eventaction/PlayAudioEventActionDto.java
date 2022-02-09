package net.alex9849.cocktailmaker.payload.dto.eventaction;

import net.alex9849.cocktailmaker.model.eventaction.PlayAudioEventAction;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class PlayAudioEventActionDto extends FileEventActionDto {
    private boolean onRepeat;
    @Min(0) @Max(100)
    private int volume;
    @NotNull()
    private String soundDevice;

    public PlayAudioEventActionDto() {}

    public PlayAudioEventActionDto(PlayAudioEventAction eventAction) {
        BeanUtils.copyProperties(eventAction, this);
    }

    public boolean isOnRepeat() {
        return onRepeat;
    }

    public void setOnRepeat(boolean onRepeat) {
        this.onRepeat = onRepeat;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getSoundDevice() {
        return soundDevice;
    }

    public void setSoundDevice(String soundDevice) {
        this.soundDevice = soundDevice;
    }

    @Override
    public String getType() {
        return "playAudio";
    }
}
