package net.alex9849.cocktailmaker.model.eventaction;

import javax.persistence.DiscriminatorValue;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
            file = File.createTempFile("cocktailmaker_py_eventaction_", ".py");
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

            process = Runtime.getRuntime().exec("python -u " + file.getAbsolutePath());

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            new Thread(new LogReader(stdInput, runningAction, RunningAction.LogEntry.Type.INFO)).start();
            new Thread(new LogReader(errorInput, runningAction, RunningAction.LogEntry.Type.ERROR)).start();

            process.waitFor();
            file.delete();
        } catch (InterruptedException e) {
            if(process != null && process.isAlive()) {
                process.destroy();
            }
            if(file != null) {
                file.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                    runningAction.addLog(s, logType);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
