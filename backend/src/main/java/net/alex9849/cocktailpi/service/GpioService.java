package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.model.gpio.*;
import net.alex9849.cocktailpi.model.gpio.i2cboard.I2CBoardModel;
import net.alex9849.cocktailpi.model.gpio.i2cboard.I2CGpioBoard;
import net.alex9849.cocktailpi.model.gpio.local.LocalGpioBoard;
import net.alex9849.cocktailpi.model.system.GpioStatus;
import net.alex9849.cocktailpi.payload.dto.gpio.GpioBoardDto;
import net.alex9849.cocktailpi.payload.dto.gpio.I2CGpioBoardDto;
import net.alex9849.cocktailpi.payload.dto.gpio.LocalGpioBoardDto;
import net.alex9849.cocktailpi.payload.dto.gpio.PinDto;
import net.alex9849.cocktailpi.repository.GpioRepository;
import net.alex9849.cocktailpi.service.pumps.PumpLockService;
import net.alex9849.cocktailpi.service.pumps.PumpMaintenanceService;
import net.alex9849.cocktailpi.utils.PinUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GpioService {

    @Autowired
    private GpioRepository gpioRepository;

    @Autowired
    private SystemService systemService;

    @Autowired
    private PumpMaintenanceService maintenanceService;

    @Autowired
    private PumpLockService pumpLockService;

    @Autowired
    private PinUtils pinUtils;


    public List<GpioBoard> getGpioBoards() {
        return gpioRepository.getBoards();
    }

    public GpioBoard getGpioBoard(long id) {
        return gpioRepository.findById(id).orElse(null);
    }

    public List<GpioBoard> getGpioBoardsByType(String dType) {
        return gpioRepository.getBoardsByDType(dType);
    }

    public GpioBoard createGpioBoard(GpioBoard gpioBoard) {
        List<GpioBoard> byType = getGpioBoardsByType(gpioBoard.getType().discriminatorValue);

        if(gpioRepository.getBoardsByName(gpioBoard.getName()).isPresent()) {
            throw new IllegalArgumentException("A GpioBoard with that name already exists!");
        }
        if(gpioBoard instanceof LocalGpioBoard) {
            if(!byType.isEmpty()) {
                throw new IllegalArgumentException("Can't create more than one LocalGpioBoard!");
            }
        } else if(gpioBoard instanceof I2CGpioBoard i2CGpioBoard) {
            if(!systemService.getI2cSettings().isEnable()) {
                throw new IllegalStateException("I2C is disabled!");
            }
            Optional<PinResource> oPinResource = getPinResourceByI2CAddress(i2CGpioBoard.getI2cAddress());
            if(oPinResource.isPresent() && oPinResource.get().getId() != gpioBoard.getId()) {
                throw new IllegalArgumentException("I2C-Address already in use!");
            }
        } else {
            throw new IllegalStateException("Unknown board type: " + gpioBoard.getClass());
        }
        return gpioRepository.createBoard(gpioBoard);
    }

    public GpioBoard updateGpioBoard(GpioBoard gpioBoard) {
        pumpLockService.testAndAcquireGlobal(this);
        boolean releadPins = false;
        try {
            Optional<GpioBoard> oOldGpioBoard = gpioRepository.findById(gpioBoard.getId());
            if(oOldGpioBoard.isEmpty()) {
                throw new IllegalArgumentException("GpioBoard with id " + gpioBoard.getId() + " doesn't exist.");
            }
            GpioBoard oldGpioBoard = oOldGpioBoard.get();
            if(oldGpioBoard.getClass() != gpioBoard.getClass()) {
                throw new IllegalArgumentException("GpioBoard type can't be changed afterwards!");
            }

            Optional<GpioBoard> withNameBoard = gpioRepository.getBoardsByName(gpioBoard.getName());
            if(withNameBoard.isPresent() && withNameBoard.get().getId() != gpioBoard.getId()) {
                throw new IllegalArgumentException("A GpioBoard with that name already exists!");
            }

            if(gpioBoard instanceof LocalGpioBoard) {
                // OK
            } else if (gpioBoard instanceof I2CGpioBoard i2CGpioBoard) {
                I2CGpioBoard oldI2CGpioBoard = (I2CGpioBoard) oldGpioBoard;
                if(oldI2CGpioBoard.getBoardModel() != i2CGpioBoard.getBoardModel()) {
                    throw new IllegalArgumentException("GpioBoard BoardModel can't be changed afterwards!");
                }
                Optional<PinResource> oPinResource = getPinResourceByI2CAddress(oldI2CGpioBoard.getI2cAddress());
                if(oPinResource.isPresent() && oPinResource.get().getId() != gpioBoard.getId()) {
                    throw new IllegalArgumentException("I2C-Address already in use!");
                }
                if(oldI2CGpioBoard.getI2cAddress() != i2CGpioBoard.getI2cAddress()) {
                    i2CGpioBoard.getBoardDriver().updateI2c(pinUtils.getI2c(i2CGpioBoard.getI2cAddress()), false);
                    pinUtils.shutdownI2CAddress(oldI2CGpioBoard.getI2cAddress());
                    releadPins = true;
                }

            } else {
                throw new IllegalStateException("Unknown board type: " + gpioBoard.getClass());
            }
            gpioRepository.updateBoard(gpioBoard);
            if(releadPins) {
                reloadGlobalPins();
            }
            return gpioRepository.findById(gpioBoard.getId()).orElse(null);
        } finally {
            pumpLockService.releaseGlobal(this);
        }
    }

    private void reloadGlobalPins() {
        maintenanceService.reloadPins();
    }

    public void deleteGpioBoard(long id) {
        Optional<GpioBoard> oGpioBoard = gpioRepository.findById(id);
        if(oGpioBoard.isEmpty()) {
            return;
        }
        GpioBoard gpioBoard = oGpioBoard.get();
        if(gpioBoard instanceof LocalGpioBoard) {
            throw new IllegalArgumentException("LocalGpioBoard can't be deleted!");
        }
        if(gpioBoard.getPins().stream().anyMatch(x -> x.getResource() != null)) {
            throw new IllegalStateException("Some pins from the board are still assigned to resources!");
        }
        if(gpioBoard instanceof I2CGpioBoard i2CGpioBoard) {
            i2CGpioBoard.shutdownDriver();
        }
        gpioRepository.deleteBoard(id);
    }

    public HardwarePin fromDto(PinDto.Request.Select pinDto) {
        if(pinDto == null) {
            return null;
        }
        GpioBoard board = gpioRepository.findById(pinDto.getBoardId()).orElse(null);
        if(board == null) {
            throw new IllegalArgumentException("GpioBoard with id \"" + pinDto.getBoardId() + "\" doesn't exist!");
        }
        return board.getPin(pinDto.getNr());
    }

    public Optional<PinResource> getPinResourceByBoardIdAndPin(long boardId, int pinNr) {
        return gpioRepository.getPinResourceByBoardIdAndPin(boardId, pinNr);
    }

    public Optional<PinResource> getPinResourceByI2CAddress(int address) {
        return gpioRepository.getPinResourceByI2CAddress(address);
    }

    public GpioStatus getGpioStatus() {
        return gpioRepository.getGpioStatus();
    }

    public GpioBoard fromDto(GpioBoardDto.Request.Create gpioBoardDto) {
        if(gpioBoardDto == null) {
            return null;
        }
        GpioBoard gpioBoard;
        if(gpioBoardDto instanceof LocalGpioBoardDto.Request.Create) {
            gpioBoard = new LocalGpioBoard();
        } else if (gpioBoardDto instanceof I2CGpioBoardDto.Request.Create i2cGpioBoardDto) {
            I2CBoardModel boardModel = I2CBoardModel.valueOf(i2cGpioBoardDto.getBoardModel());
            I2CGpioBoard i2CGpioBoard = I2CBoardModel.genInstance(boardModel);
            i2CGpioBoard.setI2cAddress(i2cGpioBoardDto.getAddress());
            gpioBoard = i2CGpioBoard;
        } else {
            throw new IllegalArgumentException("Unknown board type: " + gpioBoardDto.getClass().getName());
        }
        gpioBoard.setName(gpioBoardDto.getName());
        return gpioBoard;
    }

    public void restartBoard(GpioBoard gpioBoard) {
        pumpLockService.testAndAcquireGlobal(this);
        try {
            if(gpioBoard instanceof I2CGpioBoard i2CGpioBoard) {
                i2CGpioBoard.restart();
                reloadGlobalPins();
            }
        } finally {
            pumpLockService.releaseGlobal(this);
        }
    }
}
