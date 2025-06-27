package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.model.gpio.GpioBoard;
import net.alex9849.cocktailpi.model.system.ErrorInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ErrorService {
    private HashMap<Long, List<ErrorInfo>> gpioBoardErrors = new HashMap<>();

    public void addGpioBoardError(GpioBoard board, ErrorInfo info) {
        gpioBoardErrors.computeIfAbsent(board.getId(), k -> new ArrayList<>()).add(info);
    }

    public List<ErrorInfo> getGpioBoardErrors(GpioBoard board) {
        return gpioBoardErrors.getOrDefault(board.getId(), new ArrayList<>());
    }


}
