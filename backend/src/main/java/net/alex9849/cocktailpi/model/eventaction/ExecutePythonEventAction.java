package net.alex9849.cocktailpi.model.eventaction;

import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailpi.utils.SpringUtility;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@DiscriminatorValue("ExecPy")
public class ExecutePythonEventAction extends FileEventAction {

    @Override
    public String getDescription() {
        return "Execute python script: " + getFileName();
    }

    @Override
    public void trigger(RunningAction runningAction) {
        Process process = null;
        File file = null;
        try {
            file = File.createTempFile("cocktailpi_py_eventaction_", ".py");
            file.deleteOnExit();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8));
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(getFile())));
            String line = null;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            writer.close();
            reader.close();

            File cocktailPiDir = new File(System.getProperty("java.class.path")).getAbsoluteFile().getParentFile();
            File vEnvDir = new File(cocktailPiDir, "venv");
            SpringUtility.createPythonVenv();

            process = Runtime.getRuntime().exec(vEnvDir.getAbsolutePath() + "/bin/python3 -u " + file.getAbsolutePath());

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            new Thread(new LogReader(stdInput, runningAction, RunningAction.LogEntry.Type.INFO)).start();
            new Thread(new LogReader(errorInput, runningAction, RunningAction.LogEntry.Type.ERROR)).start();

            process.waitFor();
            file.delete();
        } catch (InterruptedException e) {
            if (process.isAlive()) {
                process.destroy();
            }
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
            runningAction.addLog(e);
        }
    }


    private static class LogReader implements Runnable {
        private final BufferedReader reader;
        private final RunningAction runningAction;
        private final RunningAction.LogEntry.Type logType;

        public LogReader(BufferedReader reader, RunningAction runningAction, RunningAction.LogEntry.Type logType) {
            this.reader = reader;
            this.runningAction = runningAction;
            this.logType = logType;
        }

        @Override
        public void run() {
            try {
                String s = null;
                while ((s = reader.readLine()) != null) {
                    runningAction.addLog(logType, s);
                }
                reader.close();
            } catch (IOException ignore) {}
        }
    }
}
