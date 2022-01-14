package net.alex9849.cocktailmaker.payload.dto.eventaction;

import net.alex9849.cocktailmaker.model.eventaction.ExecutePythonEventAction;
import org.springframework.beans.BeanUtils;

public class ExecutePythonEventActionDto extends FileEventActionDto {

    public ExecutePythonEventActionDto() {}

    public ExecutePythonEventActionDto(ExecutePythonEventAction eventAction) {
        BeanUtils.copyProperties(eventAction, this);
    }

    @Override
    public String getType() {
        return "execPy";
    }
}
