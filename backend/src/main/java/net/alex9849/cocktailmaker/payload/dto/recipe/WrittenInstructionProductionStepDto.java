package net.alex9849.cocktailmaker.payload.dto.recipe;

import net.alex9849.cocktailmaker.model.recipe.WrittenInstructionProductionStep;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class WrittenInstructionProductionStepDto implements ProductionStepDto {
    @NotNull
    @Size(min = 1, max = 500)
    private String message;

    public WrittenInstructionProductionStepDto(WrittenInstructionProductionStep writtenInstructionProductionStep) {
        BeanUtils.copyProperties(writtenInstructionProductionStep, this);
    }

    public WrittenInstructionProductionStepDto() {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getType() {
        return "writtenInstruction";
    }
}
