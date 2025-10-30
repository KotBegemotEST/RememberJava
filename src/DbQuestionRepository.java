import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbQuestionRepository {

    private static final String URL = "jdbc:postgresql://localhost:5432/quizdb";
    private static final String USER = "quiz";
    private static final String PASSWORD = "quiz";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public List<Question> findAll() {
        String sql = "SELECT id, question_text, options, correct_option, question_type FROM questions";
        List<Question> list = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String text = rs.getString("question_text");

                Array arr = rs.getArray("options");
                String[] opts = (String[]) arr.getArray();

                int correct = rs.getInt("correct_option");
                String type = rs.getString("question_type");

                Question q = new Question(
                        id,
                        text,
                        Arrays.asList(opts),
                        correct,
                        type);
                list.add(q);
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB error: " + e.getMessage(), e);
        }

        return list;
    }

    public void insert(Question q) {
        String sql = "INSERT INTO questions (question_text, options, correct_option, question_type) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, q.getQuestionText());
            // в PG надо явно сделать массив
            ps.setArray(2, conn.createArrayOf("text", q.getOptions().toArray()));
            ps.setInt(3, q.getCorrectOption());
            ps.setString(4, q.getQuestionType());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB insert error: " + e.getMessage(), e);
        }
    }
}
