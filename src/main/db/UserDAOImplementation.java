package main.db;

import main.model.User;
import main.model.enums.UserRole;
import main.rowmapper.UserMapper;

import java.sql.SQLException;
import java.util.List;

public class UserDAOImplementation implements UserDAO{
    private final DBManager db;
    private final UserMapper mapper;
    private QueryBuilder builder;

    public UserDAOImplementation() {
        try {
            db = DBManager.getInstance();
            mapper = new UserMapper();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // CRUD OPERATIONS
    // TODO: choose interface or abstract class, implemented search methods must be private (searchAll... only accessible methods)
    @Override
    public QueryResult<User> search(String sql, Object... params) {
        return db.execute_query(sql, mapper, params);
    }

    // public method for user interface
    public List<User> searchAll() {
        builder = QueryBuilder.create();
        builder.select()
               .from("USERS");

        String sql = builder.getQuery();
        return search(sql).getResults();
    }

    public User searchById(int id) {
        builder = QueryBuilder.create();
        builder.select()
               .from("USERS")
               .where("id = ?")
               .addParameter(id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        return search(sql, params).getSingleResult().orElse(null);
    }

    public User searchByUsername(String username) {
        builder = QueryBuilder.create();
        builder.select()
               .from("USERS")
               .where("username = ?")
               .addParameter(username.toLowerCase());

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        return search(sql, params).getSingleResult().orElse(null);
    }

    public List<User> searchUsersInfo() {
        builder = QueryBuilder.create();
        builder.select()
               .from("USERS")
               .leftJoin("USER_INFO", "USERS.id = USER_INFO.ID");

        String sql = builder.getQuery();
        return search(sql).getResults();
    }

    public User searchUserInfoById(int id) {
        builder = QueryBuilder.create();
        builder.select()
               .from("USERS")
               .leftJoin("USER_INFO", "USERS.id = USER_INFO.ID")
               .where("USERS.id = ?")
               .addParameter(id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        return search(sql, params).getSingleResult().orElse(null);
    }

    @Override
    public void insert(String username, String password, UserRole role, String nome, String cognome) {
        db.execute_transaction(() -> {
            Long userId = insertUser(username, password, role);
            insertUserInfo(userId, nome, cognome);
            return null;
        });
    }

    private Long insertUser(String username, String password, UserRole role) {
        builder = QueryBuilder.create();
        builder.insertInto("USERS", "username", "password", "role")
                .withEnumColumn("role", "role_type")
                .values(username, password, role.toString());

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();

        QueryResult<User> result = db.execute_query(sql, mapper, params);
        Long userId = result.getGeneratedKey().orElse(null);

        if(userId == null) {
            throw new RuntimeException("Error inserting user!");
        }

        return userId;
    }

    private void insertUserInfo(Long userId, String nome, String cognome) {
        builder = QueryBuilder.create();
        builder.insertInto("USER_INFO", "id", "nome", "cognome")
               .values(userId, nome, cognome);
        String sql = builder.getQuery();
        Object[] params = builder.getParameters();

        QueryResult<User> result = db.execute_query(sql, mapper, params);
        if(result.getGeneratedKey().isEmpty()) {
            throw new RuntimeException("Error inserting user info!");
        }
    }

    @Override
    public QueryResult<User> update(String sql, Object... params) {
        return db.execute_query(sql, mapper, params);
    }

    public void UpdateCredentials(int id, String username, String password) {
        builder = QueryBuilder.create();
        builder.update("USERS")
               .set("username", username)
               .set("password", password)
               .where("id = ?")
               .addParameter(id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        update(sql, params);
    }

    public void UpdateRole(int id, UserRole role) {
        builder = QueryBuilder.create();
        builder.update("USERS")
               .set("role", role.toString(), true)
               .where("id = ?")
               .addParameter(id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        update(sql, params);
    }

    public void UpdateUserInfo(int id, String nome, String cognome, String indirizzo, String cap, String provincia, String stato) {
        builder = QueryBuilder.create();
        builder.update("USER_INFO")
                .set("nome", nome)
                .set("cognome", cognome)
                .set("indirizzo", indirizzo)
                .set("cap", cap)
                .set("provincia", provincia)
                .set("stato", stato)
                .where("id = ?")
                .addParameter(id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        update(sql, params);
    }

    @Override
    public QueryResult<User> delete(String sql, Object... params) {
        return db.execute_query(sql, mapper, params);
    }

    public void deleteUser(int id) {
        builder = QueryBuilder.create();
        builder.deleteFrom("USERS")
                .where("id = ?")
                .addParameter(id);

        String sql = builder.getQuery();
        Object[] params = builder.getParameters();
        delete(sql, params);
    }
}
