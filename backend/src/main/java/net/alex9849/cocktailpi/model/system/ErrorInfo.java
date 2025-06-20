package net.alex9849.cocktailpi.model.system;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ErrorInfo {
    @JsonIgnore
    private final Throwable exception;

    public ErrorInfo(Throwable exception) {
        this.exception = exception;
    }

    @JsonGetter
    public List<String> getExceptionTraceMessages() {
        List<String> errorMessages = new ArrayList<>();
        HashSet<Throwable> errors = new HashSet<>();
        Throwable currentException = this.exception;
        do {
            if (!errorMessages.contains(currentException.getMessage())) {
                errorMessages.add(currentException.getMessage());
            }
            errors.add(currentException);
            currentException = currentException.getCause();
        } while (currentException != null && !errors.contains(currentException));
        return errorMessages;
    }
}
