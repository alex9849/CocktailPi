package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.model.eventaction.*;
import net.alex9849.cocktailpi.payload.dto.eventaction.*;
import net.alex9849.cocktailpi.repository.EventActionExecutionGroupRepository;
import net.alex9849.cocktailpi.repository.EventActionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Transactional
public class EventService {
    private final Map<Long, RunningAction> latestRunningActionInstancesByActionId = new HashMap<>();
    private final Map<Long, RunningAction> runningActionsByRunId = new HashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private EventActionRepository eventActionRepository;

    @Autowired
    private EventActionExecutionGroupRepository executionGroupRepository;

    @Value("${alex9849.app.demoMode}")
    private boolean isDemoMode;

    public void cancelAllRunningActions() {
        synchronized (runningActionsByRunId) {
            runningActionsByRunId.values().forEach(x -> x.getFuture().cancel(true));
            runningActionsByRunId.clear();
            webSocketService.broadcastRunningEventActionsStatus(getRunningActionsInformation());
        }
    }

    public List<EventActionInformation> getRunningActionsInformation() {
        List<EventActionInformation> information = new ArrayList<>();
        synchronized (runningActionsByRunId) {
            for(RunningAction runningAction : latestRunningActionInstancesByActionId.values()) {
                EventActionInformation eai = new EventActionInformation();
                eai.setEventActionId(runningAction.getEventAction().getId());
                eai.setRunId(runningAction.getRunId());
                eai.setHasLog(!runningAction.getLog().isEmpty());
                eai.setStatus(runningActionsByRunId.containsKey(runningAction.getRunId())
                        ? EventActionInformation.Status.RUNNING : EventActionInformation.Status.STOPPED);
                information.add(eai);
            }
        }
        return information;
    }

    public List<RunningAction.LogEntry> getEventActionLog(long actionId) {
        if(!latestRunningActionInstancesByActionId.containsKey(actionId)) {
            return new ArrayList<>();
        }
        return latestRunningActionInstancesByActionId.get(actionId).getLog();
    }

