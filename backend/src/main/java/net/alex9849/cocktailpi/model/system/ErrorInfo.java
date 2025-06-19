package net.alex9849.cocktailpi.model.system;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ErrorInfo {
    private final Exception exception;
    @Getter @Setter
    private String resolvingHint;

    public ErrorInfo(Exception exception) {
        this.exception = exception;
    }

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
