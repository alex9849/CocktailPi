package net.alex9849.cocktailmaker.model.eventaction;

import java.io.InputStream;

public abstract class FileEventAction extends EventAction {
    private String fileName;

    public abstract InputStream getFileInputStream();

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
