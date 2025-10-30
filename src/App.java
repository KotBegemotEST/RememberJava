import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
        public static void main(String[] args) throws Exception {
                DbQuestionRepository repo = new DbQuestionRepository();
                List<Question> dbQuestions = repo.findAll();

                ConsoleQuiz consoleQuiz = new ConsoleQuiz(dbQuestions);
                Question question = consoleQuiz.askQuestion(dbQuestions);
                question.displayQuestion(question);

        }
}
