package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.payload.dto.pump.PumpDto;
import net.alex9849.cocktailmaker.service.PumpService;
import net.alex9849.cocktailmaker.service.cocktailfactory.PumpCleanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private PumpCleanService pumpCleanService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPumps() {
        return ResponseEntity.ok(pumpService.getAllPumps().stream().map(PumpDto::new).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createPump(@Valid @RequestBody PumpDto pumpDto, UriComponentsBuilder uriBuilder) {
        pumpDto.setId(null);
        Pump createdPump = pumpService.createPump(pumpService.fromDto(pumpDto));
        UriComponents uriComponents = uriBuilder.path("/api/pump/{id}").buildAndExpand(createdPump.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePump(@PathVariable("id") long id, @Valid @RequestBody PumpDto pumpDto) {
        pumpDto.setId(id);
        pumpService.updatePump(pumpService.fromDto(pumpDto));
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePump(@PathVariable("id") long id) {
        pumpService.deletePump(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "{id}/clean", method = RequestMethod.PUT)
    public ResponseEntity<?> cleanPump(@PathVariable("id") long id) {
        Pump pump = pumpService.getPump(id);
        if(pump == null) {
            return ResponseEntity.notFound().build();
        }
        pumpCleanService.cleanPump(pump);
        return ResponseEntity.ok().build();
    }
}
