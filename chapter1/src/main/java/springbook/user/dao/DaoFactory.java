package springbook.user.dao;

public class DaoFactory {
    public UserDao userDao() {
        return new UserDao(connectionMaker());
    }

//    public AccountDao accountDao() {
//        return new AccountDao(new DConnectionMaker()); --> return new AccountDao(connectionMaker());
//    }
//
//    public MessageDao messageDao() {
//        return new MessageDao(new DConnectionMaker()); --> return new MessageDao(connectionMaker());
//    }

    public ConnectionMaker connectionMaker() {
        return new DConnectionMaker();
    }
}
