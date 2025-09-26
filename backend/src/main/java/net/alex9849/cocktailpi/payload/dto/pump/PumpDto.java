package net.alex9849.cocktailpi.payload.dto.pump;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Min;
import lombok.*;
import net.alex9849.cocktailpi.model.pump.DcPump;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.pump.StepperPump;
import net.alex9849.cocktailpi.model.pump.Valve;
import net.alex9849.cocktailpi.payload.dto.recipe.ingredient.AutomatedIngredientDto;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.Set;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PumpDto {
    //Common
    private interface Id { long getId(); }
    private interface FillingLevelInMl { @Min(0) int getFillingLevelInMl(); }
    private interface PatchFillingLevelInMl { @Min(0) Integer getFillingLevelInMl(); }
    private interface PowerConsumption { @Min(0) int getPowerConsumption(); }
    private interface PatchPowerConsumption { @Min(0) Integer getPowerConsumption(); }
    private interface TubeCapacityInMl { @Min(1) Double getTubeCapacityInMl(); }
    private interface CurrentIngredientId { Long getCurrentIngredientId();}
    private interface CurrentIngredient { AutomatedIngredientDto.Response.Detailed getCurrentIngredient();}
    private interface IsPumpedUp { boolean isPumpedUp(); }
    private interface PatchIsPumpedUp { Boolean getIsPumpedUp(); }
    private interface Name { String getName(); }
    private interface PrintName { String getPrintName(); }
    private interface CanControlDirection { boolean isCanControlDirection(); }

    private interface IRemoveFields { Set<String> getRemoveFields(); }

    //Read only
    private interface IState {PumpDto.State getState(); }
    private interface ISetupStage {int getSetupStage(); }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter
        @Setter
        @EqualsAndHashCode
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = DcPumpDto.Request.Create.class, name = "dc"),
                @JsonSubTypes.Type(value = ValveDto.Request.Create.class, name = "valve"),
                @JsonSubTypes.Type(value = StepperPumpDto.Request.Create.class, name = "stepper")
        })
        @NoArgsConstructor(access = AccessLevel.PUBLIC)
        public static class Create implements TubeCapacityInMl, PatchFillingLevelInMl, CurrentIngredientId, PatchIsPumpedUp, Name, IRemoveFields, PatchPowerConsumption {
            Double tubeCapacityInMl;
            Integer fillingLevelInMl;
            Boolean isPumpedUp;
            Long currentIngredientId;
            String name;
            Set<String> removeFields;
            Integer powerConsumption;

            public Create(Pump pump) {
                this.tubeCapacityInMl = pump.getTubeCapacityInMl();
                this.fillingLevelInMl = pump.getFillingLevelInMl();
                this.isPumpedUp = pump.isPumpedUp();
                this.currentIngredientId = pump.getCurrentIngredientId();
                this.name = pump.getName();
                this.removeFields = new HashSet<>();
                this.powerConsumption = pump.getPowerConsumption();
            }

            public static Request.Create toDto(Pump pump) {
                if(pump == null) {
                    return null;
                }
                if(pump instanceof StepperPump) {
                    return new StepperPumpDto.Request.Create((StepperPump) pump);
                }
                if(pump instanceof DcPump) {
                    return new DcPumpDto.Request.Create((DcPump) pump);
                }
                if(pump instanceof Valve) {
                    return new ValveDto.Request.Create((Valve) pump);
                }
                throw new IllegalStateException("Unknown pump type: " + pump.getClass().getName());
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter @Setter @EqualsAndHashCode
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = DcPumpDto.Response.Detailed.class, name = "dc"),
                @JsonSubTypes.Type(value = ValveDto.Request.Create.class, name = "valve"),
                @JsonSubTypes.Type(value = StepperPumpDto.Response.Detailed.class, name = "stepper")
        })
        public abstract static class Detailed implements PrintName, Id, FillingLevelInMl, PowerConsumption, TubeCapacityInMl,
                CurrentIngredient, IsPumpedUp, IState, ISetupStage, Name, CanControlDirection {
            long id;
            Double tubeCapacityInMl;
            int fillingLevelInMl;
            int powerConsumption;
            boolean pumpedUp;
            AutomatedIngredientDto.Response.Detailed currentIngredient;
            String name;
            String printName;
            PumpDto.State state;
            int setupStage;
            boolean canControlDirection;

            public Detailed(Pump pump) {
                BeanUtils.copyProperties(pump, this);
                if(!pump.isCompleted()) {
                    if(pump.isCanPump()) {
                        this.state = State.TESTABLE;
                    } else {
                        this.state = State.INCOMPLETE;
                    }
                } else {
                    this.state = State.READY;
                }
                this.printName = pump.getPrintName();
                this.setupStage = pump.getSetupStage().level;
                this.powerConsumption = pump.getPowerConsumption();
                this.canControlDirection = pump.isCanControlDirection();
                if(pump.getCurrentIngredient() != null) {
                    this.currentIngredient = new AutomatedIngredientDto.Response.Detailed(pump.getCurrentIngredient());
                }
            }

            public static Detailed toDto(Pump pump) {
                if(pump == null) {
                    return null;
                }
                if(pump instanceof StepperPump) {
                    return new StepperPumpDto.Response.Detailed((StepperPump) pump);
                }
                if(pump instanceof DcPump) {
                    return new DcPumpDto.Response.Detailed((DcPump) pump);
                }
                if(pump instanceof Valve) {
                    return new ValveDto.Response.Detailed((Valve) pump);
                }
                throw new IllegalStateException("Unknown pump type: " + pump.getClass().getName());
            }

            public abstract String getType();
        }
    }

    public enum State {
        INCOMPLETE, TESTABLE, DISABLED, READY
    }

}
