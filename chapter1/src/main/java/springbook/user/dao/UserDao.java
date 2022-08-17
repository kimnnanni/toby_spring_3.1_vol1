package springbook.user.dao;

import springbook.user.domain.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class UserDao {
    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection c = this.getConnection();
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection c = this.getConnection();

        return new User();
    }

    public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
}

