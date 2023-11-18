package net.alex9849.cocktailpi.model.eventaction;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RunningAction {
    private static long nextRunId;
    private final List<LogEntry> log;
    private final List<Consumer<List<LogEntry>>> logSubscribers;
    private final EventAction eventAction;
    private final long runId;
    private Future<?> future;

    public RunningAction(EventAction eventAction) {
        this.runId = nextRunId++;
        this.eventAction = eventAction;
        this.future = null;
        this.log = new LinkedList<>();
        this.logSubscribers = new ArrayList<>();
    }

    public Future<?> getFuture() {
        return future;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public EventAction getEventAction() {
        return eventAction;
    }

    public long getRunId() {
        return runId;
    }

    public List<LogEntry> getLog() {
        return Collections.unmodifiableList(log);
    }

    public void subscribeToLog(Consumer<List<LogEntry>> consumer) {
        logSubscribers.add(consumer);
    }

    protected void addLog(LogEntry.Type type, String message) {
        addLog(type, Collections.singletonList(message));
    }

    protected void addLog(Throwable throwable) {
        List<String> stackTraceList = Arrays.stream(throwable.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
        stackTraceList.add(0, throwable.toString());
        addLog(RunningAction.LogEntry.Type.ERROR, stackTraceList);
    }

    protected void addLog(LogEntry.Type type, List<String> messages) {
        List<LogEntry> logEntries = new LinkedList<>();
        Date currentDate = new Date();
        for(String message : messages) {
            LogEntry logEntry = new LogEntry(currentDate, type, message);
            logEntries.add(logEntry);
        }
        this.log.addAll(logEntries);
        logSubscribers.forEach(x -> x.accept(logEntries));
    }

    public static class LogEntry {
        private final Date timeStamp;
        private final Type type;
        private final String message;
        protected LogEntry(Date timeStamp, Type type, String message) {
            this.timeStamp = timeStamp;
            this.type = type;
            this.message = message;
        }

        public Date getTimeStamp() {
            return timeStamp;
        }

        public Type getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }

        public enum Type {
            INFO, ERROR
        }
    }
}
