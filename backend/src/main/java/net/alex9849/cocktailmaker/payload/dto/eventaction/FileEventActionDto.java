package net.alex9849.cocktailmaker.payload.dto.eventaction;

public abstract class FileEventActionDto extends EventActionDto {
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
