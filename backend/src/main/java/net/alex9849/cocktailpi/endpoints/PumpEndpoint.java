package net.alex9849.cocktailpi.endpoints;

import jakarta.validation.Valid;
import net.alex9849.cocktailpi.model.pump.JobMetrics;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.pump.PumpAdvice;
import net.alex9849.cocktailpi.model.user.ERole;
import net.alex9849.cocktailpi.model.user.User;
import net.alex9849.cocktailpi.payload.dto.pump.DcPumpDto;
import net.alex9849.cocktailpi.payload.dto.pump.PumpDto;
import net.alex9849.cocktailpi.payload.dto.pump.StepperPumpDto;
import net.alex9849.cocktailpi.service.PumpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pump/")
public class PumpEndpoint {

    @Autowired
    private PumpService pumpService;

    private final Set<String> pumpIngEditorAllowedFields;
    private final Set<String> adminAllowedFields;

    public PumpEndpoint() {
        pumpIngEditorAllowedFields = new HashSet<>();
        pumpIngEditorAllowedFields.add("isPumpedUp");
        pumpIngEditorAllowedFields.add("currentIngredient");
        pumpIngEditorAllowedFields.add("fillingLevelInMl");
        adminAllowedFields = new HashSet<>(pumpIngEditorAllowedFields);
        adminAllowedFields.add("name");
        adminAllowedFields.add("maxStepsPerSecond");
        adminAllowedFields.add("timePerClInMs");
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPumps() {
        return ResponseEntity.ok(pumpService.getAllPumps().stream().map(PumpDto.Response.Detailed::toDto).collect(Collectors.toList()));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getPump(@PathVariable("id") long id) {
        Pump pump = pumpService.getPump(id);
        if(pump == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PumpDto.Response.Detailed.toDto(pump));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePump(@PathVariable("id") long id) {
        Pump pump = pumpService.getPump(id);
        if(pump == null) {
            return ResponseEntity.notFound().build();
        }
        pumpService.deletePump(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createPump(@Valid @RequestBody PumpDto.Request.Create pumpDto, UriComponentsBuilder uriBuilder) {
        Pump createdPump = pumpService.createPump(pumpService.fromDto(pumpDto));
        UriComponents uriComponents = uriBuilder.path("/api/pump/{id}").buildAndExpand(createdPump.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(PumpDto.Response.Detailed.toDto(createdPump));
    }

    @PreAuthorize("hasAuthority('PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "{id}", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchPump(@PathVariable("id") long id, @Valid @RequestBody PumpDto.Request.Create patchPumpDto) {
        Pump toUpdate = pumpService.getPump(id);
        if(toUpdate == null) {
            return ResponseEntity.notFound().build();
        }
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ERole authority = principal.getAuthority();
        if(authority.getLevel() < ERole.ROLE_SUPER_ADMIN.getLevel()) {
            Set<String> allowedFieldsToRemove = new HashSet<>(pumpIngEditorAllowedFields);
            PumpDto.Request.Create newPatchDto = PumpDto.Request.Create.toDto(toUpdate);
            newPatchDto.setIsPumpedUp(patchPumpDto.getIsPumpedUp());
            newPatchDto.setFillingLevelInMl(patchPumpDto.getFillingLevelInMl());
            newPatchDto.setCurrentIngredientId(patchPumpDto.getCurrentIngredientId());
            if (authority.getLevel() >= ERole.ROLE_ADMIN.getLevel()) {
                allowedFieldsToRemove.addAll(adminAllowedFields);
                newPatchDto.setName(patchPumpDto.getName());
                if (
                        newPatchDto instanceof DcPumpDto.Request.Create newPatchDtoDc
                        && patchPumpDto instanceof DcPumpDto.Request.Create patchPumpDtoDc
                ) {
                    newPatchDtoDc.setTimePerClInMs(patchPumpDtoDc.getTimePerClInMs());
                } else if (
                        newPatchDto instanceof StepperPumpDto.Request.Create newPatchDtoCreate
                        && patchPumpDto instanceof StepperPumpDto.Request.Create patchPumpDtoCreate
                ) {
                    newPatchDtoCreate.setStepsPerCl(patchPumpDtoCreate.getStepsPerCl());
                }
            }
            Set<String> removeFields = patchPumpDto.getRemoveFields();
            if(removeFields != null) {
                newPatchDto.setRemoveFields(new HashSet<>(removeFields.stream()
                        .filter(allowedFieldsToRemove::contains)
                        .toList()));
            }
            patchPumpDto = newPatchDto;
        }
        Pump updatePump = pumpService.fromDto(patchPumpDto, toUpdate);
        updatePump = pumpService.updatePump(updatePump);
        return ResponseEntity.ok(PumpDto.Response.Detailed.toDto(updatePump));
    }

    @PreAuthorize("hasAuthority('PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "{id}/pumpup", method = RequestMethod.PUT)
    public ResponseEntity<?> pumpUp(@PathVariable("id") long id, UriComponentsBuilder uriBuilder) {
        Pump pump = pumpService.getPump(id);
        if(pump == null) {
            return ResponseEntity.notFound().build();
        }
        long jobId = pumpService.performPumpAdvice(pump, new PumpAdvice(PumpAdvice.Type.PUMP_UP, 0));

        UriComponents uriComponents = uriBuilder.path("/api/pump/jobmetrics/{id}").buildAndExpand(jobId);
        return ResponseEntity.created(uriComponents.toUri()).body(jobId);
    }

    @PreAuthorize("hasAuthority('PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "{id}/pumpback", method = RequestMethod.PUT)
    public ResponseEntity<?> pumpBack(@PathVariable("id") long id, UriComponentsBuilder uriBuilder) {
        Pump pump = pumpService.getPump(id);
        if(pump == null) {
            return ResponseEntity.notFound().build();
        }
        long jobId = pumpService.performPumpAdvice(pump, new PumpAdvice(PumpAdvice.Type.PUMP_DOWN, 0));
        UriComponents uriComponents = uriBuilder.path("/api/pump/jobmetrics/{id}").buildAndExpand(jobId);
        return ResponseEntity.created(uriComponents.toUri()).body(jobId);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{id}/runjob", method = RequestMethod.PUT)
    public ResponseEntity<?> dispatchPumpAdvice(@PathVariable(value = "id") Long id, @Valid @RequestBody PumpAdvice advice, UriComponentsBuilder uriBuilder) {
        Pump pump = pumpService.getPump(id);
        if(pump == null) {
            return ResponseEntity.notFound().build();
        }
        long jobId = pumpService.performPumpAdvice(pump, advice);
        UriComponents uriComponents = uriBuilder.path("/api/pump/jobmetrics/{id}").buildAndExpand(jobId);
        return ResponseEntity.created(uriComponents.toUri()).body(jobId);
    }

    @PreAuthorize("hasAuthority('PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "start", method = RequestMethod.PUT)
    public ResponseEntity<?> startPump(@RequestParam(value = "id", required = false) Long id, UriComponentsBuilder uriBuilder) {
        if(id == null) {
            pumpService.runAllPumps();
            return ResponseEntity.ok().build();
        }
        Pump pump = pumpService.getPump(id);
        if(pump == null) {
            return ResponseEntity.notFound().build();
        }
        long jobId = pumpService.performPumpAdvice(pump, new PumpAdvice(PumpAdvice.Type.RUN, 0));
        UriComponents uriComponents = uriBuilder.path("/api/pump/jobmetrics/{id}").buildAndExpand(jobId);
        return ResponseEntity.created(uriComponents.toUri()).body(jobId);
    }

    @PreAuthorize("hasAuthority('PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "stop", method = RequestMethod.PUT)
    public ResponseEntity<?> stopPump(@RequestParam(value = "id", required = false) Long id) {
        if(id == null) {
            pumpService.stopAllPumps();
            return ResponseEntity.ok().build();
        }
        Pump pump = pumpService.getPump(id);
        if(pump == null) {
            return ResponseEntity.notFound().build();
        }
        pumpService.cancelPumpUp(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('PUMP_INGREDIENT_EDITOR')")
    @RequestMapping(value = "jobmetrics/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getJobMetrics(@PathVariable("id") long id) {
        JobMetrics jobMetrics = pumpService.getJobMetrics(id);
        if(jobMetrics == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(jobMetrics);
    }

}
