package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.eventaction.EventAction;
import net.alex9849.cocktailmaker.model.eventaction.EventTrigger;
import net.alex9849.cocktailmaker.model.eventaction.RunningAction;
import net.alex9849.cocktailmaker.payload.dto.eventaction.EventActionDto;
import net.alex9849.cocktailmaker.repository.EventActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class EventService {
    private final Map<Long, RunningAction> runningActions = new HashMap<>();

    @Autowired
    private EventActionRepository eventActionRepository;

    public void cancelAllRunningActions() {
        synchronized (runningActions) {
            runningActions.values().forEach(x -> x.getFuture().cancel(true));
            runningActions.clear();
        }
    }

    /**
     * Cancels all running actions that have one or more ExecutionGroups
     * @param executionGroups A Set of ExecutionGroups. Function won't cancel anything if empty
     */
    private void cancelAllWithoutExecutionGroups(Set<String> executionGroups) {
        if(executionGroups.isEmpty()) {
            return;
        }
        synchronized (runningActions) {
            for(RunningAction runningAction : runningActions.values()) {
                if(runningAction.getEventAction().getExecutionGroups().isEmpty()) {
                    continue;
                }
                Set<String> matchingGroups = new HashSet<>(runningAction.getEventAction().getExecutionGroups());
                matchingGroups.retainAll(executionGroups);
                if(matchingGroups.isEmpty()) {
                    cancelRunningAction(runningAction.getRunId());
                }
            }
        }
    }

    public boolean cancelRunningAction(long id) {
        synchronized (runningActions) {
            RunningAction runningAction = runningActions.remove(id);
            if(runningAction != null) {
                runningAction.getFuture().cancel(true);
                return true;
            }
            return false;
        }
    }

    public void triggerActions(EventTrigger trigger) {
        List<EventAction> actions = eventActionRepository.getByTrigger(trigger);
        synchronized (runningActions) {
            for(EventAction action : eventActionRepository.getByTrigger(trigger)) {
                cancelAllWithoutExecutionGroups(action.getExecutionGroups());
                CompletableFuture<?> future = CompletableFuture.runAsync(action::trigger);
                RunningAction runningAction = new RunningAction(action, future);
                runningActions.put(runningAction.getRunId(), runningAction);
                future.thenAccept((x) -> cancelRunningAction(runningAction.getRunId()));
            }
        }
    }

    public List<EventAction> getEventActions() {
        return eventActionRepository.getAll();
    }

    public EventAction getEventAction(long id) {
        return eventActionRepository.getById(id).orElse(null);
    }

    public boolean deleteEventAction(long id) {
        return eventActionRepository.delete(id);
    }

    public EventAction createEventAction(EventAction eventAction) {
        return eventActionRepository.create(eventAction);
    }

    public EventAction updateEventAction(EventAction updatedEventAction) {
        if(eventActionRepository.getById(updatedEventAction.getId()).isEmpty()) {
            throw new IllegalArgumentException("EventAction with id " + updatedEventAction.getId() + " doesn't exist!");
        }
        return eventActionRepository.update(updatedEventAction);
    }

    public static EventAction fromDto(EventActionDto dto) {
        //Todo
        return null;
    }
}
