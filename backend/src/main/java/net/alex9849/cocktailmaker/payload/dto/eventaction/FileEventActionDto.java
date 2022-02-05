package net.alex9849.cocktailmaker.payload.dto.eventaction;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Size;

public abstract class FileEventActionDto extends EventActionDto {
    @Size(max = 255)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
