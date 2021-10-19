package net.alex9849.cocktailmaker.model.recipe;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("Instruction")
public class InstructionProductionStep implements ProductionStep {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
