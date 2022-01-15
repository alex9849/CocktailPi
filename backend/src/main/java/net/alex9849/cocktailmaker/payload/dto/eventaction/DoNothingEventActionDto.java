package net.alex9849.cocktailmaker.payload.dto.eventaction;

import net.alex9849.cocktailmaker.model.eventaction.DoNothingEventAction;
import org.springframework.beans.BeanUtils;

public class DoNothingEventActionDto extends EventActionDto {

    public DoNothingEventActionDto() {}

    public DoNothingEventActionDto(DoNothingEventAction eventAction) {
        BeanUtils.copyProperties(eventAction, this);
    }

    @Override
    public String getType() {
        return "doNothing";
    }
}
