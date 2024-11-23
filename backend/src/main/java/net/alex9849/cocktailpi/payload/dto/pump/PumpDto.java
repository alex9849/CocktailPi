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

import java.util.Set;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PumpDto {
    //Common
    private interface Id { long getId(); }
    private interface FillingLevelInMl { @Min(0) int getFillingLevelInMl(); }
    private interface PatchFillingLevelInMl { @Min(0) Integer getFillingLevelInMl(); }
    private interface TubeCapacityInMl { @Min(1) Double getTubeCapacityInMl(); }
    private interface CurrentIngredientId { Long getCurrentIngredientId();}
    private interface CurrentIngredient { AutomatedIngredientDto.Response.Detailed getCurrentIngredient();}
    private interface IsPumpedUp { boolean isPumpedUp(); }
    private interface PatchIsPumpedUp { Boolean getIsPumpedUp(); }
    private interface Name { String getName(); }

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
                @JsonSubTypes.Type(value = StepperPumpDto.Request.Create.class, name = "stepper")
        })
        public static class Create implements TubeCapacityInMl, PatchFillingLevelInMl, CurrentIngredientId, PatchIsPumpedUp, Name, IRemoveFields {
            Double tubeCapacityInMl;
            Integer fillingLevelInMl;
            Boolean isPumpedUp;
            Long currentIngredientId;
            String name;
            Set<String> removeFields;

        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {

        @Getter @Setter @EqualsAndHashCode
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = DcPumpDto.Response.Detailed.class, name = "dc"),
                @JsonSubTypes.Type(value = StepperPumpDto.Response.Detailed.class, name = "stepper")
        })
        public abstract static class Detailed implements Id, FillingLevelInMl, TubeCapacityInMl,
                CurrentIngredient, IsPumpedUp, IState, ISetupStage, Name {
            long id;
            Double tubeCapacityInMl;
            int fillingLevelInMl;
            boolean pumpedUp;
            AutomatedIngredientDto.Response.Detailed currentIngredient;
            String name;
            PumpDto.State state;
            int setupStage;

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
                this.setupStage = pump.getSetupStage().level;
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
