package health.tracker;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class AppTest {

    @Test
    void appHasAGreeting() {
        var classUnderTest = new App();
        assertThat(classUnderTest.getGreeting(), is(not(nullValue())));
    }
}