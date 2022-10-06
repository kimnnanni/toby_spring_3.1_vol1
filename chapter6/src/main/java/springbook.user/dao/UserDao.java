package springbook.user.dao;

import springbook.user.domain.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    void add(User user);
    public User get(String id) throws SQLException, ClassNotFoundException;
    List<User> getAll();
    public void deleteAll() throws SQLException;
    public int getCount() throws SQLException;
    void update(User user);
}
