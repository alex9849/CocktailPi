package net.alex9849.cocktailpi.model.recipe.productionstep;


import jakarta.persistence.DiscriminatorValue;

@DiscriminatorValue("WrittenInstruction")
public class WrittenInstructionProductionStep implements ProductionStep {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
