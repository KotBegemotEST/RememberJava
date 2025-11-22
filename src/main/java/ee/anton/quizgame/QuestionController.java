package ee.anton.quizgame;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class QuestionController {

    private final JdbcQuestionRepository repository;

    public QuestionController(JdbcQuestionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/questions")
    public List<Question> getQuestions() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            // Return empty list if DB not available — safe fallback for quick startup
            return List.of();
        }
    }
}
