package springbook.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import springbook.user.domain.User;

import javax.sql.DataSource;

public class TestUserService extends UserService {
    private String id;

    public TestUserService(String id) {
        this.id = id;
    }

    protected void upgradeLevel(User user) {
        if(user.getId().equals(this.id)) throw new TestUserServiceException();
        super.upgradeLevel(user);
    }
}
