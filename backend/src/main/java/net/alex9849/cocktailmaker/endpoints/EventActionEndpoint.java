package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.eventaction.EventAction;
import net.alex9849.cocktailmaker.payload.dto.eventaction.EventActionDto;
import net.alex9849.cocktailmaker.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
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
                .map(EventActionDto::toDto).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createAction(@Valid @RequestBody EventActionDto eventActionDto, UriComponentsBuilder uriBuilder) {
        EventAction createdAction = eventService.createEventAction(EventService.fromDto(eventActionDto));
        UriComponents uriComponents = uriBuilder.path("/api/eventaction/{id}").buildAndExpand(createdAction.getId());
        return ResponseEntity.ok(uriComponents.toUri());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateAction(@Valid @RequestBody EventActionDto eventActionDto, @PathVariable Long id) {
        eventActionDto.setId(id);
        EventAction updatedAction = eventService.updateEventAction(EventService.fromDto(eventActionDto));
        return ResponseEntity.ok(updatedAction);
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
