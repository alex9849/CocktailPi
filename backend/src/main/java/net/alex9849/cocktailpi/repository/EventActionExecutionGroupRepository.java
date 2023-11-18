package net.alex9849.cocktailpi.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EventActionExecutionGroupRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public Set<String> setEventActionExecutionGroups(long eventActionId, Set<String> groups) {
        getJdbcTemplate().execute((ConnectionCallback<Void>) con -> {
            PreparedStatement pstmt = con.prepareStatement("DELETE FROM event_actions_execution_groups where id = ?");
            pstmt.setLong(1, eventActionId);
            pstmt.executeUpdate();
            return null;
        });
        if(groups.isEmpty()) {
            return new HashSet<>();
        }
        getJdbcTemplate().execute((ConnectionCallback<Void>) con -> {
            String query = "INSERT INTO event_actions_execution_groups (id, \"group\") VALUES ";
            query += groups.stream().map(x -> "(?, ?)").collect(Collectors.joining(","));
            PreparedStatement pstmt = con.prepareStatement(query);
            int index = 0;
            for(String group : groups) {
                pstmt.setLong(index * 2 + 1, eventActionId);
                pstmt.setString(index * 2 + 2, group);
                index++;
            }
            pstmt.execute();
            return null;
        });
        return groups;
    }

    public Set<String> getEventExecutionGroups(long eventActionId) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<String>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT \"group\" from event_actions_execution_groups where id = ?");
            pstmt.setLong(1, eventActionId);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            Set<String> groups = new HashSet<>();
            while (rs.next()) {
                groups.add(rs.getString("group"));
            }
            return groups;
        });
    }

    public Map<Long, Set<String>> getAllEventExecutionGroupsByEventActionId() {
        return getJdbcTemplate().execute((ConnectionCallback<Map<Long, Set<String>>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT id, \"group\" from event_actions_execution_groups");
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            Map<Long, Set<String>> groups = new HashMap<>();
            while (rs.next()) {
                Set<String> set = groups.putIfAbsent(rs.getLong("id"), new HashSet<>());
                set.add(rs.getString("group"));
            }
            return groups;
        });
    }

    public List<String> getAllEventExecutionGroups() {
        return getJdbcTemplate().execute((ConnectionCallback<List<String>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT DISTINCT \"group\" from event_actions_execution_groups ORDER BY \"group\" asc");
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            List<String> groups = new ArrayList<>();
            while (rs.next()) {
                groups.add(rs.getString("group"));
            }
            return groups;
        });
    }
}
