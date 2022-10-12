package springbook.user.service;

import springbook.user.dao.MockUserDao;
import springbook.user.domain.User;

public interface UserService {
    void add(User user);
    void upgradeLevels();
    void setUserDao(MockUserDao mockUserDao);
}
