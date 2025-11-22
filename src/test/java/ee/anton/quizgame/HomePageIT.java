package ee.anton.quizgame;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HomePageIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private JdbcQuestionRepository repository;

    @Test
    public void indexPageLoadsAndApiReturnsQuestion() {
        // Arrange: mock repository to return one question
        Question q = new Question(1L, "Тестовый вопрос?", List.of("Вариант A", "Вариант B"), 1, "single");
        when(repository.findAll()).thenReturn(List.of(q));

        // Act: request index page
        ResponseEntity<String> index = restTemplate.getForEntity("/", String.class);

        // Assert: index loads
        assertThat(index.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(index.getBody()).contains("QuizGame");

        // Act: request API
        ResponseEntity<String> api = restTemplate.getForEntity("        t        taskkill /PID 12332 /F", String.class);

        // Assert: API returns the mocked question text
        assertThat(api.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(api.getBody()).contains("Тестовый вопрос?");
    }
}
