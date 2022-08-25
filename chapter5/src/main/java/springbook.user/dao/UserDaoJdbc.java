package springbook.user.dao;

import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoJdbc {
    private DataSource dataSource;
    private ConnectionMaker connectionMaker;

    private JdbcContext jdbcContext;

    private JdbcTemplate jdbcTemplate;

    public void setJdbcContext(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public UserDaoJdbc(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public UserDaoJdbc() {}

    private RowMapper<User> userRowMapper =
        new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setId(resultSet.getString("id"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                user.setLevel(Level.valueOf(resultSet.getInt("level")));
                user.setLogin(resultSet.getInt("login"));
                user.setRecommend(resultSet.getInt("recommend"));
                return user;
            }
        };

    public void add(User user) {
        this.jdbcTemplate.update(
        "insert into users(id, name, password, level, login, recommend) " +
            "values(?,?,?,?,?,?)", user.getId(), user.getName(),
            user.getPassword(), user.getLevel().intValue(),
            user.getLogin(), user.getRecommend()
        );
    }

//    public void add(final User user) throws ClassNotFoundException, SQLException {
//
//        this.jdbcContext.workWithStatementStrategy(
//            new StatementStrategy() {
//                @Override
//                public PreparedStatement makePreparedStatment(Connection c) throws SQLException {
//                    PreparedStatement ps = c.prepareStatement("insert into user(id, name, password) values(?,?,?)");
//                    ps.setString(1, user.getId());
//                    ps.setString(2, user.getName());
//                    ps.setString(3, user.getPassword());
//
//                    return ps;
//                }
//            }
//        );
//    }

    public void add() throws DuplicateUserIdException {
        try {
            executeSql("test");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection c = connectionMaker.makeConnection();

        PreparedStatement ps = c.prepareStatement("");
        ResultSet rs = ps.executeQuery();

        User user = null;
        if(rs.next()) {
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if(user == null) throw new EmptyResultDataAccessException(1);

        return user;
    }

    //public abstract Connection getConnection() throws ClassNotFoundException, SQLException;

    public void deleteAll() throws SQLException {
        //executeSql("delete from users");
        this.jdbcContext.executeSql("delete from users");
    }

    private void executeSql(final String sql) throws SQLException {
        this.jdbcContext.workWithStatementStrategy(
            new StatementStrategy() {
                @Override
                public PreparedStatement makePreparedStatment(Connection c) throws SQLException {
                    return c.prepareStatement(sql);
                }
            }
        );
    }

    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();
            ps = stmt.makePreparedStatment(c);
            ps.executeUpdate();
        } catch(SQLException e) {
            throw e;
        } finally {
            if(ps!=null) try{ ps.close(); } catch(SQLException e){ }
            if(c!=null) try{ c.close(); } catch(SQLException e){ }
        }
    }

    public int getCount() throws SQLException {
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement("select count(*) from users");
        ResultSet rs = ps.executeQuery();
        rs.next();
        int count = rs.getInt(1);

        rs.close();
        ps.close();
        c.close();

        return count;
    }

//    protected abstract PreparedStatement makeStatement(Connection c) throws SQLException;

    //abstract protected PreparedStatement makeStatement(Connection c) throws SQLException;
}

