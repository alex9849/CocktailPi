package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.eventaction.EventAction;
import net.alex9849.cocktailmaker.model.eventaction.EventTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.persistence.DiscriminatorValue;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO event_actions (dType, trigger) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, eventAction.getClass().getAnnotation(DiscriminatorValue.class).value());
            pstmt.setString(2, eventAction.getTrigger().name());
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                eventAction.setId(rs.getLong(1));
                eventAction.setExecutionGroup(executionGroupRepository
                        .getEventExecutionGroups(eventAction.getId()));
                return eventAction;
            }
            throw new IllegalStateException("Error saving eventAction");
        });
    }

    public EventAction update(EventAction eventAction) {
        return getJdbcTemplate().execute((ConnectionCallback<EventAction>) con -> {
            PreparedStatement pstmt = con.prepareStatement("UPDATE event_actions SET dType = ?, trigger = ? WHERE id = ?");
            pstmt.setString(1, eventAction.getClass().getAnnotation(DiscriminatorValue.class).value());
            pstmt.setString(2, eventAction.getTrigger().name());
            pstmt.setLong(3, eventAction.getId());
            pstmt.executeUpdate();
            eventAction.setExecutionGroup(executionGroupRepository
                    .getEventExecutionGroups(eventAction.getId()));
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
        executionGroupRepository.getEventExecutionGroups(id);

        //TODO
        return null;
    }


}
