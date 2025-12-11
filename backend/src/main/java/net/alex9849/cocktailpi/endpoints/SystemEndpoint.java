package net.alex9849.cocktailpi.endpoints;

import jakarta.validation.Valid;
import net.alex9849.cocktailpi.model.system.settings.DefaultFilterSettings;
import net.alex9849.cocktailpi.model.system.settings.I2CSettings;
import net.alex9849.cocktailpi.model.system.settings.Language;
import net.alex9849.cocktailpi.payload.dto.system.I2cAddressDto;
import net.alex9849.cocktailpi.payload.dto.system.settings.AppearanceSettingsDto;
import net.alex9849.cocktailpi.payload.dto.system.settings.DefaultFilterDto;
import net.alex9849.cocktailpi.payload.dto.system.settings.I2cSettingsDto;
import net.alex9849.cocktailpi.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/system/")
public class SystemEndpoint {

    @Autowired
    private SystemService systemService;

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "/pythonlibraries", method = RequestMethod.GET)
    public ResponseEntity<?> getPythonLibraries() throws IOException {
        return ResponseEntity.ok(systemService.getInstalledPythonLibraries());
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "/audiodevices", method = RequestMethod.GET)
    public ResponseEntity<?> getAudioDevices() throws IOException {
        return ResponseEntity.ok(systemService.getAudioDevices());
    }

    @RequestMapping(value = "settings/donated", method = RequestMethod.PUT)
    public ResponseEntity<?> setDonated(@RequestBody boolean value) {
        systemService.setDonated(value);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "settings/appearance", method = RequestMethod.PUT)
    public ResponseEntity<?> setAppearance(@RequestBody @Valid AppearanceSettingsDto.Duplex.Detailed settingsDto) {
        systemService.setAppearance(settingsDto);
        return ResponseEntity.ok(systemService.getAppearance());
    }

    @RequestMapping(value = "settings/appearance", method = RequestMethod.GET)
    public ResponseEntity<?> getAppearance() {
        return ResponseEntity.ok(systemService.getAppearance());
    }

    @RequestMapping(value = "settings/appearance/language", method = RequestMethod.GET)
    public ResponseEntity<?> getLanguages() {
        return ResponseEntity.ok(Language.values());
    }

    @RequestMapping(value = "settings/sawdonationdisclaimer", method = RequestMethod.PUT)
    public ResponseEntity<?> setDonated() {
        systemService.setOpenedDonationDisclaimer();
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/shutdown", method = RequestMethod.PUT)
    public ResponseEntity<?> shutdown(@RequestParam(value = "isReboot", defaultValue = "false") boolean isReboot) throws IOException {
        systemService.shutdown(isReboot);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "settings/global", method = RequestMethod.GET)
    public ResponseEntity<?> getGlobalSettings() {;
        return ResponseEntity.ok(systemService.getGlobalSettings());
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "settings/i2c", method = RequestMethod.PUT)
    public ResponseEntity<?> setI2C(@RequestBody @Valid I2cSettingsDto.Request dto) throws IOException {
        I2CSettings i2CSettings = systemService.fromDto(dto);
        systemService.setI2cSettings(i2CSettings);
        return ResponseEntity.ok(new I2cSettingsDto.Response(systemService.getI2cSettings()));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "settings/i2c", method = RequestMethod.GET)
    public ResponseEntity<?> getI2C() throws IOException {
        return ResponseEntity.ok(new I2cSettingsDto.Response(systemService.getI2cSettings()));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "i2cprobe", method = RequestMethod.GET)
    public ResponseEntity<?> getI2CProbe() throws IOException {
        return ResponseEntity.ok(systemService.probeI2c().stream().map(I2cAddressDto.Response::new).toList());
    }

    @RequestMapping(value = "settings/defaultfilter", method = RequestMethod.GET)
    public ResponseEntity<?> getDefaultFilter() {
        return ResponseEntity.ok(new DefaultFilterDto.Duplex.Detailed(systemService.getDefaultFilterSettings()));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "settings/defaultfilter", method = RequestMethod.PUT)
    public ResponseEntity<?> getDefaultFilter(@Valid @RequestBody DefaultFilterDto.Duplex.Detailed filter) {
        DefaultFilterSettings dfs = systemService.fromDto(filter);
        dfs = systemService.setDefaultFilterSettings(dfs);
        return ResponseEntity.ok(new DefaultFilterDto.Duplex.Detailed(dfs));
    }

    @RequestMapping(value = "version", method = RequestMethod.GET)
    public ResponseEntity<?> getVersion() {
        return ResponseEntity.ok(systemService.getVersion());
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "checkupdate", method = RequestMethod.GET)
    public ResponseEntity<?> checkUpdate() {
        return ResponseEntity.ok(systemService.checkUpdate());
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @RequestMapping(value = "performupdate", method = RequestMethod.POST)
    public ResponseEntity<?> performUpdate() {
        systemService.performUpdate();
        return ResponseEntity.ok().build();
    }
}
