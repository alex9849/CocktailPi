package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.model.gpio.GpioBoard;
import net.alex9849.cocktailmaker.payload.dto.gpio.GpioBoardDto;
import net.alex9849.cocktailmaker.payload.dto.gpio.PinDto;
import net.alex9849.cocktailmaker.service.GpioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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
    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    private ResponseEntity<?> getGpioBoards(@PathVariable("id") long id) {
        GpioBoard board = gpioService.getGpioBoard(id);
        if(board == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(GpioBoardDto.Response.Detailed.toDto(board));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "", method = RequestMethod.POST)
    private ResponseEntity<?> createGpioBoard(@RequestBody GpioBoardDto.Request.Create gpioBoardDto, UriComponentsBuilder uriBuilder) {
        GpioBoard gpioBoard = gpioService.fromDto(gpioBoardDto);
        gpioBoard = gpioService.createGpioBoard(gpioBoard);
        UriComponents uriComponents = uriBuilder.path("/api/gpio/{id}").buildAndExpand(gpioBoard.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "{id}", method = RequestMethod.PUT)
    private ResponseEntity<?> updateGpioBoard(@RequestBody GpioBoardDto.Request.Create gpioBoardDto, @PathVariable("id") long id) {
        GpioBoard oldGpioBoard = gpioService.getGpioBoard(id);
        if(oldGpioBoard == null) {
            return ResponseEntity.notFound().build();
        }
        GpioBoard gpioBoard = gpioService.fromDto(gpioBoardDto);
        gpioBoard.setId(id);
        gpioBoard = gpioService.updateGpioBoard(gpioBoard);
        return ResponseEntity.ok(GpioBoardDto.Response.Detailed.toDto(gpioBoard));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "status", method = RequestMethod.GET)
    private ResponseEntity<?> getStatus() {
        return ResponseEntity.ok(gpioService.getGpioStatus());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "{id}/pin", method = RequestMethod.GET)
    private ResponseEntity<?> getGpioPins(@PathVariable(value = "id") long boardId) {
        GpioBoard gpioBoard = gpioService.getGpioBoard(boardId);
        if(gpioBoard == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(gpioBoard.getPins().stream().map(PinDto.Response.Detailed::new).toList());
    }

}
