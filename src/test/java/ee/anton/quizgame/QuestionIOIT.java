package ee.anton.quizgame;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionIOIT {

    private PrintStream originalOut;
    private InputStream originalIn;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        originalIn = System.in;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void tearDown() throws IOException {
        System.setOut(originalOut);
        System.setIn(originalIn);
        out.close();
    }

    @Test
    void userAnswersCorrectLetterB() {
        Question q = new Question(
                1L, "What is 2+2?", List.of("3", "4", "5"), 2, "single");
        System.setIn(new ByteArrayInputStream("b\n".getBytes()));

        q.displayQuestion();
        boolean ok = q.inputAnswerToQuestion();

        String printed = out.toString();
        assertTrue(printed.contains("What is 2+2?"));
        assertTrue(printed.contains("A) 3"));
        assertTrue(printed.contains("B) 4"));
        assertTrue(printed.contains("C) 5"));
        assertTrue(ok);
    }

    @Test
    void userAnswersNumberAlsoWorks() {
        Question q = new Question(
                2L, "Capital of Estonia?", List.of("Riga", "Tallinn", "Vilnius"), 2, "single");
        System.setIn(new ByteArrayInputStream("2\n".getBytes()));

        q.displayQuestion();
        boolean ok = q.inputAnswerToQuestion();

        assertTrue(ok);
    }
}
