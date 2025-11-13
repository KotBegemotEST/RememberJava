package ee.anton.quizgame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class QuestionTest {

    @Test
    void inputAnswer_letterUppercase_correctWhen1Based() {
        Question q = new Question(1L, "Q?", List.of("Aopt", "Bopt", "Copt"), 1, "single");
        // correctOption == 1 -> treated as 1-based -> 'A' is correct
        assertTrue(q.inputAnswerToQuestion("A"));
        assertTrue(q.inputAnswerToQuestion("a"));
    }

    @Test
    void inputAnswer_numericAndLetter_correctWhen1Based() {
        Question q = new Question(2L, "Q?", List.of("Opt1", "Opt2", "Opt3"), 2, "single");
        // correctOption == 2 -> treated as 1-based -> 'B' and '2' should be correct
        assertTrue(q.inputAnswerToQuestion("B"));
        assertTrue(q.inputAnswerToQuestion("2"));
        assertFalse(q.inputAnswerToQuestion("1"));
    }

    @Test
    void inputAnswer_zeroBased_correctWhen0Based() {
        Question q = new Question(3L, "Q?", List.of("X", "Y"), 0, "single");
        // correctOption == 0 -> treated as 0-based -> 'A' should be correct
        assertTrue(q.inputAnswerToQuestion("A"));
        assertFalse(q.inputAnswerToQuestion("B"));
    }

    @Test
    void inputAnswer_invalidFormat_returnsFalse() {
        Question q = new Question(4L, "Q?", List.of("1", "2"), 1, "single");
        assertFalse(q.inputAnswerToQuestion("Z"));
        assertFalse(q.inputAnswerToQuestion(""));
        assertFalse(q.inputAnswerToQuestion(null));
    }
}
