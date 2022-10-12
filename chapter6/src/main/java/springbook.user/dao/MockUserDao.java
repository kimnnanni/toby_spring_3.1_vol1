package springbook.user.dao;

import springbook.user.domain.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MockUserDao implements UserDao {
    private List<User> users; //레벨 업그레이드 후보 User 오브젝트 모록
    private List<User> updated = new ArrayList<>(); //업그레이드 대상 오브젝트를 저장해둘 목록

    public MockUserDao(List<User> users) {
        this.users = users;
    }

    public List<User> getUpdated() {
        return this.updated;
    }

    public List<User> getAll() {
        return this.users;
    }

    public void update(User user) {
        updated.add(user);
    }

    @Override
    public void add(User user) {throw new UnsupportedOperationException();}
    @Override
    public User get(String id) throws SQLException, ClassNotFoundException {throw new UnsupportedOperationException();}
    @Override
    public void deleteAll() throws SQLException {throw new UnsupportedOperationException();}
    @Override
    public int getCount() throws SQLException {throw new UnsupportedOperationException();}
}
