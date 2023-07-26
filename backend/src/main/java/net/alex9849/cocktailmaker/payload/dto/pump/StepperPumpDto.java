package net.alex9849.cocktailmaker.payload.dto.pump;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import net.alex9849.cocktailmaker.model.pump.StepperPump;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StepperPumpDto {
    private interface EnablePin {
        @Min(value = 0, message = "EnablePin needs to be between 0 and 31")
        @Max(value = 31, message = "EnablePin needs to be between 0 and 31") Integer getEnablePin();
    }
    private interface StepPin {
        @Min(value = 0, message = "StepPin needs to be between 0 and 31")
        @Max(value = 31, message = "StepPin needs to be between 0 and 31") Integer getStepPin();
    }
    private interface StepsPerCl { @Min(1) Integer getStepsPerCl(); }
    private interface MaxStepsPerSecond { @Min(1) @Max(500000) Integer getMaxStepsPerSecond(); }
    private interface Acceleration { @Min(1) @Max(500000) Integer getAcceleration(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter @Setter @EqualsAndHashCode(callSuper = true)
        public static class Create extends PumpDto.Request.Create implements EnablePin, StepPin, StepsPerCl,
                MaxStepsPerSecond, Acceleration {
            Integer enablePin;
            Integer stepPin;
            Integer stepsPerCl;
            Integer maxStepsPerSecond;
            Integer acceleration;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter
        @Setter
        @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends PumpDto.Response.Detailed implements EnablePin, StepPin, StepsPerCl,
                MaxStepsPerSecond, Acceleration {
            Integer enablePin;
            Integer stepPin;
            Integer stepsPerCl;
            Integer maxStepsPerSecond;
            Integer acceleration;

            protected Detailed(StepperPump stepperPump) {
                super(stepperPump);
            }

            @Override
            public String getType() {
                return "stepper";
            }
        }
    }
}
