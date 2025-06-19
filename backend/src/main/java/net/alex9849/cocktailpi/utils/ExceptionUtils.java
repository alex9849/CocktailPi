package net.alex9849.cocktailpi.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ExceptionUtils {

    public static List<String> getExceptionTraceMessages(Exception e) {
        List<String> errorMessages = new ArrayList<>();
        HashSet<Throwable> errors = new HashSet<>();
        Throwable currentException = e;
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
