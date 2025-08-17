package net.alex9849.cocktailpi.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.SneakyThrows;
import net.alex9849.cocktailpi.config.seed.SeedDataInserter;
import net.alex9849.cocktailpi.service.PowerLimitService;
import net.alex9849.cocktailpi.service.PumpService;
import org.flywaydb.core.Flyway;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SpringUtility implements ApplicationContextAware {

    @Autowired
    private Flyway flyway;

    @Autowired
    private SeedDataInserter seedDataInserter;

    @Autowired
    private PumpService pumpService;


    @Autowired
    private static ApplicationContext applicationContext;
    @Autowired
    private PowerLimitService powerLimitService;

    @SneakyThrows
    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        flyway.migrate();
        seedDataInserter.migrate();
        pumpService.stopAllPumps();
        powerLimitService.applyPowerLimit();
    }

    /*
        Get a class bean from the application context
     */
    public static <T> T getBean(final Class clazz) {
        return (T) applicationContext.getBean(clazz);
    }

    /*
        Return the application context if necessary for anything else
     */
    public static ApplicationContext getContext() {
        return applicationContext;
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public synchronized static void createPythonVenv() throws IOException {
        File cocktailPiDir = new File(System.getProperty("java.class.path")).getAbsoluteFile().getParentFile();
        File vEnvDir = new File(cocktailPiDir, "venv");
        if (!vEnvDir.exists()) {
            try {
                Process createVenvP = Runtime.getRuntime().exec("python3 -m venv " + vEnvDir.getAbsolutePath());
                createVenvP.waitFor();
            } catch (InterruptedException e) {
                FileSystemUtils.deleteRecursively(vEnvDir);
                return;
            }
        }

    }

    public static <T> List<T> loadFromStream(InputStream stream, Class<T> typeClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, typeClass);
        return mapper.readValue(stream, collectionType);
    }

    public static <T> List<T> loadFromFile(String path, Class<T> typeClass) throws IOException {
        InputStream recipeStream = SpringUtility.class.getResourceAsStream(path);
        return loadFromStream(recipeStream, typeClass);
    }

    public static String normalize(String input) {
        if (input == null) return null;
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

}
