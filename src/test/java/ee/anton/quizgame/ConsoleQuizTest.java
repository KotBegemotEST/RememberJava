package ee.anton.quizgame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ConsoleQuizTest {

    @Test
    void askQuestion_returnsNullWhenEmpty() {
        ConsoleQuiz quiz = new ConsoleQuiz(List.of());
        assertNull(quiz.askQuestion());
    }

    @Test
    void askQuestion_returnsElementWhenNotEmpty() {
        Question q1 = new Question(1L, "Q1", List.of("a", "b"), 1, "single");
        Question q2 = new Question(2L, "Q2", List.of("x", "y"), 1, "single");
        ConsoleQuiz quiz = new ConsoleQuiz(List.of(q1, q2));
        Question picked = quiz.askQuestion();
        assertNotNull(picked);
        assertTrue(picked == q1 || picked == q2);
    }
}
