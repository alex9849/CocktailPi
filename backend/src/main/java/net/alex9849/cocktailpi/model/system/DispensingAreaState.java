package net.alex9849.cocktailpi.model.system;

import lombok.Getter;
import lombok.Setter;
import net.alex9849.cocktailpi.model.Glass;

import java.util.Objects;

@Getter @Setter
public class DispensingAreaState {
    private Glass glass;
    private Boolean empty;

    public boolean getEmpty() {
        return glass == null && empty;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DispensingAreaState that)) return false;
        return Objects.equals(glass, that.glass) && Objects.equals(empty, that.empty);
    }
}
