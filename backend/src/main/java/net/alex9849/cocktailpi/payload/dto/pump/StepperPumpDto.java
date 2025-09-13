package net.alex9849.cocktailpi.payload.dto.pump;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import net.alex9849.cocktailpi.model.pump.StepperPump;
import net.alex9849.cocktailpi.payload.dto.gpio.PinDto;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StepperPumpDto {

    private interface EnablePinResponse { PinDto.Response.PumpPin getEnablePin(); }
    private interface EnablePinRequest { PinDto.Request.Select getEnablePin(); }
    private interface StepPinResponse { PinDto.Response.PumpPin getStepPin(); }
    private interface StepPinRequest { PinDto.Request.Select getStepPin(); }
    private interface StepsPerCl { @Min(1) Integer getStepsPerCl(); }
    private interface MaxStepsPerSecond { @Min(1) @Max(500000) Integer getMaxStepsPerSecond(); }
    private interface Acceleration { @Min(1) @Max(500000) Integer getAcceleration(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter @Setter
        @EqualsAndHashCode(callSuper = true)
        @NoArgsConstructor(access = AccessLevel.PUBLIC)
        public static class Create extends PumpDto.Request.Create implements EnablePinRequest, StepPinRequest, StepsPerCl,
                MaxStepsPerSecond, Acceleration {
            PinDto.Request.Select enablePin;
            PinDto.Request.Select stepPin;
            Integer stepsPerCl;
            Integer maxStepsPerSecond;
            Integer acceleration;

            public Create(StepperPump pump) {
                super(pump);
                if(pump.getEnablePin() != null) {
                    enablePin = new PinDto.Request.Select(pump.getEnablePin());
                }
                if(pump.getStepPin() != null) {
                    stepPin = new PinDto.Request.Select(pump.getStepPin());
                }
                this.stepsPerCl = pump.getStepsPerCl();
                this.maxStepsPerSecond = pump.getMaxStepsPerSecond();
                this.acceleration = pump.getAcceleration();
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter
        @Setter
        @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends PumpDto.Response.Detailed implements EnablePinResponse, StepPinResponse, StepsPerCl,
                MaxStepsPerSecond, Acceleration {
            PinDto.Response.PumpPin enablePin;
            PinDto.Response.PumpPin stepPin;
            Integer stepsPerCl;
            Integer maxStepsPerSecond;
            Integer acceleration;

            protected Detailed(StepperPump stepperPump) {
                super(stepperPump);
                if(stepperPump.getEnablePin() != null) {
                    enablePin = new PinDto.Response.PumpPin(stepperPump.getEnablePin());
                }
                if(stepperPump.getStepPin() != null) {
                    stepPin = new PinDto.Response.PumpPin(stepperPump.getStepPin());
                }
            }

            @Override
            public String getType() {
                return "stepper";
            }
        }
    }
}
