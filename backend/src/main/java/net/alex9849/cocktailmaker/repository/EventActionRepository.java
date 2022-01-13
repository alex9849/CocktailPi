package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.eventaction.*;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.persistence.DiscriminatorValue;
import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

@Repository
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
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO event_actions (dType, trigger, comment, on_repeat, filename, " +
                    "requestMethod, url) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, eventAction.getClass().getAnnotation(DiscriminatorValue.class).value());
            pstmt.setString(2, eventAction.getTrigger().name());
            pstmt.setString(3, eventAction.getComment());
            if (eventAction instanceof CallUrlEventAction) {
                CallUrlEventAction callUrlEventAction = (CallUrlEventAction) eventAction;
                pstmt.setNull(4, Types.BOOLEAN);
                pstmt.setNull(5, Types.VARCHAR);
                pstmt.setString(6, callUrlEventAction.getRequestMethod().name());
                pstmt.setString(7, callUrlEventAction.getUrl());

            } else if (eventAction instanceof PlayAudioEventAction) {
                PlayAudioEventAction playAudioEventAction = (PlayAudioEventAction) eventAction;
                pstmt.setBoolean(4, playAudioEventAction.isOnRepeat());
                pstmt.setString(5, playAudioEventAction.getFileName());
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.VARCHAR);

            } else if (eventAction instanceof ExecutePythonEventAction) {
                ExecutePythonEventAction executePythonEventAction = (ExecutePythonEventAction) eventAction;
                pstmt.setNull(4, Types.BOOLEAN);
                pstmt.setString(5, executePythonEventAction.getFileName());
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.VARCHAR);

            } else {
                throw new IllegalArgumentException("EventAction type not supported yet!");
            }
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                eventAction.setId(rs.getLong(1));
                eventAction.setExecutionGroup(executionGroupRepository
                        .getEventExecutionGroups(eventAction.getId()));
                if(eventAction instanceof FileEventAction) {
                    setFile(eventAction.getId(), ((FileEventAction) eventAction).getFile());
                }
                return eventAction;
            }
            throw new IllegalStateException("Error saving eventAction");
        });
    }

    private void setFile(long eventActionId, byte[] file) {
        getJdbcTemplate().execute((ConnectionCallback<Void>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT file FROM event_actions where id = ? AND file IS NOT NULL");
            pstmt.setLong(1, eventActionId);
            ResultSet resultSet = pstmt.executeQuery();
            boolean autoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            LargeObjectManager lobApi = con.unwrap(PGConnection.class).getLargeObjectAPI();
            Long fileOid;
            if (resultSet.next()) {
                fileOid = resultSet.getObject("file", Long.class);
            } else {
                fileOid = lobApi.createLO(LargeObjectManager.READWRITE);
            }
            LargeObject lobObject = lobApi.open(fileOid, LargeObjectManager.READWRITE);
            lobObject.write(file);
            PreparedStatement updateLobOidPstmt = con.prepareStatement("UPDATE event_actions SET file = ? where id = ?");
            updateLobOidPstmt.setLong(1, fileOid);
            updateLobOidPstmt.setLong(2, eventActionId);
            updateLobOidPstmt.executeUpdate();
            con.commit();
            con.setAutoCommit(autoCommit);
            return null;
        });
    }

    private Optional<byte[]> getFile(long eventActionId) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<byte[]>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT file FROM event_actions where id = ?");
            pstmt.setLong(1, eventActionId);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                Long imageOid = resultSet.getObject("file", Long.class);
                if (imageOid == null) {
                    return Optional.empty();
                }
                LargeObjectManager lobApi = con.unwrap(PGConnection.class).getLargeObjectAPI();
                LargeObject imageLob = lobApi.open(imageOid, LargeObjectManager.READ);
                byte buf[] = new byte[imageLob.size()];
                imageLob.read(buf, 0, buf.length);
                return Optional.of(buf);
            }
            return Optional.empty();
        });
    }

    public EventAction update(EventAction eventAction) {
        return getJdbcTemplate().execute((ConnectionCallback<EventAction>) con -> {
            PreparedStatement pstmt = con.prepareStatement("UPDATE event_actions SET dType = ?, trigger = ?, comment = ?, " +
                    "on_repeat = ?, filename = ?, requestMethod = ?, url = ? WHERE id = ?");

            pstmt.setString(1, eventAction.getClass().getAnnotation(DiscriminatorValue.class).value());
            pstmt.setString(2, eventAction.getTrigger().name());
            pstmt.setString(3, eventAction.getComment());
            if (eventAction instanceof CallUrlEventAction) {
                CallUrlEventAction callUrlEventAction = (CallUrlEventAction) eventAction;
                pstmt.setNull(4, Types.BOOLEAN);
                pstmt.setNull(5, Types.VARCHAR);
                pstmt.setString(6, callUrlEventAction.getRequestMethod().name());
                pstmt.setString(7, callUrlEventAction.getUrl());

            } else if (eventAction instanceof PlayAudioEventAction) {
                PlayAudioEventAction playAudioEventAction = (PlayAudioEventAction) eventAction;
                pstmt.setBoolean(4, playAudioEventAction.isOnRepeat());
                pstmt.setString(5, playAudioEventAction.getFileName());
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.VARCHAR);

            } else if (eventAction instanceof ExecutePythonEventAction) {
                ExecutePythonEventAction executePythonEventAction = (ExecutePythonEventAction) eventAction;
                pstmt.setNull(4, Types.BOOLEAN);
                pstmt.setString(5, executePythonEventAction.getFileName());
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.VARCHAR);

            } else {
                throw new IllegalArgumentException("EventAction type not supported yet!");
            }

            pstmt.setLong(8, eventAction.getId());
            pstmt.executeUpdate();
            eventAction.setExecutionGroup(executionGroupRepository
                    .getEventExecutionGroups(eventAction.getId()));
            if(eventAction instanceof FileEventAction) {
                setFile(eventAction.getId(), ((FileEventAction) eventAction).getFile());
            }
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
            playAudioEventAction.setFile(getFile(id).orElseThrow());
            playAudioEventAction.setFileName(rs.getString("filename"));
            eventAction = playAudioEventAction;

        } else if (Objects.equals(dType, ExecutePythonEventAction.class.getAnnotation(DiscriminatorValue.class).value())) {
            ExecutePythonEventAction executePythonEventAction = new ExecutePythonEventAction();
            executePythonEventAction.setFile(getFile(id).orElseThrow());
            executePythonEventAction.setFileName(rs.getString("filename"));
            eventAction = executePythonEventAction;

        } else {
            throw new IllegalArgumentException("Unknown dType: " + dType);
        }

        eventAction.setComment(rs.getString("comment"));
        if(eventAction.getComment() == null) {
            eventAction.setComment("");
        }
        eventAction.setExecutionGroup(eventExecutionGroups);
        eventAction.setTrigger(EventTrigger.valueOf(rs.getString("trigger")));
        eventAction.setId(id);
        return eventAction;
    }


}
