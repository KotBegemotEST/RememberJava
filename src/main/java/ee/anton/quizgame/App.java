package ee.anton.quizgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
        public static void main(String[] args) throws Exception {
                DbQuestionRepository repo = new DbQuestionRepository();
                List<Question> dbQuestions = repo.findAll();
                ConsoleQuiz consoleQuiz = new ConsoleQuiz(dbQuestions);
                while (true) {

                        System.out.println(consoleQuiz);
                        Question question = consoleQuiz.askQuestion();
                        if (question == null) {
                                System.out.println("No questions available. Exiting.");
                                return;
                        }
                        question.displayQuestion();
                        boolean correct = question.inputAnswerToQuestion();
                        if (correct) {
                                System.out.println("Well done — your answer is correct.");
                        } else {
                                System.out.println("Better luck next time.");

                        }
                }
        }
}
