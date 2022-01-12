package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.eventaction.EventAction;
import net.alex9849.cocktailmaker.model.eventaction.EventTrigger;
import net.alex9849.cocktailmaker.model.eventaction.RunningAction;
import net.alex9849.cocktailmaker.payload.dto.eventaction.EventActionDto;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class EventService {
    private final Map<Long, RunningAction> runningActions = new HashMap<>();

    public void triggerActions(EventTrigger trigger) {
        //Todo get actions with trigger from DB
        List<EventAction> actions = new ArrayList<>();
        synchronized (runningActions) {
            for(EventAction action : actions) {
                cancelWithoutExecutionGroups(action.getExecutionGroups());
                CompletableFuture<?> future = CompletableFuture.runAsync(action::trigger);
                RunningAction runningAction = new RunningAction(action, future);
                runningActions.put(runningAction.getRunId(), runningAction);
                future.thenAccept((x) -> cancelRunningAction(runningAction.getRunId()));
            }
        }
    }

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
    private void cancelWithoutExecutionGroups(Set<String> executionGroups) {
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

    public static EventAction fromDto(EventActionDto dto) {
        //Todo
        return null;
    }

    public List<EventAction> getEventActions() {
        //TODO
        return new ArrayList<>();
    }

    public EventAction getEventAction(long id) {
        //TODO
        return null;
    }

    public boolean deleteEventAction(long id) {
        //TODO
        return false;
    }

    public EventAction createEventAction(EventAction eventAction) {
        //TODO
        return null;
    }

    public EventAction updateEventAction(EventAction updatedEventAction) {
        //TODO
        //Check if exists
        return null;
    }
}
