package net.alex9849.cocktailpi.payload.dto.recipe.productionstep;

import lombok.*;
import net.alex9849.cocktailpi.model.recipe.productionstep.WrittenInstructionProductionStep;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WrittenInstructionProductionStepDto {
    private interface Message { String getMessage(); }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @Getter @Setter
        @EqualsAndHashCode(callSuper = true)
        public static class Create extends ProductionStepDto.Request.Create implements Message {
            String message;

            public Create() {}

            public Create(Response.Detailed detailed) {
                super(detailed);
            }

            @Override
            public String getType() {
                return "writtenInstruction";
            }
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Getter @Setter
        @EqualsAndHashCode(callSuper = true)
        public static class Detailed extends ProductionStepDto.Response.Detailed implements Message {
            String message;

            public Detailed() {}

            public Detailed(WrittenInstructionProductionStep productionStep) {
                super(productionStep);
            }

            @Override
            public String getType() {
                return "writtenInstruction";
            }
        }
    }

}
