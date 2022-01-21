package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.eventaction.EventAction;
import net.alex9849.cocktailmaker.payload.dto.eventaction.EventActionDto;
import net.alex9849.cocktailmaker.payload.dto.eventaction.FileEventActionDto;
import net.alex9849.cocktailmaker.payload.dto.recipe.RecipeDto;
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
                .map(EventActionDto::toDto).collect(Collectors.toList()));
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
    public ResponseEntity<?> createAction(@Valid @RequestPart("eventAction") EventActionDto eventActionDto,
                                          @RequestPart(value = "file", required = false) MultipartFile file,
                                          UriComponentsBuilder uriBuilder) throws IOException {
        byte[] fileBytes = null;
        if(eventActionDto instanceof FileEventActionDto) {
            if(file == null) {
                throw new IllegalArgumentException("file required!");
            }
            ((FileEventActionDto) eventActionDto).setFileName(file.getOriginalFilename());
            fileBytes = file.getBytes();
        }
        EventAction createdAction = eventService.createEventAction(EventService.fromDto(eventActionDto, fileBytes));
        UriComponents uriComponents = uriBuilder.path("/api/eventaction/{id}").buildAndExpand(createdAction.getId());
        return ResponseEntity.ok(uriComponents.toUri());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateAction(@Valid @RequestPart("eventAction") EventActionDto eventActionDto,
                                          @RequestPart(value = "file", required = false) MultipartFile file,
                                          @PathVariable long id) throws IOException {
        eventActionDto.setId(id);
        byte[] fileBytes = null;
        if(eventActionDto instanceof FileEventActionDto) {
            if(file != null) {
                ((FileEventActionDto) eventActionDto).setFileName(file.getOriginalFilename());
                fileBytes = file.getBytes();
            }
        }
        EventAction updatedAction = eventService.updateEventAction(EventService.fromDto(eventActionDto, fileBytes));
        return ResponseEntity.ok(EventActionDto.toDto(updatedAction));
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
