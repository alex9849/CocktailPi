package net.alex9849.cocktailpi.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;

@Component
public class RecipeCategoryRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public void addToCategory(long recipeId, long categoryId) {
        getJdbcTemplate().execute((ConnectionCallback<Void>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO recipe_categories (recipe_id, categories_id) VALUES (?, ?)");
            pstmt.setLong(1, recipeId);
            pstmt.setLong(2, categoryId);
            pstmt.executeUpdate();
            return null;
        });

    }

    public void removeFromAllCategories(long recipeId) {
        getJdbcTemplate().execute((ConnectionCallback<Void>) con -> {
            PreparedStatement pstmt = con.prepareStatement("DELETE FROM recipe_categories WHERE recipe_id = ?");
            pstmt.setLong(1, recipeId);
            pstmt.executeUpdate();
            return null;
        });
    }
}