    /**
     * Cancels all running actions that have one or more ExecutionGroups
     * @param executionGroups A Set of ExecutionGroups. Function won't cancel anything if empty
     */
    private void cancelAllWithoutExecutionGroups(Set<String> executionGroups) {
        if(executionGroups.isEmpty()) {
            return;
        }
        synchronized (runningActionsByRunId) {
            Set<Long> actionsToCancel = new HashSet<>();
            for(RunningAction runningAction : runningActionsByRunId.values()) {
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

    private void cancelRunningWithSameActionId(long id) {
        synchronized (runningActionsByRunId) {
            Set<Long> actionsToCancel = new HashSet<>();
            for(RunningAction runningAction : runningActionsByRunId.values()) {
                if(runningAction.getEventAction().getId() == id) {
                    actionsToCancel.add(runningAction.getRunId());
                }
            }
            for(Long runningActionId : actionsToCancel) {
                cancelRunningAction(runningActionId);
            }
        }
    }

    public boolean cancelRunningAction(long runId) {
        synchronized (runningActionsByRunId) {
            RunningAction runningAction = runningActionsByRunId.remove(runId);
            if(runningAction != null) {
                runningAction.getFuture().cancel(true);
                webSocketService.broadcastRunningEventActionsStatus(getRunningActionsInformation());
                return true;
            }
            return false;
        }
    }

    public void startActionAndBroadcastStatus(EventAction eventAction) {
        synchronized (runningActionsByRunId) {
            startAction(eventAction);
            webSocketService.broadcastRunningEventActionsStatus(getRunningActionsInformation());
        }
    }

    private void startAction(EventAction action) {
        synchronized (runningActionsByRunId) {
            cancelRunningWithSameActionId(action.getId());
            cancelAllWithoutExecutionGroups(action.getExecutionGroups());

            RunningAction runningAction = new RunningAction(action);
            runningAction.subscribeToLog(x -> {
                webSocketService.broadcastEventActionLog(action.getId(), x);
                // On first log entry broadcast running actions information
                if(!x.isEmpty() && x.size() == runningAction.getLog().size()) {
                    webSocketService.broadcastRunningEventActionsStatus(getRunningActionsInformation());
                }
            });
            runningActionsByRunId.put(runningAction.getRunId(), runningAction);
            latestRunningActionInstancesByActionId.put(action.getId(), runningAction);
            CountDownLatch syncLatch = new CountDownLatch(1);
            Future<?> future = executor.submit(() -> {
                try {
                    //Wait till runningaction is constructed completely
                    syncLatch.await();
                    action.trigger(runningAction);
                    cancelRunningAction(runningAction.getRunId());
                } catch (Exception e) {
                    e.printStackTrace();
                    cancelRunningAction(runningAction.getRunId());
                }
            });
            runningAction.setFuture(future);
            webSocketService.broadcastClearEventActionLog(action.getId());
            //Start process
            syncLatch.countDown();
        }
    }

    public void triggerActions(EventTrigger trigger) {
        synchronized (runningActionsByRunId) {
            for(EventAction action : eventActionRepository.getByTrigger(trigger)) {
                startAction(action);
            }
            webSocketService.broadcastRunningEventActionsStatus(getRunningActionsInformation());
        }
    }

    public static EventAction fromDto(EventActionDto.Request.Create dto, MultipartFile file) throws IOException {
        if(dto == null) {
            return null;
        }
        if(dto instanceof CallUrlEventActionDto.Request.Create) {
            return fromDto((CallUrlEventActionDto.Request.Create) dto);
        }
        if(dto instanceof DoNothingEventActionDto.Request.Create) {
            return fromDto((DoNothingEventActionDto.Request.Create) dto);
        }
        if(dto instanceof PlayAudioEventActionDto.Request.Create) {
            return fromDto((PlayAudioEventActionDto.Request.Create) dto, file);
        }
        if(dto instanceof ExecutePythonEventActionDto.Request.Create) {
            return fromDto((ExecutePythonEventActionDto.Request.Create) dto, file);
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
        if(isDemoMode) {
            throw new IllegalArgumentException("Deleting Event-Actions isn't allowed in demomode!");
        }
        cancelRunningWithSameActionId(id);
        return eventActionRepository.delete(id);
    }

    public EventAction createEventAction(EventAction eventAction) {
        if(isDemoMode) {
            throw new IllegalArgumentException("Creating Event-Actions isn't allowed in demomode!");
        }
        return eventActionRepository.create(eventAction);
    }

    public EventAction updateEventAction(EventAction toUpdateEventAction) {
        if(isDemoMode) {
            throw new IllegalArgumentException("Updating Event-Actions isn't allowed in demomode!");
        }
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

    public static CallUrlEventAction fromDto(CallUrlEventActionDto.Request.Create dto) {
        if(dto == null) {
            return null;
        }
        CallUrlEventAction eventAction = new CallUrlEventAction();
        BeanUtils.copyProperties(dto, eventAction);
        eventAction.setExecutionGroups(new HashSet<>(dto.getExecutionGroups()));
        return eventAction;
    }

    public static DoNothingEventAction fromDto(DoNothingEventActionDto.Request.Create dto) {
        if(dto == null) {
            return null;
        }
        DoNothingEventAction eventAction = new DoNothingEventAction();
        BeanUtils.copyProperties(dto, eventAction);
        eventAction.setExecutionGroups(new HashSet<>(dto.getExecutionGroups()));
        return eventAction;
    }

    public static PlayAudioEventAction fromDto(PlayAudioEventActionDto.Request.Create dto, MultipartFile file) throws IOException {
        if(dto == null) {
            return null;
        }
        PlayAudioEventAction eventAction = new PlayAudioEventAction();
        BeanUtils.copyProperties(dto, eventAction);
        eventAction.setExecutionGroups(new HashSet<>(dto.getExecutionGroups()));
        if(file != null) {
            eventAction.setFile(file.getBytes());
            eventAction.setFileName(file.getOriginalFilename());
        }
        return eventAction;
    }

    public static ExecutePythonEventAction fromDto(ExecutePythonEventActionDto.Request.Create dto, MultipartFile file) throws IOException {
        if(dto == null) {
            return null;
        }
        ExecutePythonEventAction eventAction = new ExecutePythonEventAction();
        BeanUtils.copyProperties(dto, eventAction);
        eventAction.setExecutionGroups(new HashSet<>(dto.getExecutionGroups()));
        if(file != null) {
            eventAction.setFile(file.getBytes());
            eventAction.setFileName(file.getOriginalFilename());
        }
        return eventAction;
    }

    public List<String> getExecutionGroups() {
        return executionGroupRepository.getAllEventExecutionGroups();
    }
}
