package net.alex9849.cocktailmaker.model.eventaction;

import javax.persistence.DiscriminatorValue;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@DiscriminatorValue("ExecPy")
public class ExecutePythonEventAction extends FileEventAction {

    @Override
    public String getDescription() {
        return "Execute python script: " + getFileName();
    }

    @Override
    public void trigger() {
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

            new Thread(new LogReader(stdInput)).start();
            new Thread(new LogReader(errorInput)).start();

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

        public LogReader(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public void run() {
            try {
                String s = null;
                while ((s = reader.readLine()) != null) {
                    System.out.println(s);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
