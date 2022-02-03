package net.alex9849.cocktailmaker.payload.dto.eventaction;

import net.alex9849.cocktailmaker.model.eventaction.PlayAudioEventAction;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class PlayAudioEventActionDto extends FileEventActionDto {
    private boolean onRepeat;
    @Min(0) @Max(100)
    private int volume;

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

    @Override
    public String getType() {
        return "playAudio";
    }
}
