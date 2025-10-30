import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ConsoleQuiz {
    private List<Question> questions;

    public ConsoleQuiz(List<Question> questions) {
        this.questions = new ArrayList<>(questions); // защитная копия

    }

    public Question askQuestion(List<Question> questions) {
        int idx = ThreadLocalRandom.current().nextInt(questions.size());
        return questions.get(idx);
    }

}
