package net.alex9849.cocktailpi.repository;

import jakarta.annotation.PostConstruct;
import net.alex9849.cocktailpi.model.Glass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GlassRepository extends JdbcDaoSupport {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private OptionsRepository optionsRepository;

    public static final String OPTION_DEFAULT_GLASS_ID = "Default_Glass_Id";
    public static final String OPTION_SINGLE_INGREDIENT_GLASS_ID = "Single_Ingredient_Glass_Id";

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public Long getDefaultGlassId() {
        String default_glass_id = optionsRepository.getOption(OPTION_DEFAULT_GLASS_ID).orElse(null);
        if(default_glass_id == null) {
            return null;
        }
        return Long.parseLong(default_glass_id);
    }

    public Long getSingleIngredientGlassId() {
        String default_glass_id = optionsRepository.getOption(OPTION_SINGLE_INGREDIENT_GLASS_ID).orElse(null);
        if(default_glass_id == null) {
            return null;
        }
        return Long.parseLong(default_glass_id);
    }

    public List<Glass> findByIds(Long... ids) {
        if(ids.length == 0) {
            return new ArrayList<>();
        }
        Long siGlassId = getSingleIngredientGlassId();
        Long defaultGlassId = getDefaultGlassId();

        return getJdbcTemplate().execute((ConnectionCallback<List<Glass>>) con -> {
            String stmt = "SELECT * FROM glasses WHERE id IN (";
            stmt += Arrays.stream(ids).map(x -> "?").collect(Collectors.joining(","));
            stmt += ") order by name";

            PreparedStatement pstmt = con.prepareStatement(stmt);
            for (int i = 0; i < ids.length; i++) {
                pstmt.setLong(i + 1, ids[i]);
            }
            ResultSet rs = pstmt.executeQuery();
            List<Glass> results = new ArrayList<>();
            while (rs.next()) {
                Glass glass = parseRs(rs);
                glass.setDefault(Objects.equals(glass.getId(), defaultGlassId));
                glass.setUseForSingleIngredients(Objects.equals(glass.getId(), siGlassId));
                results.add(glass);
            }
            return results;
        });
    }

    public Set<Long> findAllIds() {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT id FROM glasses");
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Optional<Glass> findById(long id) {
        List<Glass> found = findByIds(id);
        if(found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(found.get(0));
    }

    public Set<Long> findIdsByName(String name) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM glasses WHERE name = ? ORDER BY name ASC");
            pstmt.setString(1, name);
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public boolean update (Glass glass) {
        Long siGlassId = getSingleIngredientGlassId();
        Long defaultGlassId = getDefaultGlassId();
        return Boolean.TRUE.equals(getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("UPDATE glasses SET name = ?, size = ?, empty_weight = ? WHERE id = ?");
            pstmt.setString(1, glass.getName());
            pstmt.setLong(2, glass.getSize());
            if (glass.getEmptyWeight() != null) {
                pstmt.setObject(3, glass.getEmptyWeight());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setLong(4, glass.getId());
            if (glass.isDefault() && !Objects.equals(glass.getId(), defaultGlassId)) {
                optionsRepository.setOption(OPTION_DEFAULT_GLASS_ID, String.valueOf(glass.getId()));
            }
            if (!glass.isDefault() && Objects.equals(glass.getId(), defaultGlassId)) {
                optionsRepository.delOption(OPTION_DEFAULT_GLASS_ID, false);
            }
            if (glass.isUseForSingleIngredients() && !Objects.equals(glass.getId(), siGlassId)) {
                optionsRepository.setOption(OPTION_SINGLE_INGREDIENT_GLASS_ID, String.valueOf(glass.getId()));
            }
            if (!glass.isUseForSingleIngredients() && Objects.equals(glass.getId(), siGlassId)) {
                optionsRepository.delOption(OPTION_SINGLE_INGREDIENT_GLASS_ID, false);
            }
            return pstmt.executeUpdate() != 0;
        }));
    }

    public Glass create (Glass glass) {
        return getJdbcTemplate().execute((ConnectionCallback<Glass>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO glasses (name, size, empty_weight) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, glass.getName());
            pstmt.setLong(2, glass.getSize());
            if (glass.getEmptyWeight() != null) {
                pstmt.setObject(3, glass.getEmptyWeight());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (!rs.next()) {
                throw new IllegalStateException("Error saving glass");
            }
            glass.setId(rs.getLong(1));
            if(glass.isDefault()) {
                optionsRepository.setOption(OPTION_DEFAULT_GLASS_ID, String.valueOf(glass.getId()));
            }
            if(glass.isUseForSingleIngredients()) {
                optionsRepository.setOption(OPTION_SINGLE_INGREDIENT_GLASS_ID, String.valueOf(glass.getId()));
            }
            return glass;
        });
    }

    public boolean delete (long id) {
        Long siGlassId = getSingleIngredientGlassId();
        Long defaultGlassId = getDefaultGlassId();
        if(Objects.equals(id, siGlassId)) {
            optionsRepository.delOption(OPTION_SINGLE_INGREDIENT_GLASS_ID, false);
        }
        if(Objects.equals(id, defaultGlassId)) {
            optionsRepository.delOption(OPTION_DEFAULT_GLASS_ID, false);
        }

        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("DELETE FROM glasses WHERE id = ?");
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() != 0;
        });
    }

    private Glass parseRs(ResultSet rs) throws SQLException {
        Glass glass = new Glass();
        glass.setId(rs.getLong("id"));
        glass.setName(rs.getString("name"));
        glass.setSize(rs.getLong("size"));
        glass.setEmptyWeight((Integer) rs.getObject("empty_weight"));
        return glass;
    }
}
