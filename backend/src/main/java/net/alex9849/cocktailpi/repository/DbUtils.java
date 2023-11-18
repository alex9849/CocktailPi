package net.alex9849.cocktailpi.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

class DbUtils {

    private DbUtils() {}

    /**
     * @param pstmt needs to produce a resultset with exactly one numeric attribute
     * @return a parsed set of longs
     */
    static Set<Long> executeGetIdsPstmt(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();
        Set<Long> results = new HashSet<>();
        while (rs.next()) {
            results.add(rs.getLong(1));
        }
        return results;
    }
}
