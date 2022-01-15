package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.eventaction.*;
import net.alex9849.cocktailmaker.payload.dto.eventaction.*;
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
            Set<Long> actionsToCancel = new HashSet<>();
            for(RunningAction runningAction : runningActions.values()) {
                if(runningAction.getEventAction().getExecutionGroups().isEmpty()) {
                    continue;
                }
                Set<String> matchingGroups = new HashSet<>(runningAction.getEventAction().getExecutionGroups());
                matchingGroups.retainAll(executionGroups);
                if(matchingGroups.isEmpty()) {
                    actionsToCancel.add(runningAction.getRunId());
                }
            }
            for(Long runningActionId : actionsToCancel) {
                cancelRunningAction(runningActionId);
            }
        }
    }

    public void cancelRunningWithSameActionId(long id) {
        synchronized (runningActions) {
            Set<Long> actionsToCancel = new HashSet<>();
            for(RunningAction runningAction : runningActions.values()) {
                if(runningAction.getEventAction().getId() == id) {
                    actionsToCancel.add(runningAction.getRunId());
                }
            }
            for(Long runningActionId : actionsToCancel) {
                cancelRunningAction(runningActionId);
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
                cancelRunningWithSameActionId(action.getId());
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
        if(dto instanceof DoNothingEventActionDto) {
            return fromDto((DoNothingEventActionDto) dto);
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
        cancelRunningWithSameActionId(id);
        return eventActionRepository.delete(id);
    }

    public EventAction createEventAction(EventAction eventAction) {
        return eventActionRepository.create(eventAction);
    }

    public EventAction updateEventAction(EventAction toUpdateEventAction) {
        Optional<EventAction> oOldEventAction = eventActionRepository.getById(toUpdateEventAction.getId());
        if(oOldEventAction.isEmpty()) {
            throw new IllegalArgumentException("EventAction with id " + toUpdateEventAction.getId() + " doesn't exist!");
        }
        EventAction oldEventAction = oOldEventAction.get();
        if(toUpdateEventAction instanceof FileEventAction) {
            FileEventAction toUpdateFileEventAction = (FileEventAction) toUpdateEventAction;
            if(toUpdateFileEventAction.getFile() == null) {
                if(oldEventAction.getClass() != toUpdateEventAction.getClass()) {
                    throw new IllegalArgumentException("File update required on type change!");
                } else {
                    FileEventAction oldFileEventAction = (FileEventAction) oldEventAction;
                    toUpdateFileEventAction.setFile(oldFileEventAction.getFile());
                    toUpdateFileEventAction.setFileName(oldFileEventAction.getFileName());
                }
            }
        }

        cancelRunningWithSameActionId(toUpdateEventAction.getId());
        return eventActionRepository.update(toUpdateEventAction);
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

    public static DoNothingEventAction fromDto(DoNothingEventActionDto dto) {
        if(dto == null) {
            return null;
        }
        DoNothingEventAction eventAction = new DoNothingEventAction();
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
