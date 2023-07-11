package net.alex9849.cocktailmaker.payload.dto.pump;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Min;
import lombok.*;
import net.alex9849.cocktailmaker.model.pump.DcPump;
import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.model.pump.StepperPump;
import net.alex9849.cocktailmaker.payload.dto.recipe.ingredient.AutomatedIngredientDto;
import net.alex9849.cocktailmaker.service.PumpService;
import net.alex9849.cocktailmaker.service.pumps.PumpUpService;
import net.alex9849.cocktailmaker.utils.SpringUtility;
import net.alex9849.motorlib.Direction;
import org.springframework.beans.BeanUtils;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PumpDto {
    //Common
    private interface Id { long getId(); }
    private interface FillingLevelInMl { @Min(0) Integer getFillingLevelInMl(); }
    private interface TubeCapacityInMl { @Min(1) Double getTubeCapacityInMl(); }
    private interface CurrentIngredientId { Long getCurrentIngredientId();}
    private interface CurrentIngredient { AutomatedIngredientDto.Response.Detailed getCurrentIngredient();}
    private interface RemoveIngredient { Boolean getIsRemoveIngredient(); }
    private interface IsPumpedUp { Boolean getIsPumpedUp(); }

    //Read only
    private interface IsReversed { boolean isReversed(); }
    private interface Occupation { PumpService.PumpOccupation getOccupation(); }
    private interface IState {PumpDto.State getState(); }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {

        @Getter
        @Setter
        @EqualsAndHashCode
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = DcPumpDto.Request.Patch.class, name = "DcPump"),
                @JsonSubTypes.Type(value = StepperPumpDto.Request.Patch.class, name = "StepperPump")
        })
        public static class Patch implements FillingLevelInMl, TubeCapacityInMl, CurrentIngredientId, IsPumpedUp, RemoveIngredient {
            Integer fillingLevelInMl;
            Long currentIngredientId;
            Boolean isPumpedUp;
            Boolean isRemoveIngredient;
            Double tubeCapacityInMl;
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
                CurrentIngredient, Occupation, IsReversed, IsPumpedUp, IState {
            long id;
            Double tubeCapacityInMl;
            Integer fillingLevelInMl;
            AutomatedIngredientDto.Response.Detailed currentIngredient;
            PumpService.PumpOccupation occupation;
            Boolean isPumpedUp;
            boolean isReversed;
            PumpDto.State state;

            public Detailed(Pump pump) {
                BeanUtils.copyProperties(pump, this);
                PumpService pService = SpringUtility.getBean(PumpService.class);
                PumpUpService puService = SpringUtility.getBean(PumpUpService.class);
                this.occupation = pService.getPumpOccupation(id);
                this.isReversed = puService.getDirection() == Direction.BACKWARD;
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
                throw new IllegalStateException("Unknown pump type: " + pump.getClass().getName());
            }

            public abstract String getType();
        }
    }

    public enum State {
        INCOMPLETE, DISABLED, READY, RUNNING
    }

}
