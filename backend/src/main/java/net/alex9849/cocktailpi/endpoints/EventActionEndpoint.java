package net.alex9849.cocktailpi.endpoints;

import jakarta.validation.Valid;
import net.alex9849.cocktailpi.model.eventaction.EventAction;
import net.alex9849.cocktailpi.payload.dto.eventaction.EventActionDto;
import net.alex9849.cocktailpi.payload.dto.eventaction.FileEventActionDto;
import net.alex9849.cocktailpi.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/eventaction/")
public class EventActionEndpoint {

    @Autowired
    private EventService eventService;

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getActions() {
        return ResponseEntity.ok(eventService.getEventActions().stream()
                .map(EventActionDto.Response.Detailed::toDto).collect(Collectors.toList()));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "/executiongroup", method = RequestMethod.GET)
    public ResponseEntity<?> getExecutionGroups() {
        return ResponseEntity.ok(eventService.getExecutionGroups());
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "/process/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> killProcess(@PathVariable long id) {
        if (eventService.cancelRunningAction(id)) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getAction(@PathVariable long id) {
        EventAction eventAction = eventService.getEventAction(id);
        if (eventAction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(eventAction);
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "/{id}/start", method = RequestMethod.POST)
    public ResponseEntity<?> runAction(@PathVariable long id) {
        EventAction eventAction = eventService.getEventAction(id);
        if (eventAction == null) {
            return ResponseEntity.notFound().build();
        }
        eventService.startActionAndBroadcastStatus(eventAction);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createAction(@Valid @RequestPart("eventAction") EventActionDto.Request.Create eventActionDto,
                                          @RequestPart(value = "file", required = false) MultipartFile file,
                                          UriComponentsBuilder uriBuilder) throws IOException {
        if(eventActionDto instanceof FileEventActionDto.Request.Create && file == null) {
            throw new IllegalArgumentException("file required!");
        }
        EventAction createdAction = eventService.createEventAction(EventService.fromDto(eventActionDto, file));
        UriComponents uriComponents = uriBuilder.path("/api/eventaction/{id}").buildAndExpand(createdAction.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(EventActionDto.Response.Detailed.toDto(createdAction));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateAction(@Valid @RequestPart("eventAction") EventActionDto.Request.Create eventActionDto,
                                          @RequestPart(value = "file", required = false) MultipartFile file,
                                          @PathVariable long id) throws IOException {
        EventAction eventAction = EventService.fromDto(eventActionDto, file);
        eventAction.setId(id);
        EventAction updatedAction = eventService.updateEventAction(eventAction);
        return ResponseEntity.ok(EventActionDto.Response.Detailed.toDto(updatedAction));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAction(@PathVariable long id) {
        if (!eventService.deleteEventAction(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }


}
