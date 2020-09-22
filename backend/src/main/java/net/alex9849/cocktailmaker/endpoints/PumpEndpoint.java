package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.payload.dto.pump.PumpDto;
import net.alex9849.cocktailmaker.service.PumpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}
