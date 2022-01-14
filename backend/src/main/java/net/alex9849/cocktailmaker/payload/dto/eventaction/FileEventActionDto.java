package net.alex9849.cocktailmaker.payload.dto.eventaction;

import javax.validation.constraints.Size;

public abstract class FileEventActionDto extends EventActionDto {
    @Size(max = 255)
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
