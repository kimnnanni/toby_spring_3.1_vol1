package springbook.user.service;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    UserDao userDao;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    private DataSource dataSource;

    private PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void upgradeLevels() throws Exception {
//        TransactionSynchronizationManager.initSynchronization(); //트랜잭션 동기화 관리자를 이용해 동기화 작업 초기화
//        //DB 커넥션 생성하고 트랜잭션 시작. 이후의 DAO 작업은 모두 여기서 시작한 트랜잭션 안에서 진행
//        Connection c = DataSourceUtils.getConnection(dataSource);
//        c.setAutoCommit(false);
//
//        try {
//            List<User> users = userDao.getAll();
//            for (User user : users)
//                if (canUpgradeLevel(user))
//                    upgradeLevel(user);
//
//            c.commit(); //성공시 커밋
//        } catch (Exception e) {
//            c.rollback(); //예외 발생시 롤백
//            throw e;
//        } finally {
//            DataSourceUtils.releaseConnection(c, dataSource); //스프링 유틸리티 메소드를 이요해 DB 커넥션 닫기
//            //동기화 작업 종료 및 정리
//            TransactionSynchronizationManager.unbindResource(this.dataSource);
//            TransactionSynchronizationManager.clearSynchronization();
//        }

        //2. 글로벌 트랜잭션

//        //3. 스프링의 트랜잭션 추상화
//        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource); //JDBC 트랜잭션 추상 오브잭트 생성
//        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition()); //트랜잭션 시작

        //트랜잭션 기술 설정의 분리
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = userDao.getAll();
            for(User user : users) {
                if(canUpgradeLevel(user))
                    upgradeLevel(user);
            }
            transactionManager.commit(status);
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel){
            case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level : " + currentLevel);
        }
    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }

    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
