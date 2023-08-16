package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.gpio.GpioBoard;
import net.alex9849.cocktailmaker.payload.dto.gpio.GpioBoardDto;
import net.alex9849.cocktailmaker.service.GpioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/gpio/")
public class GpioEndpoint {

    @Autowired
    private GpioService gpioService;

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

}
