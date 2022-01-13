package net.alex9849.cocktailmaker.model.eventaction;

import javax.persistence.DiscriminatorValue;
import java.io.*;

@DiscriminatorValue("ExecPy")
public class ExecutePythonEventAction extends FileEventAction {
    private InputStream program;

    @Override
    public InputStream getFileInputStream() {
        return program;
    }

    public void setFileInputStream(InputStream stream) {
        this.program = program;
    }

    @Override
    protected String generateDescription() {
        return "Execute python script: " + getFileName();
    }

    @Override
    public void trigger() {
        Process process = null;
        File file = null;
        try {
            file = File.createTempFile("cocktailmaker_py_eventaction_", ".py");
            file.deleteOnExit();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            BufferedReader reader = new BufferedReader(new InputStreamReader(program));
            String line = null;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            writer.close();
            reader.close();

            process = Runtime.getRuntime().exec("python " + file.getAbsolutePath());
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
}
