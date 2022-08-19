package springbook.user.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.*;

public class UserDao {
    private DataSource dataSource;
    private ConnectionMaker connectionMaker;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public UserDao() {}

    public void add(final User user) throws ClassNotFoundException, SQLException {
//        Connection c = dataSource.getConnection();
//        class AddStatemenet implements StatementStrategy {
//            public PreparedStatement makePreparedStatment(Connection c) throws SQLException {
//                PreparedStatement ps = c.prepareStatement("insert into user(id, name, password) values(?,?,?)");
//                ps.setString(1, user.getId());
//                ps.setString(2, user.getName());
//                ps.setString(3, user.getPassword());
//
//                return ps;
//            }
//        }
//
//        //StatementStrategy st = new AddStatement(user);
//        StatementStrategy st = new StatementStrategy() {
//            @Override
//            public PreparedStatement makePreparedStatment(Connection c) throws SQLException {
//                PreparedStatement ps = c.prepareStatement("insert into user(id, name, password) values(?,?,?)");
//                ps.setString(1, user.getId());
//                ps.setString(2, user.getName());
//                ps.setString(3, user.getPassword());
//
//                return ps;
//            }
//        };
//        jdbcContextWithStatementStrategy(st);

        jdbcContextWithStatementStrategy(
            new StatementStrategy() {
                @Override
                public PreparedStatement makePreparedStatment(Connection c) throws SQLException {
                    PreparedStatement ps = c.prepareStatement("insert into user(id, name, password) values(?,?,?)");
                    ps.setString(1, user.getId());
                    ps.setString(2, user.getName());
                    ps.setString(3, user.getPassword());

                    return ps;
                }
            }
        );
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
//        StatementStrategy st = new DeleteAllStatement();
//        jdbcContextWithStatementStrategy(st);
        jdbcContextWithStatementStrategy(
            new StatementStrategy() {
                @Override
                public PreparedStatement makePreparedStatment(Connection c) throws SQLException {
                    return c.prepareStatement("delete from users");
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

    //abstract protected PreparedStatement makeStatement(Connection c) throws SQLException;
}

