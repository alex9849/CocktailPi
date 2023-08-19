package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.PythonLibraryInfo;
import net.alex9849.cocktailmaker.model.gpio.GpioBoard;
import net.alex9849.cocktailmaker.model.settings.I2CSettings;
import net.alex9849.cocktailmaker.payload.dto.settings.I2cSettingsDto;
import net.alex9849.cocktailmaker.payload.response.GlobalSettings;
import net.alex9849.cocktailmaker.repository.OptionsRepository;
import net.alex9849.cocktailmaker.service.pumps.PumpMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class SystemService {

    @Value("${alex9849.app.demoMode}")
    private boolean isDemoMode;

    @Autowired
    private PumpMaintenanceService pumpUpService;

    @Autowired
    private GpioService gpioService;

    @Autowired
    private OptionsRepository optionsRepository;

    public void shutdown() throws IOException {
        if(isDemoMode) {
            throw new IllegalArgumentException("System can't be shutdown in demomode!");
        }
        Process process = Runtime.getRuntime().exec("sudo shutdown -h now");
    }

    public List<PythonLibraryInfo> getInstalledPythonLibraries() throws IOException {
        List<PythonLibraryInfo> pythonLibraries = new ArrayList<>();
        Process process = Runtime.getRuntime().exec("pip3 list");
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String s = null;
        int line = 1;
        while ((s = stdInput.readLine()) != null) {
            if(line++ <= 2) {
                //Skip first 2 lines
                continue;
            }
            String[] splitVersion = s.split("\\s+");
            PythonLibraryInfo info = new PythonLibraryInfo();
            info.setName(splitVersion[0]);
            info.setVersion(splitVersion[1]);
            pythonLibraries.add(info);

        }
        stdInput.close();
        return pythonLibraries;
    }

    public List<String> getAudioDevices() {
        List<String> devices = new ArrayList<>();
        Line.Info sourceInfo = new Line.Info(SourceDataLine.class);
        for(Mixer.Info info : AudioSystem.getMixerInfo()) {
            Mixer mixer = AudioSystem.getMixer(info);
            if(!mixer.isLineSupported((sourceInfo))) {
                continue;
            }
            devices.add(mixer.getMixerInfo().getName());
        }
        return devices;
    }

    public GlobalSettings getGlobalSettings() {
        GlobalSettings globalSettings = new GlobalSettings();
        globalSettings.setAllowReversePumping(pumpUpService.getReversePumpingSettings().isEnable());
        return globalSettings;
    }

    public I2CSettings fromDto(I2cSettingsDto.Request dto) {
        I2CSettings settings = new I2CSettings();
        settings.setEnable(dto.isEnable());
        if(dto.isEnable()) {
            GpioBoard boardScl = gpioService.getGpioBoard(dto.getSclPin().getBoardId());
            settings.setSclPin(boardScl.getPin(dto.getSclPin().getNr()));
            GpioBoard boardSda = gpioService.getGpioBoard(dto.getSdaPin().getBoardId());
            settings.setSdaPin(boardSda.getPin(dto.getSdaPin().getNr()));
            if(settings.getSclPin() == null || settings.getSdaPin() == null) {
                settings.setEnable(false);
                settings.setSclPin(null);
                settings.setSdaPin(null);
            }
        }
        return settings;
    }

    public void setI2cSettings(I2CSettings i2CSettings) throws IOException {
        try {
            Process process;
            if (i2CSettings.isEnable()) {
                process = Runtime.getRuntime().exec("raspi-config nonint do_i2c 0");
            } else {
                process = Runtime.getRuntime().exec("raspi-config nonint do_i2c 1");
            }
            process.waitFor();
            optionsRepository.setOption("I2C_Enable", String.valueOf(i2CSettings.isEnable()));
            optionsRepository.setPinOption("I2C_SCL", i2CSettings.getSclPin());
            optionsRepository.setPinOption("I2C_SDA", i2CSettings.getSdaPin());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> probeI2c() throws IOException {
        Process process = Runtime.getRuntime().exec("i2cdetect -y 1");
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String s = null;
        int line = 1;
        Pattern pattern = Pattern.compile("[0-9a-f][0-9a-f]");
        List<Integer> found = new ArrayList<>();
        while ((s = stdInput.readLine()) != null) {
            if(line++ <= 1) {
                //Skip first 2 lines
                continue;
            }
            Matcher matcher = pattern.matcher(s);
            matcher.find();
            while (matcher.find()) {
                found.add(Integer.parseInt(matcher.group(), 16));
            }
        }
        stdInput.close();
        return found;
    }
}
