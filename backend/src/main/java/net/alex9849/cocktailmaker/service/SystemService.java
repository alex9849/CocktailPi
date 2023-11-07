package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.gpio.GpioBoard;
import net.alex9849.cocktailmaker.model.gpio.PinResource;
import net.alex9849.cocktailmaker.model.system.I2cAddress;
import net.alex9849.cocktailmaker.model.system.PythonLibraryInfo;
import net.alex9849.cocktailmaker.model.system.settings.DefaultFilterSettings;
import net.alex9849.cocktailmaker.model.system.settings.I2CSettings;
import net.alex9849.cocktailmaker.payload.dto.system.settings.DefaultFilterDto;
import net.alex9849.cocktailmaker.payload.dto.system.settings.I2cSettingsDto;
import net.alex9849.cocktailmaker.payload.response.GlobalSettings;
import net.alex9849.cocktailmaker.repository.OptionsRepository;
import net.alex9849.cocktailmaker.service.pumps.PumpMaintenanceService;
import net.alex9849.cocktailmaker.utils.PinUtils;
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
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class SystemService {
    public static final String REPO_KEY_I2C_PIN_SDA = "I2C_Pin_SDA";
    public static final String REPO_KEY_I2C_PIN_SCL = "I2C_Pin_SCL";
    public static final String REPO_KEY_DF_ENABLE = "DF_Enable";
    public static final String REPO_KEY_DF_SETTING_FABRICABLE = "DF_Settings_Fabricable";

    @Value("${alex9849.app.demoMode}")
    private boolean isDemoMode;

    @Value("${alex9849.app.devMode}")
    private boolean isDevMode;

    @Autowired
    private PumpMaintenanceService pumpUpService;

    @Autowired
    private GpioService gpioService;

    @Autowired
    private OptionsRepository optionsRepository;

    @Autowired
    private PinUtils pinUtils;

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
        GlobalSettings.Donation donationSettings = new GlobalSettings.Donation();
        donationSettings.setDonated(Boolean.parseBoolean(optionsRepository.getOption("Donated")));
        String stringTimeDonationDisclaimerSeen = optionsRepository.getOption("TIMESTAMP_LAST_SAW_DONATION_DISCLAIMER");
        long timeDonationDisclaimerSeen;
        if(stringTimeDonationDisclaimerSeen == null) {
            timeDonationDisclaimerSeen = 0;
        } else {
            timeDonationDisclaimerSeen = Long.parseLong(stringTimeDonationDisclaimerSeen);
        }
        long timeElapsedDonationDisclaimer = System.currentTimeMillis() - timeDonationDisclaimerSeen;
        boolean showDonationDisclaimer = timeElapsedDonationDisclaimer > (1000 * 60 * 60 * 2);
        showDonationDisclaimer &= !donationSettings.isDonated() && !isDemoMode && !isDevMode;
        donationSettings.setShowDisclaimer(showDonationDisclaimer);
        donationSettings.setDisclaimerDelay(60000);
        globalSettings.setDonation(donationSettings);
        return globalSettings;
    }

    public void setDonated(boolean value) {
        optionsRepository.setOption("Donated", Boolean.valueOf(value).toString());
    }

    public void setOpenedDonationDisclaimer() {
        optionsRepository.setOption("TIMESTAMP_LAST_SAW_DONATION_DISCLAIMER", String.valueOf(System.currentTimeMillis()));
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
        if(isDemoMode) {
            throw new IllegalArgumentException("I2C can't be configured in demomode!");
        }
        if(i2CSettings.isEnable()) {
            PinUtils.failIfPinOccupiedOrDoubled(PinResource.Type.I2C, null, i2CSettings.getSclPin(), i2CSettings.getSdaPin());
            pinUtils.shutdownOutputPin(i2CSettings.getSdaPin().getPinNr());
            pinUtils.shutdownOutputPin(i2CSettings.getSclPin().getPinNr());
        } else {
            if(!gpioService.getGpioBoardsByType("i2c").isEmpty()) {
                throw new IllegalStateException("I2C expanders found!");
            }
            pinUtils.shutdownI2C();
        }

        try {
            Process process;
            if (i2CSettings.isEnable()) {
                process = Runtime.getRuntime().exec("raspi-config nonint do_i2c 0");
            } else {
                process = Runtime.getRuntime().exec("raspi-config nonint do_i2c 1");
            }
            process.waitFor();
            optionsRepository.setOption("I2C_Enable", String.valueOf(i2CSettings.isEnable()));
            optionsRepository.setPinOption(REPO_KEY_I2C_PIN_SCL, i2CSettings.getSclPin());
            optionsRepository.setPinOption(REPO_KEY_I2C_PIN_SDA, i2CSettings.getSdaPin());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<I2cAddress> probeI2c() throws IOException {
        if(isDemoMode) {
            throw new IllegalArgumentException("I2C can't be probed in demomode!");
        }
        if(!getI2cSettings().isEnable()) {
            throw new IllegalArgumentException("I2C is disabled!");
        }
        Process process = Runtime.getRuntime().exec("i2cdetect -y 1");
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String s = null;
        int line = 1;
        Pattern pattern = Pattern.compile("[0-9a-f][0-9a-f]");
        List<I2cAddress> found = new ArrayList<>();
        while ((s = stdInput.readLine()) != null) {
            if(line++ <= 1) {
                //Skip first 2 lines
                continue;
            }
            Matcher matcher = pattern.matcher(s);
            matcher.find();
            while (matcher.find()) {
                I2cAddress addr = new I2cAddress();
                addr.setAddress(Integer.parseInt(matcher.group(), 16));
                found.add(addr);
            }
        }
        stdInput.close();
        return found;
    }

    public I2CSettings getI2cSettings() {
        I2CSettings i2CSettings = new I2CSettings();
        i2CSettings.setEnable(Boolean.parseBoolean(optionsRepository.getOption("I2C_Enable")));
        if(i2CSettings.isEnable()) {
            i2CSettings.setSdaPin(optionsRepository.getPinOption(REPO_KEY_I2C_PIN_SDA).orElse(null));
            i2CSettings.setSclPin(optionsRepository.getPinOption(REPO_KEY_I2C_PIN_SCL).orElse(null));
        }
        return i2CSettings;
    }

    public DefaultFilterSettings getDefaultFilterSettings() {
        DefaultFilterSettings defaultFilter = new DefaultFilterSettings();
        defaultFilter.setEnable(Boolean.parseBoolean(optionsRepository.getOption(REPO_KEY_DF_ENABLE)));
        if(defaultFilter.isEnable()) {
            DefaultFilterSettings.Filter filter = new DefaultFilterSettings.Filter();
            String fabricableString = optionsRepository.getOption(REPO_KEY_DF_SETTING_FABRICABLE);
            try {
                filter.setFabricable(RecipeService.FabricableFilter.valueOf(fabricableString));
            } catch (IllegalArgumentException e) {
                filter.setFabricable(RecipeService.FabricableFilter.ALL);
            }
            defaultFilter.setFilter(filter);
        }
        return defaultFilter;
    }

    public DefaultFilterSettings setDefaultFilterSettings(DefaultFilterSettings settings) {
        optionsRepository.setOption(REPO_KEY_DF_ENABLE, Boolean.toString(settings.isEnable()));
        if(settings.isEnable()) {
            DefaultFilterSettings.Filter filter = settings.getFilter();
            optionsRepository.setOption(REPO_KEY_DF_SETTING_FABRICABLE, filter.getFabricable().toString());
        } else {
            optionsRepository.delOption(REPO_KEY_DF_SETTING_FABRICABLE, false);
        }
        return getDefaultFilterSettings();
    }

    public DefaultFilterSettings fromDto(DefaultFilterDto.Duplex.Detailed dto) {
        if(dto == null) {
            return null;
        }
        DefaultFilterSettings dfs = new DefaultFilterSettings();

        if(!dto.isEnable() || dto.getFilter() == null) {
            dfs.setEnable(false);
            return dfs;
        }
        dfs.setEnable(true);
        DefaultFilterSettings.Filter filter = new DefaultFilterSettings.Filter();
        switch (dto.getFilter().getFabricable()) {
            case "" -> filter.setFabricable(RecipeService.FabricableFilter.ALL);
            case "auto" -> filter.setFabricable(RecipeService.FabricableFilter.AUTOMATICALLY);
            case "manual" -> filter.setFabricable(RecipeService.FabricableFilter.IN_BAR);
            default -> throw new IllegalArgumentException("Unknown filter type: " + dto.getFilter().getFabricable());
        }
        dfs.setFilter(filter);
        return dfs;
    }
}
