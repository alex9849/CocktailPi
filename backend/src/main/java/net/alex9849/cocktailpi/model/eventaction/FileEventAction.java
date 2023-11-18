package net.alex9849.cocktailpi.model.eventaction;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public abstract class FileEventAction extends EventAction {
    private String fileName;
    private byte[] file;

    public InputStream getFileInputStream() {
        return new ByteArrayInputStream(file);
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
