package net.alex9849.cocktailmaker.payload.dto.eventaction;

import net.alex9849.cocktailmaker.model.eventaction.PlayAudioEventAction;
import org.springframework.beans.BeanUtils;

public class PlayAudioEventActionDto extends FileEventActionDto {
    private boolean onRepeat;

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

    @Override
    public String getType() {
        return "playAudio";
    }
}
