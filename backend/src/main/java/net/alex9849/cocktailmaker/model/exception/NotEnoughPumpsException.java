package net.alex9849.cocktailmaker.model.exception;

public class NotEnoughPumpsException extends BadIngredientAllocation {

    public NotEnoughPumpsException(String message) {
        super(message);
    }
}
