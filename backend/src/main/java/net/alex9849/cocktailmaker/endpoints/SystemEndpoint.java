package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/system")
public class SystemEndpoint {

    @Autowired
    private SystemService systemService;

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/pythonlibraries", method = RequestMethod.GET)
    public ResponseEntity<?> getPythonLibraries() throws IOException {
        return ResponseEntity.ok(systemService.getInstalledPythonLibraries());
    }

}
