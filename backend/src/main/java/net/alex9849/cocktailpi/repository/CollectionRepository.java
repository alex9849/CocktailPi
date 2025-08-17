package net.alex9849.cocktailpi.repository;

import jakarta.annotation.PostConstruct;
import net.alex9849.cocktailpi.model.Collection;
import net.alex9849.cocktailpi.utils.SpringUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CollectionRepository extends JdbcDaoSupport {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private RecipeRepository recipeRepository;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    public Collection create(Collection collection) {
        return getJdbcTemplate().execute((ConnectionCallback<Collection>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO collections (name, normal_name, description, owner_id) " +
                    "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, collection.getName());
            pstmt.setString(2, collection.getNormalName());
            pstmt.setString(3, collection.getDescription());
            pstmt.setLong(4, collection.getOwner().getId());
            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                collection.setId(rs.getLong(1));
                return collection;
            }
            throw new IllegalStateException("Error saving collection");
        });
    }

    public boolean update(Collection collection) {
        return Boolean.TRUE.equals(getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("UPDATE collections SET name = ?, normal_name = ?, " +
                    "description = ?, owner_id = ?, last_update = CURRENT_TIMESTAMP WHERE id = ?");
            pstmt.setString(1, collection.getName());
            pstmt.setString(2, collection.getNormalName());
            pstmt.setString(3, collection.getDescription());
            pstmt.setLong(4, collection.getOwner().getId());
            pstmt.setLong(5, collection.getId());
            return pstmt.executeUpdate() != 0;
        }));
    }

    public boolean addRecipe(long collectionId, long recipeId) {
        return Boolean.TRUE.equals(getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO collection_recipes (recipe_id, collection_id) " +
                    "VALUES (?, ?)");
            pstmt.setLong(1, recipeId);
            pstmt.setLong(2, collectionId);
            return pstmt.executeUpdate() != 0;
        }));
    }

    public boolean removeRecipe(long collectionId, long recipeId) {
        return Boolean.TRUE.equals(getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("DELETE FROM collection_recipes WHERE " +
                    "recipe_id = ? AND collection_id = ?");
            pstmt.setLong(1, recipeId);
            pstmt.setLong(2, collectionId);
            return pstmt.executeUpdate() != 0;
        }));
    }

    public boolean delete(long id) {
        return getJdbcTemplate().execute((ConnectionCallback<Boolean>) con -> {
            PreparedStatement pstmt = con.prepareStatement("DELETE FROM collections WHERE id = ?");
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() != 0;
        });
    }

    public Set<Long> findIdsOwnedByUser(long userId) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT id as id FROM collections " +
                    "WHERE owner_id = ?");
            pstmt.setLong(1, userId);
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Set<Long> findAllIds() {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT id as id FROM collections");
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public Set<Long> findIdsByName(String name) {
        return getJdbcTemplate().execute((ConnectionCallback<Set<Long>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT id as id FROM collections WHERE lower(normal_name) = ? or lower(name) = ?");
            pstmt.setString(1, SpringUtility.normalize(name));
            pstmt.setString(2, name);
            pstmt.executeQuery();
            return DbUtils.executeGetIdsPstmt(pstmt);
        });
    }

    public List<Collection> findByIds(Long... ids) {
        if(ids.length == 0) {
            return new ArrayList<>();
        }
        return getJdbcTemplate().execute((ConnectionCallback<List<Collection>>) con -> {
            String idQuestionmarks = Arrays.stream(ids).map(x -> "?").collect(Collectors.joining(","));
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM collections c " +
                    "WHERE c.id IN (" + idQuestionmarks + ") order by c.name");
            for(int i = 0; i < ids.length; i++) {
                pstmt.setLong(i + 1, ids[i]);
            }
            ResultSet rs = pstmt.executeQuery();
            List<Collection> results = new ArrayList<>();
            while (rs.next()) {
                results.add(parseRs(rs));
            }
            return results;
        });
    }

    public void setImage(long collectionId, byte[] image) {
        getJdbcTemplate().execute((ConnectionCallback<Void>) con -> {
            if (image == null) {
                PreparedStatement deleteImagePstmt = con.prepareStatement("UPDATE collections SET image = NULL, last_update = CURRENT_TIMESTAMP where id = ?");
                deleteImagePstmt.setLong(1, collectionId);
                deleteImagePstmt.executeUpdate();
                return null;
            }
            PreparedStatement updateLobOidPstmt = con.prepareStatement("UPDATE collections SET image = ?, last_update = CURRENT_TIMESTAMP where id = ?");
            updateLobOidPstmt.setBytes(1, image);
            updateLobOidPstmt.setLong(2, collectionId);
            updateLobOidPstmt.executeUpdate();
            return null;
        });
    }

    public Optional<byte[]> getImage(long collectionId) {
        return getJdbcTemplate().execute((ConnectionCallback<Optional<byte[]>>) con -> {
            PreparedStatement pstmt = con.prepareStatement("SELECT image FROM collections where id = ?");
            pstmt.setLong(1, collectionId);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSet.getBytes("image"));
            }
            return Optional.empty();
        });
    }

    private Collection populateEntity(Collection collection) {
        collection.setSize(recipeRepository.findIdsInCollection(collection.getId()).size());
        return collection;
    }

    private Collection parseRs(ResultSet rs) throws SQLException {
        Collection collection = new Collection();
        collection.setId(rs.getLong("id"));
        collection.setName(rs.getString("name"));
        collection.setDescription(rs.getString("description"));
        collection.setOwnerId(rs.getLong("owner_id"));
        collection.setHasImage(rs.getObject("image") != null);
        collection.setLastUpdate(rs.getTimestamp("last_update"));
        return populateEntity(collection);
    }
}
