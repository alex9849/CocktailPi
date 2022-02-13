package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.eventaction.EventAction;
import net.alex9849.cocktailmaker.payload.dto.eventaction.EventActionDto;
import net.alex9849.cocktailmaker.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/eventaction")
public class EventActionEndpoint {

    @Autowired
    private EventService eventService;

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getActions() {
        return ResponseEntity.ok(eventService.getEventActions().stream()
                .map(EventActionDto.Response.Detailed::toDto).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/executiongroup", method = RequestMethod.GET)
    public ResponseEntity<?> getExecutionGroups() {
        return ResponseEntity.ok(eventService.getExecutionGroups());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/process/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> killProcess(@PathVariable long id) {
        if(eventService.cancelRunningAction(id)) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getAction(@PathVariable long id) {
        EventAction eventAction = eventService.getEventAction(id);
        if(eventAction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(eventAction);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createAction(@Valid @RequestPart("eventAction") EventActionDto.Request.Create eventActionDto,
                                          @RequestPart(value = "file", required = false) MultipartFile file,
                                          UriComponentsBuilder uriBuilder) throws IOException {
        EventAction createdAction = eventService.createEventAction(EventService.fromDto(eventActionDto, file));
        UriComponents uriComponents = uriBuilder.path("/api/eventaction/{id}").buildAndExpand(createdAction.getId());
        return ResponseEntity.ok(uriComponents.toUri());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateAction(@Valid @RequestPart("eventAction") EventActionDto.Request.Create eventActionDto,
                                          @RequestPart(value = "file", required = false) MultipartFile file,
                                          @PathVariable long id) throws IOException {
        EventAction eventAction = EventService.fromDto(eventActionDto, file);
        eventAction.setId(id);
        EventAction updatedAction = eventService.updateEventAction(eventAction);
        return ResponseEntity.ok(EventActionDto.Response.Detailed.toDto(updatedAction));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAction(@PathVariable long id) {
        if(!eventService.deleteEventAction(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }


}
