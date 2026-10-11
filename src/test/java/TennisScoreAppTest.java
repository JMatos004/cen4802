import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TennisScoreAppTest {

    @Test
    void normalScoreIsAccepted() {
        assertEquals(4, TennisScoreApp.parseScore("4"));
    }

    @Test
    void negativeScoreBecomesZero() {
        assertEquals(0, TennisScoreApp.parseScore("-3"));
    }

    @Test
    void scoreAboveSixBecomesSix() {
        assertEquals(6, TennisScoreApp.parseScore("10"));
    }

    @Test
    void invalidScoreBecomesZero() {
        assertEquals(0, TennisScoreApp.parseScore("hello"));
    }
}
