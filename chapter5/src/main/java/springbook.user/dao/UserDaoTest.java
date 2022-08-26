package springbook.user.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "/applicationContext.xml")
//@DirtiesContext
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserDaoTest {
//    @Autowired
//    private ApplicationContext context;

    UserDaoJdbc dao;
    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp() {
        dao = new UserDaoJdbc();

        DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost/testdb", "spring", "book", true);
        dao.setDataSource(dataSource);

//        this.user1 = new User("gyumee", "박성철", "springno1");
//        this.user2 = new User("leegw700", "이길원", "springno2");
//        this.user3 = new User("bumjin", "박범진", "springno3");
        this.user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0);
        this.user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 10);
        this.user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40);
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        JUnitCore.main("springbook.user.dao.UserDaoTest");
    }

    @Test
    public void addAndGet() throws ClassNotFoundException, SQLException {
//        user.setId("gyumee");
//        user.setName("박성철");
//        user.setPassword("springno1");

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        User userget1 = dao.get(user1.getId());
//        assertThat(userget1.getName(), is(user1.getName()));
//        assertThat(userget1.getPassword(), is(user1.getPassword()));
        checkSameUser(userget1, user1);

        User userget2 = dao.get(user2.getId());
//        assertThat(userget2.getName(), is(user2.getName()));
//        assertThat(userget2.getPassword(), is(user2.getPassword()));
        checkSameUser(userget2, user2);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() throws ClassNotFoundException, SQLException {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        UserDao dao = context.getBean("userDao", UserDao.class);
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.get("unknown_id");
    }

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));
        assertThat(user1.getLevel(), is(user2.getLevel()));
        assertThat(user1.getLogin(), is(user2.getLogin()));
        assertThat(user1.getRecommend(), is(user2.getRecommend()));
    }

    @Test
    public void update() throws SQLException, ClassNotFoundException {
        dao.deleteAll();

        dao.add(user1);
        dao.add(user2);

        user1.setName("오민규");
        user1.setPassword("springno6");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        dao.update(user1);

        User user1update = dao.get(user1.getId());
        checkSameUser(user1, user1update);

        User user2same = dao.get(user2.getId());
        checkSameUser(user2, user2same);
    }
}
