package net.alex9849.cocktailpi.endpoints;

import jakarta.validation.Valid;
import net.alex9849.cocktailpi.model.Glass;
import net.alex9849.cocktailpi.payload.dto.glass.GlassDto;
import net.alex9849.cocktailpi.service.GlassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/glass/")
public class GlassEndpoint {

    @Autowired
    GlassService glassService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getAllGlasses() {
        return ResponseEntity.ok(glassService.getAll()
                .stream().map(GlassDto.Duplex.Detailed::new).collect(Collectors.toList()));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getGlass(@PathVariable(value = "id") long id) {
        Glass glass = glassService.getById(id);
        if(glass == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new GlassDto.Duplex.Detailed(glass));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createGlass(@Valid @RequestBody GlassDto.Duplex.Detailed glassDto, UriComponentsBuilder uriBuilder) {
        Glass glass = glassService.createGlass(glassService.fromDto(glassDto));
        UriComponents uriComponents = uriBuilder.path("/api/glass/{id}").buildAndExpand(glass.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(new GlassDto.Duplex.Detailed(glass));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateGlass(@PathVariable(value = "id") long id, @Valid @RequestBody GlassDto.Duplex.Detailed glassDto) {
        Glass oldGlass = glassService.getById(id);
        if(oldGlass == null) {
            return ResponseEntity.notFound().build();
        }
        Glass glass = glassService.fromDto(glassDto);
        glass.setId(id);
        glass = glassService.updateGlass(glass);
        return ResponseEntity.ok(new GlassDto.Duplex.Detailed(glass));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteGlass(@PathVariable(value = "id") long id) {
        glassService.deleteGlass(id);
        return ResponseEntity.ok().build();
    }

}
