package springbook.user.dao;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        //ConnectionMaker connectionMaker = new DConnectionMaker();
        ConnectionMaker connectionMaker = new NConnectionMaker();

        UserDao dao = new UserDao(connectionMaker);
        //1. UserDao create
        //2. ConnectionMaker Type 제공 - 의존관계 설정
    }
}
