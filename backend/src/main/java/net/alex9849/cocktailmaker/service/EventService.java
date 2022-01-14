package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.eventaction.*;
import net.alex9849.cocktailmaker.payload.dto.eventaction.CallUrlEventActionDto;
import net.alex9849.cocktailmaker.payload.dto.eventaction.EventActionDto;
import net.alex9849.cocktailmaker.payload.dto.eventaction.ExecutePythonEventActionDto;
import net.alex9849.cocktailmaker.payload.dto.eventaction.PlayAudioEventActionDto;
import net.alex9849.cocktailmaker.repository.EventActionExecutionGroupRepository;
import net.alex9849.cocktailmaker.repository.EventActionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.*;

@Service
@Transactional
public class EventService {
    private final Map<Long, RunningAction> runningActions = new HashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    private EventActionRepository eventActionRepository;

    @Autowired
    private EventActionExecutionGroupRepository executionGroupRepository;

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
        synchronized (runningActions) {
            for(EventAction action : eventActionRepository.getByTrigger(trigger)) {
                cancelAllWithoutExecutionGroups(action.getExecutionGroups());

                RunningAction runningAction = new RunningAction(action);
                runningActions.put(runningAction.getRunId(), runningAction);
                CountDownLatch syncLatch = new CountDownLatch(1);
                Future<?> future = executor.submit(() -> {
                    try {
                        syncLatch.await();
                        action.trigger();
                        cancelRunningAction(runningAction.getRunId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        cancelRunningAction(runningAction.getRunId());
                    }
                });
                runningAction.setFuture(future);
                syncLatch.countDown();
            }
        }
    }

    public static EventAction fromDto(EventActionDto dto, byte[] file) {
        if(dto == null) {
            return null;
        }
        if(dto instanceof CallUrlEventActionDto) {
            return fromDto((CallUrlEventActionDto) dto);
        }
        if(dto instanceof PlayAudioEventActionDto) {
            return fromDto((PlayAudioEventActionDto) dto, file);
        }
        if(dto instanceof ExecutePythonEventActionDto) {
            return fromDto((ExecutePythonEventActionDto) dto, file);
        }
        throw new IllegalStateException("DtoType not supported yet: " + dto.getClass().getName());
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

    public static CallUrlEventAction fromDto(CallUrlEventActionDto dto) {
        if(dto == null) {
            return null;
        }
        CallUrlEventAction eventAction = new CallUrlEventAction();
        BeanUtils.copyProperties(dto, eventAction);
        eventAction.setExecutionGroups(new HashSet<>(dto.getExecutionGroups()));
        return eventAction;
    }

    public static PlayAudioEventAction fromDto(PlayAudioEventActionDto dto, byte[] file) {
        if(dto == null) {
            return null;
        }
        PlayAudioEventAction eventAction = new PlayAudioEventAction();
        BeanUtils.copyProperties(dto, eventAction);
        eventAction.setFile(file);
        eventAction.setExecutionGroups(new HashSet<>(dto.getExecutionGroups()));
        return eventAction;
    }

    public static ExecutePythonEventAction fromDto(ExecutePythonEventActionDto dto, byte[] file) {
        if(dto == null) {
            return null;
        }
        ExecutePythonEventAction eventAction = new ExecutePythonEventAction();
        BeanUtils.copyProperties(dto, eventAction);
        eventAction.setFile(file);
        eventAction.setExecutionGroups(new HashSet<>(dto.getExecutionGroups()));
        return eventAction;
    }

    public List<String> getExecutionGroups() {
        return executionGroupRepository.getAllEventExecutionGroups();
    }
}
