package net.alex9849.cocktailpi.payload.response;

import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailpi.model.system.DispensingAreaState;
import net.alex9849.cocktailpi.payload.dto.glass.GlassDto;

@Getter @Setter
public class DispensingAreaStateDto {
    private GlassDto.Duplex.Detailed glass;
    private Boolean empty;

    public DispensingAreaStateDto(DispensingAreaState dispensingAreaState) {
        if (dispensingAreaState.getGlass() != null) {
            glass = new GlassDto.Duplex.Detailed(dispensingAreaState.getGlass());
        }
        empty = dispensingAreaState.getEmpty();
    }
}
