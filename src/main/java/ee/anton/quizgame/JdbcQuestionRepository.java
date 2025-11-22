package ee.anton.quizgame;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class JdbcQuestionRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcQuestionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Question> findAll() {
        String sql = "SELECT id, question_text, options, correct_option, question_type FROM questions";
        return jdbcTemplate.query(sql, new RowMapper<Question>() {
            @Override
            public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
                long id = rs.getLong("id");
                String text = rs.getString("question_text");

                Array arr = rs.getArray("options");
                String[] opts = new String[0];
                if (arr != null) {
                    Object raw = arr.getArray();
                    if (raw instanceof String[]) {
                        opts = (String[]) raw;
                    } else if (raw instanceof Object[]) {
                        Object[] o = (Object[]) raw;
                        opts = Arrays.copyOf(o, o.length, String[].class);
                    }
                }

                int correct = rs.getInt("correct_option");
                String type = rs.getString("question_type");

                return new Question(id, text, new ArrayList<>(Arrays.asList(opts)), correct, type);
            }
        });
    }
}
