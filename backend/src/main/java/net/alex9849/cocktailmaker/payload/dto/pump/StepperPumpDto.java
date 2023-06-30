package net.alex9849.cocktailmaker.payload.dto.pump;

import lombok.*;
import net.alex9849.cocktailmaker.model.pump.StepperPump;
import net.alex9849.cocktailmaker.model.recipe.ingredient.AddableIngredient;
import net.alex9849.cocktailmaker.payload.dto.recipe.ingredient.AddableIngredientDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.ingredient.IngredientDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StepperPumpDto {
    private interface EnablePin { @Min(0) @Max(31) Integer getEnablePin(); }
    private interface StepPin { @Min(0) @Max(31) Integer getStepPin(); }
    private interface StepsPerCl { @Min(1) Integer getStepsPerCl(); }
    private interface MinStepDeltaInMs { @Min(1) Integer getMinStepDeltaInMs(); }
    private interface Acceleration { @Min(1) Integer getAcceleration(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Patch extends PumpDto.Request.Patch implements EnablePin, StepPin, StepsPerCl,
                MinStepDeltaInMs, Acceleration {
            Integer enablePin;
            Integer stepPin;
            Integer stepsPerCl;
            Integer minStepDeltaInMs;
            Integer acceleration;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter
        @Setter
        @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends PumpDto.Response.Detailed implements EnablePin, StepPin, StepsPerCl,
                MinStepDeltaInMs, Acceleration {
            Integer enablePin;
            Integer stepPin;
            Integer stepsPerCl;
            Integer minStepDeltaInMs;
            Integer acceleration;

            protected Detailed(StepperPump stepperPump) {
                super(stepperPump);
            }
        }
    }
}
