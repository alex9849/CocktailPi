package net.alex9849.cocktailmaker.endpoints;

import jakarta.validation.Valid;
import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.payload.dto.pump.PumpDto;
import net.alex9849.cocktailmaker.service.pumps.PumpDataService;
import net.alex9849.cocktailmaker.service.pumps.PumpUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pump")
public class PumpEndpoint {

    @Autowired
    private PumpDataService pumpService;

    @Autowired
    private PumpUpService pumpUpService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPumps() {
        return ResponseEntity.ok(pumpService.getAllPumps().stream().map(PumpDto.Response.Detailed::new).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createPump(@Valid @RequestBody PumpDto.Request.Patch pumpDto, UriComponentsBuilder uriBuilder) {
        Pump createdPump = pumpService.createPump(pumpService.fromDto(pumpDto));
        UriComponents uriComponents = uriBuilder.path("/api/pump/{id}").buildAndExpand(createdPump.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(new PumpDto.Response.Detailed(createdPump));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "{id}", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchPump(@PathVariable("id") long id, @Valid @RequestBody PumpDto.Request.Patch patchPumpDto) {
        Pump toUpdate = pumpService.getPump(id);
        if(toUpdate == null) {
            return ResponseEntity.notFound().build();
        }
        Pump updatePump = pumpService.fromDto(patchPumpDto, toUpdate);
        pumpService.updatePump(updatePump);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "{id}/pumpup", method = RequestMethod.PUT)
    public ResponseEntity<?> pumpUp(@PathVariable("id") long id) {
        Pump pump = pumpService.getPump(id);
        if(pump == null) {
            return ResponseEntity.notFound().build();
        }
        pumpUpService.pumpBackOrUp(pump, true);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "{id}/pumpback", method = RequestMethod.PUT)
    public ResponseEntity<?> pumpBack(@PathVariable("id") long id) {
        Pump pump = pumpService.getPump(id);
        if(pump == null) {
            return ResponseEntity.notFound().build();
        }
        pumpUpService.pumpBackOrUp(pump, false);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "start", method = RequestMethod.PUT)
    public ResponseEntity<?> startPump(@RequestParam(value = "id", required = false) Long id) {
        if(id == null) {
            pumpService.turnOnOffPumps(true);
            return ResponseEntity.ok().build();
        }
        Pump pump = pumpService.getPump(id);
        if(pump == null) {
            return ResponseEntity.notFound().build();
        }
        pumpService.turnOnOffPump(pump, true);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "stop", method = RequestMethod.PUT)
    public ResponseEntity<?> stopPump(@RequestParam(value = "id", required = false) Long id) {
        if(id == null) {
            pumpService.turnOnOffPumps(false);
            return ResponseEntity.ok().build();
        }
        Pump pump = pumpService.getPump(id);
        if(pump == null) {
            return ResponseEntity.notFound().build();
        }
        pumpService.turnOnOffPump(pump, false);
        return ResponseEntity.ok().build();
    }
}
