package springbook.learningtest.junit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.either;
import static org.junit.matchers.JUnitMatchers.hasItem;

public class JUnitTest {
    @Autowired
    ApplicationContext context;

    static Set<JUnitTest> testObjects = new HashSet<>();
    static ApplicationContext contextObject = null;
    static JUnitTest testObject;

    @Test
    public void test1() {
//        assertThat(this, is(not(sameInstance(testObject))));
//        assertThat(testObjects, not(hasItem(this)));
        assertThat(contextObject == null || contextObject == this.context, is(true));
        testObject = this;
    }

    @Test
    public void test2() {
//        assertThat(this, is(not(sameInstance(testObject))));
//        assertThat(testObjects, not(hasItem(this)));
        assertTrue(contextObject == null || contextObject == this.context);
        testObject = this;
    }

    @Test
    public void test3() {
//        assertThat(this, is(not(sameInstance(testObject))));
//        assertThat(testObjects, not(hasItem(this)));
        assertThat(contextObject, either(is(nullValue())).or(is(this.context)));
        //either - 2개의 매처 조건을 or조건으로 비교
        //nullValue - 매처, 오브젝트가 null 인지 판단
        testObject = this;
    }
}
