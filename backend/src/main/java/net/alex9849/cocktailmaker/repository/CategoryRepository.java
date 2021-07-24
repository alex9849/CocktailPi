package net.alex9849.cocktailmaker.repository;

import net.alex9849.cocktailmaker.model.Category;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryRepository {
    private final DataSource dataSource;

    public CategoryRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Category create(Category category) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO categories (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, category.getName());
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                category.setId(rs.getLong(1));
                return category;
            }
            throw new IllegalStateException("Error saving category");
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error saving category", throwables);
        }
    }

    public List<Category> findAll() {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM categories");
            ResultSet rs = pstmt.executeQuery();
            List<Category> result = new ArrayList<>();
            while (rs.next()) {
                result.add(parseRs(rs));
            }
            return result;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading category", throwables);
        }
    }

    public Optional<Category> findByNameIgnoringCase(String name) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM categories WHERE lower(name) = lower(?)");
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(parseRs(rs));
            }
            return Optional.empty();
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading category", throwables);
        }
    }

    public List<Category> findByRecipeId(long recipeId) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT c.* FROM categories c " +
                    "join recipe_categories rc on c.id = rc.categories_id where rc.recipe_id = ?");
            pstmt.setLong(1, recipeId);
            ResultSet rs = pstmt.executeQuery();
            List<Category> result = new ArrayList<>();
            while (rs.next()) {
                result.add(parseRs(rs));
            }
            return result;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading category", throwables);
        }
    }

    public Optional<Category> findById(long id) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM categories WHERE id = ?");
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(parseRs(rs));
            }
            return Optional.empty();
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error loading category", throwables);
        }
    }

    public boolean update(Category category) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("UPDATE categories SET name = ? WHERE id = ?");
            pstmt.setString(1, category.getName());
            pstmt.setLong(2, category.getId());
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error updating category", throwables);
        }
    }

    public boolean delete(long id) {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement("DELETE FROM categories WHERE id = ?");
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new ServerErrorException("Error deleting category", throwables);
        }
    }

    private Category parseRs(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setName(rs.getString("name"));
        category.setId(rs.getLong("id"));
        return category;
    }
}
