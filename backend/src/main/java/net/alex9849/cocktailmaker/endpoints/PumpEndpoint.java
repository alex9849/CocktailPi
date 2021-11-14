package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.user.ERole;
import net.alex9849.cocktailmaker.model.user.User;
import net.alex9849.cocktailmaker.payload.dto.pump.PumpDto;
import net.alex9849.cocktailmaker.service.PumpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pump")
public class PumpEndpoint {

    @Autowired
    private PumpService pumpService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPumps() {
        return ResponseEntity.ok(pumpService.getAllPumps().stream().map(PumpDto::new).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createPump(@Valid @RequestBody PumpDto pumpDto, UriComponentsBuilder uriBuilder) {
        Pump createdPump = pumpService.createPump(PumpService.fromDto(pumpDto));
        UriComponents uriComponents = uriBuilder.path("/api/pump/{id}").buildAndExpand(createdPump.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePump(@PathVariable("id") long id, @Valid @RequestBody PumpDto pumpDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        pumpDto.setId(id);
        Pump updatePump = PumpService.fromDto(pumpDto);
        if(!user.getAuthorities().contains(ERole.ROLE_ADMIN)) {
            Pump oldPump = pumpService.getPump(id);
            if(oldPump != null) {
                oldPump.setCurrentIngredient(updatePump.getCurrentIngredient());
                oldPump.setFillingLevelInMl(updatePump.getFillingLevelInMl());
                updatePump = oldPump;
            }
        }
        pumpService.updatePump(updatePump);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePump(@PathVariable("id") long id) {
        pumpService.deletePump(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "{id}/clean", method = RequestMethod.PUT)
    public ResponseEntity<?> cleanPump(@PathVariable("id") long id) {
        Pump pump = pumpService.getPump(id);
        if(pump == null) {
            return ResponseEntity.notFound().build();
        }
        pumpService.cleanPump(pump);
        return ResponseEntity.ok().build();
    }
}
