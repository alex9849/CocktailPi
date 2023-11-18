package net.alex9849.cocktailpi.repository;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.DiscriminatorValue;
import net.alex9849.cocktailpi.model.eventaction.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Component
public class EventActionRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private EventActionExecutionGroupRepository executionGroupRepository;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public EventAction create(EventAction eventAction) {
        return getJdbcTemplate().execute((ConnectionCallback<EventAction>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO event_actions (dType, trigger, comment, on_repeat, volume, " +
                    "filename, file, requestMethod, url, audio_device) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, eventAction.getClass().getAnnotation(DiscriminatorValue.class).value());
            pstmt.setString(2, eventAction.getTrigger().name());
            pstmt.setString(3, eventAction.getComment());
            if (eventAction instanceof CallUrlEventAction) {
                CallUrlEventAction callUrlEventAction = (CallUrlEventAction) eventAction;
                pstmt.setNull(4, Types.BOOLEAN);
                pstmt.setNull(5, Types.NUMERIC);
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.BLOB);
                pstmt.setString(8, callUrlEventAction.getRequestMethod().name());
                pstmt.setString(9, callUrlEventAction.getUrl());
                pstmt.setNull(10, Types.VARCHAR);

            } else if (eventAction instanceof PlayAudioEventAction) {
                PlayAudioEventAction playAudioEventAction = (PlayAudioEventAction) eventAction;
                pstmt.setBoolean(4, playAudioEventAction.isOnRepeat());
                pstmt.setInt(5, playAudioEventAction.getVolume());
                pstmt.setString(6, playAudioEventAction.getFileName());
                pstmt.setBytes(7, playAudioEventAction.getFile());
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.VARCHAR);
                pstmt.setString(10, playAudioEventAction.getSoundDevice());

            } else if (eventAction instanceof ExecutePythonEventAction) {
                ExecutePythonEventAction executePythonEventAction = (ExecutePythonEventAction) eventAction;
                pstmt.setNull(4, Types.BOOLEAN);
                pstmt.setNull(5, Types.NUMERIC);
                pstmt.setString(6, executePythonEventAction.getFileName());
                pstmt.setBytes(7, executePythonEventAction.getFile());
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.VARCHAR);
                pstmt.setNull(10, Types.VARCHAR);

            } else if (eventAction instanceof DoNothingEventAction) {
                pstmt.setNull(4, Types.BOOLEAN);
                pstmt.setNull(5, Types.NUMERIC);
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.BLOB);
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.VARCHAR);
                pstmt.setNull(10, Types.VARCHAR);

            } else {
                throw new IllegalArgumentException("EventAction type not supported yet!");
            }

            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                eventAction.setId(rs.getLong(1));
                eventAction.setExecutionGroups(executionGroupRepository
                        .setEventActionExecutionGroups(eventAction.getId(),
                                eventAction.getExecutionGroups()));
                return eventAction;
            }
            throw new IllegalStateException("Error saving eventAction");
        });
    }

    public EventAction update(EventAction eventAction) {
        return getJdbcTemplate().execute((ConnectionCallback<EventAction>) con -> {
            PreparedStatement pstmt = con.prepareStatement("UPDATE event_actions SET dType = ?, trigger = ?, comment = ?, " +
                    "on_repeat = ?, volume = ?, filename = ?, file = ?, requestMethod = ?, url = ?, audio_device = ? WHERE id = ?");

            pstmt.setString(1, eventAction.getClass().getAnnotation(DiscriminatorValue.class).value());
            pstmt.setString(2, eventAction.getTrigger().name());
            pstmt.setString(3, eventAction.getComment());

            if (eventAction instanceof CallUrlEventAction) {
                CallUrlEventAction callUrlEventAction = (CallUrlEventAction) eventAction;
                pstmt.setNull(4, Types.BOOLEAN);
                pstmt.setNull(5, Types.NUMERIC);
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.BLOB);
                pstmt.setString(8, callUrlEventAction.getRequestMethod().name());
                pstmt.setString(9, callUrlEventAction.getUrl());
                pstmt.setNull(10, Types.VARCHAR);

            } else if (eventAction instanceof PlayAudioEventAction) {
                PlayAudioEventAction playAudioEventAction = (PlayAudioEventAction) eventAction;
                pstmt.setBoolean(4, playAudioEventAction.isOnRepeat());
                pstmt.setInt(5, playAudioEventAction.getVolume());
                pstmt.setString(6, playAudioEventAction.getFileName());
                pstmt.setBytes(7, playAudioEventAction.getFile());
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.VARCHAR);
                pstmt.setString(10, playAudioEventAction.getSoundDevice());

            } else if (eventAction instanceof ExecutePythonEventAction) {
                ExecutePythonEventAction executePythonEventAction = (ExecutePythonEventAction) eventAction;
                pstmt.setNull(4, Types.BOOLEAN);
                pstmt.setNull(5, Types.NUMERIC);
                pstmt.setString(6, executePythonEventAction.getFileName());
                pstmt.setBytes(7, executePythonEventAction.getFile());
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.VARCHAR);
                pstmt.setNull(10, Types.VARCHAR);

            } else if (eventAction instanceof DoNothingEventAction) {
                pstmt.setNull(4, Types.BOOLEAN);
                pstmt.setNull(5, Types.NUMERIC);
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.BLOB);
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.VARCHAR);
                pstmt.setNull(10, Types.VARCHAR);

            } else {
                throw new IllegalArgumentException("EventAction type not supported yet!");
            }

            pstmt.setLong(11, eventAction.getId());
            pstmt.executeUpdate();
            eventAction.setExecutionGroups(executionGroupRepository
                    .setEventActionExecutionGroups(eventAction.getId(),
                            eventAction.getExecutionGroups()));
            return eventAction;
        });
    }

    public Optional<EventAction> getById(long id) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<EventAction>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM event_actions WHERE id = ?");
            pstmt.setLong(1, id);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            if (rs.next()) {
                return Optional.of(parseRs(rs));
            }
            return Optional.empty();
        });
    }

    public List<EventAction> getAll() {
        return getJdbcTemplate().execute((ConnectionCallback<? extends List<EventAction>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM event_actions ORDER BY id");
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            List<EventAction> eventActions = new ArrayList<>();
            while (rs.next()) {
                eventActions.add(parseRs(rs));
            }
            return eventActions;
        });
    }

    public List<EventAction> getByTrigger(EventTrigger trigger) {
        return getJdbcTemplate().execute((ConnectionCallback<? extends List<EventAction>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM event_actions WHERE trigger = ? ORDER BY id");
            pstmt.setString(1, trigger.name());
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            List<EventAction> eventActions = new ArrayList<>();
            while (rs.next()) {
                eventActions.add(parseRs(rs));
            }
            return eventActions;
        });
    }

    public boolean delete(long id) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("DELETE from event_actions WHERE id = ?");
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() != 0;
        });
    }

    private EventAction parseRs(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        Map<Long, Set<String>> executionGroups = new HashMap<>();
        executionGroups.put(id, executionGroupRepository.getEventExecutionGroups(id));
        return parseRs(rs, executionGroups);
    }

    private EventAction parseRs(ResultSet rs, Map<Long, Set<String>> eventExecutionGroupsById) throws SQLException {
        long id = rs.getLong("id");
        Set<String> eventExecutionGroups = eventExecutionGroupsById.getOrDefault(id, new HashSet<>());
        String dType = rs.getString("dType");

        EventAction eventAction;
        if (Objects.equals(dType, CallUrlEventAction.class.getAnnotation(DiscriminatorValue.class).value())) {
            CallUrlEventAction callUrlEventAction = new CallUrlEventAction();
            callUrlEventAction.setUrl(rs.getString("url"));
            callUrlEventAction.setRequestMethod(RequestMethod.valueOf(rs.getString("requestMethod")));
            eventAction = callUrlEventAction;

        } else if (Objects.equals(dType, PlayAudioEventAction.class.getAnnotation(DiscriminatorValue.class).value())) {
            PlayAudioEventAction playAudioEventAction = new PlayAudioEventAction();
            playAudioEventAction.setOnRepeat(rs.getBoolean("on_repeat"));
            playAudioEventAction.setVolume(rs.getInt("volume"));
            playAudioEventAction.setFile(rs.getBytes("file"));
            playAudioEventAction.setFileName(rs.getString("filename"));
            playAudioEventAction.setSoundDevice(rs.getString("audio_device"));
            eventAction = playAudioEventAction;

        } else if (Objects.equals(dType, ExecutePythonEventAction.class.getAnnotation(DiscriminatorValue.class).value())) {
            ExecutePythonEventAction executePythonEventAction = new ExecutePythonEventAction();
            executePythonEventAction.setFile(rs.getBytes("file"));
            executePythonEventAction.setFileName(rs.getString("filename"));
            eventAction = executePythonEventAction;

        } else if (Objects.equals(dType, DoNothingEventAction.class.getAnnotation(DiscriminatorValue.class).value())) {
            eventAction = new DoNothingEventAction();

        } else {
            throw new IllegalArgumentException("Unknown dType: " + dType);
        }

        eventAction.setComment(rs.getString("comment"));
        if(eventAction.getComment() == null) {
            eventAction.setComment("");
        }
        eventAction.setExecutionGroups(eventExecutionGroups);
        eventAction.setTrigger(EventTrigger.valueOf(rs.getString("trigger")));
        eventAction.setId(id);
        return eventAction;
    }


}
