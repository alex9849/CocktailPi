package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public Category create(Category category) {
        return getJdbcTemplate().execute((ConnectionCallback<Category>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO categories (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, category.getName());
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                category.setId(rs.getLong(1));
                return category;
            }
            throw new IllegalStateException("Error saving category");
        });
    }

    public List<Category> findAll() {
        return getJdbcTemplate().execute((ConnectionCallback<List<Category>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM categories ORDER BY name ASC");
            ResultSet rs = pstmt.executeQuery();
            List<Category> result = new ArrayList<>();
            while (rs.next()) {
                result.add(parseRs(rs));
            }
            return result;
        });
    }

    public Optional<Category> findByNameIgnoringCase(String name) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<Category>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM categories WHERE lower(name) = lower(?)");
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(parseRs(rs));
            }
            return Optional.empty();
        });
    }

    public List<Category> findByRecipeId(long recipeId) {
        return getJdbcTemplate().execute((ConnectionCallback<List<Category>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT c.* FROM categories c " +
                    "join recipe_categories rc on c.id = rc.categories_id where rc.recipe_id = ?");
            pstmt.setLong(1, recipeId);
            ResultSet rs = pstmt.executeQuery();
            List<Category> result = new ArrayList<>();
            while (rs.next()) {
                result.add(parseRs(rs));
            }
            return result;
        });
    }

    public Optional<Category> findById(long id) {
        List<Category> found = findByIds(id);
        if(found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(found.get(0));
    }

    public List<Category> findByIds(Long... ids) {
        if(ids == null) {
            return new ArrayList<>();
        }
        return getJdbcTemplate().execute((ConnectionCallback<List<Category>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM categories where id = ANY(?) " +
                    "ORDER BY name ASC");
            pstmt.setObject(1, con.createArrayOf("int8", ids));
            ResultSet rs = pstmt.executeQuery();
            List<Category> categories = new ArrayList<>();
            if (rs.next()) {
                categories.add(parseRs(rs));
            }
            return categories;
        });
    }

    public boolean update(Category category) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("UPDATE categories SET name = ? WHERE id = ?");
            pstmt.setString(1, category.getName());
            pstmt.setLong(2, category.getId());
            return pstmt.executeUpdate() != 0;
        });
    }

    public boolean delete(long id) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("DELETE FROM categories WHERE id = ?");
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() != 0;
        });
    }

    private Category parseRs(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setName(rs.getString("name"));
        category.setId(rs.getLong("id"));
        return category;
    }
}
