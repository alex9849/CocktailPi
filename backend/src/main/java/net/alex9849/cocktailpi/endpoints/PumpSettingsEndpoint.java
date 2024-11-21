package net.alex9849.cocktailpi.endpoints;

import jakarta.validation.Valid;
import net.alex9849.cocktailpi.model.LoadCell;
import net.alex9849.cocktailpi.model.system.settings.ReversePumpSettings;
import net.alex9849.cocktailpi.payload.dto.system.settings.LoadCellDto;
import net.alex9849.cocktailpi.payload.dto.system.settings.ReversePumpSettingsDto;
import net.alex9849.cocktailpi.service.LoadCellService;
import net.alex9849.cocktailpi.service.ReversePumpSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pump/settings/")
public class PumpSettingsEndpoint {

    @Autowired
    private ReversePumpSettingsService reversePumpSettingsService;

    @Autowired
    private LoadCellService loadCellService;

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "reversepumping", method = RequestMethod.PUT)
    public ResponseEntity<?> setReversePumpSettings(@RequestBody @Valid ReversePumpSettingsDto.Request.Create settings) {
        if(settings.isEnable() && settings.getSettings() == null) {
            throw new IllegalStateException("Settings-Details are null!");
        }
        ReversePumpSettings reversePumpSettings = reversePumpSettingsService.fromDto(settings);
        reversePumpSettingsService.setReversePumpingSettings(reversePumpSettings);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "reversepumping", method = RequestMethod.GET)
    public ResponseEntity<?> getReversePumpSettings() {;
        return ResponseEntity.ok(new ReversePumpSettingsDto.Response.Detailed(reversePumpSettingsService.getReversePumpingSettings()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "loadcell", method = RequestMethod.GET)
    public ResponseEntity<?> getLoadCell() {;
        return ResponseEntity.ok(new LoadCellDto.Response.Detailed(loadCellService.getLoadCell()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "loadcell", method = RequestMethod.PUT)
    public ResponseEntity<?> setLoadCell(@RequestBody @Valid LoadCellDto.Request.Create settings) {
        LoadCell loadCell = loadCellService.fromDto(settings);
        loadCellService.setLoadCell(loadCell);
        return ResponseEntity.ok(new LoadCellDto.Response.Detailed(loadCellService.getLoadCell()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "loadcell/read", method = RequestMethod.GET)
    public ResponseEntity<?> setLoadCell() {
        return ResponseEntity.ok(loadCellService.readLoadCell());
    }

}
