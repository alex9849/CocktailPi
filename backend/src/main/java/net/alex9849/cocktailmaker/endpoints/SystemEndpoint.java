package net.alex9849.cocktailmaker.endpoints;

import jakarta.validation.Valid;
import net.alex9849.cocktailmaker.model.system.settings.DefaultFilterSettings;
import net.alex9849.cocktailmaker.model.system.settings.I2CSettings;
import net.alex9849.cocktailmaker.model.system.settings.ReversePumpSettings;
import net.alex9849.cocktailmaker.payload.dto.system.I2cAddressDto;
import net.alex9849.cocktailmaker.payload.dto.system.settings.DefaultFilterDto;
import net.alex9849.cocktailmaker.payload.dto.system.settings.I2cSettingsDto;
import net.alex9849.cocktailmaker.payload.dto.system.settings.ReversePumpSettingsDto;
import net.alex9849.cocktailmaker.service.PumpService;
import net.alex9849.cocktailmaker.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/system/")
public class SystemEndpoint {

    @Autowired
    private SystemService systemService;

    @Autowired
    private PumpService pumpService;

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/pythonlibraries", method = RequestMethod.GET)
    public ResponseEntity<?> getPythonLibraries() throws IOException {
        return ResponseEntity.ok(systemService.getInstalledPythonLibraries());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/audiodevices", method = RequestMethod.GET)
    public ResponseEntity<?> getAudioDevices() throws IOException {
        return ResponseEntity.ok(systemService.getAudioDevices());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "settings/reversepumping", method = RequestMethod.PUT)
    public ResponseEntity<?> setReversePumpSettings(@RequestBody @Valid ReversePumpSettingsDto.Request.Create settings) {
        if(settings.isEnable() && settings.getSettings() == null) {
            throw new IllegalStateException("Settings-Details are null!");
        }
        ReversePumpSettings reversePumpSettings = pumpService.fromDto(settings);
        pumpService.setReversePumpingSettings(reversePumpSettings);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "settings/reversepumping", method = RequestMethod.GET)
    public ResponseEntity<?> getReversePumpSettings() {;
        return ResponseEntity.ok(new ReversePumpSettingsDto.Response.Detailed(pumpService.getReversePumpingSettings()));
    }

    @RequestMapping(value = "settings/donated", method = RequestMethod.PUT)
    public ResponseEntity<?> setDonated(@RequestBody boolean value) {
        systemService.setDonated(value);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/shutdown", method = RequestMethod.PUT)
    public ResponseEntity<?> shutdown() throws IOException {
        systemService.shutdown();
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "settings/global", method = RequestMethod.GET)
    public ResponseEntity<?> getGlobalSettings() {;
        return ResponseEntity.ok(systemService.getGlobalSettings());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "settings/i2c", method = RequestMethod.PUT)
    public ResponseEntity<?> setI2C(@RequestBody @Valid I2cSettingsDto.Request dto) throws IOException {
        I2CSettings i2CSettings = systemService.fromDto(dto);
        systemService.setI2cSettings(i2CSettings);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "settings/i2c", method = RequestMethod.GET)
    public ResponseEntity<?> getI2C() throws IOException {
        return ResponseEntity.ok(new I2cSettingsDto.Response(systemService.getI2cSettings()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "i2cprobe", method = RequestMethod.GET)
    public ResponseEntity<?> getI2CProbe() throws IOException {
        return ResponseEntity.ok(systemService.probeI2c().stream().map(I2cAddressDto.Response::new).toList());
    }

    @RequestMapping(value = "settings/defaultfilter", method = RequestMethod.GET)
    public ResponseEntity<?> getDefaultFilter() {
        return ResponseEntity.ok(new DefaultFilterDto.Duplex.Detailed(systemService.getDefaultFilterSettings()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "settings/defaultfilter", method = RequestMethod.PUT)
    public ResponseEntity<?> getDefaultFilter(@Valid @RequestBody DefaultFilterDto.Duplex.Detailed filter) {
        DefaultFilterSettings dfs = systemService.fromDto(filter);
        systemService.setDefaultFilterSettings(dfs);
        return ResponseEntity.ok().build();
    }
}
