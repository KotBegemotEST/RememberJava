package ee.anton.quizgame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ConsoleQuiz {
    private List<Question> questions;

    public ConsoleQuiz(List<Question> questions) {
        this.questions = new ArrayList<>(questions); // защитная копия

    }

    /**
     * Return a random question from the repository copy, or null if none available.
     */
    public Question askQuestion() {
        if (this.questions == null || this.questions.isEmpty()) {
            return null;
        }
        int idx = ThreadLocalRandom.current().nextInt(this.questions.size());
        return this.questions.get(idx);
    }

}
