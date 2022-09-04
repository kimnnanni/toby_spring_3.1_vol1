package springbook.user.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class UserService {
    UserDao userDao;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    private DataSource dataSource;

    private PlatformTransactionManager transactionManager;

    private MailSender mailSender;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
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
//        //1차 구현
//        if(user.getLevel() == Level.BASIC) user.setLevel(Level.SILVER);
//        else if(user.getLevel() == Level.SILVER) user.setLevel(Level.GOLD);
//        //2차 리팩토링
        user.upgradeLevel();
        userDao.update(user);
        //5.4 메일 서비스 추상화
        sendUpgradeEMail(user);
    }

    private void sendUpgradeEMail(User user) {
//        //1. 기본 java.mail 사용 방법
//        Properties props = new Properties();
//        props.put("mail.smtp.host", "mail.ksug.org");
//        Session s = Session.getInstance(props, null);
//
//        MimeMessage message = new MimeMessage(s);
//        try {
//            message.setFrom(new InternetAddress("useradmin@ksug.org"));
//            message.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(user.getEmail())));
//            message.setSubject("Upgrade 안내");
//            message.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다.");
//
//            Transport.send(message);
//        } catch(AddressException e) {
//            throw new RuntimeException(e);
//        } catch(MessagingException e) {
//            throw new RuntimeException(e);
//        } catch(UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        }
//        //2. JavaMail의 서비스 추상화 인터페이스 방법
//        //MailSender 구현 클래스의 오브젝트를 생성한다.
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("mail.server.com");
//
//        //MailMessage 인터페이스와 구현 클래스 오브젝트를 만들어 메일 내용을 작성한다.
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(user.getEmail());
//        mailMessage.setFrom("useradmin@ksug.org");
//        mailMessage.setSubject("Upgrade 안내");
//        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다.");
//
//        mailSender.send(mailMessage);
        //3. DI 적용
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다.");

        this.mailSender.send(mailMessage);
    }

    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
