package springbook.user.service;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    UserDao userDao;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
//        //1차 구현
//        List<User> users = userDao.getAll();
//        for(User user : users) {
//            Boolean changed = null;
//            if(user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
//                user.setLevel(Level.SILVER);
//                changed = true;
//            }
//            else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
//                user.setLevel(Level.GOLD);
//                changed = true;
//            }
//            else if(user.getLevel() == Level.GOLD) { changed = false; }
//            else { changed = false; }
//
//            if(changed) userDao.update(user);
//        }
//        2차 리팩토링
        List<User> users = userDao.getAll();
        for(User user : users)
            if(canUpgradeLevel(user))
                upgradeLevel(user);
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
    }

    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
