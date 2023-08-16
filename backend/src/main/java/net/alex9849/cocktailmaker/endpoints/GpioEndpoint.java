package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.gpio.GpioBoard;
import net.alex9849.cocktailmaker.payload.dto.gpio.GpioBoardDto;
import net.alex9849.cocktailmaker.service.GpioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/gpio/")
public class GpioEndpoint {

    @Autowired
    private GpioService gpioService;

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "", method = RequestMethod.GET)
    private ResponseEntity<?> getGpioBoards(@RequestParam(value = "dType", required = false) String dType) {
        List<GpioBoard> boards;
        if(dType == null) {
            boards = gpioService.getGpioBoards();
        } else {
            boards = gpioService.getGpioBoardsByType(dType);
        }
        return ResponseEntity.ok(boards.stream().map(GpioBoardDto.Response.Detailed::toDto).toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "{id}/pin", method = RequestMethod.GET)
    private ResponseEntity<?> getGpioPins(@PathVariable(value = "id", required = true) long boardId) {
        List<GpioBoard> boards = new ArrayList<>();
        return ResponseEntity.ok(boards.stream().map(GpioBoardDto.Response.Detailed::toDto).toList());
    }

}
