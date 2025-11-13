package ee.anton.quizgame;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbQuestionRepository {

    private static final String DB_HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String DB_PORT = System.getenv().getOrDefault("DB_PORT", "5432");
    private static final String DB_NAME = System.getenv().getOrDefault("POSTGRES_DB", "quizdb");
    private static final String URL = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?characterEncoding=UTF-8&useUnicode=true";

    private static final String USER = System.getenv().getOrDefault("POSTGRES_USER", "quiz");
    private static final String PASSWORD = System.getenv().getOrDefault("POSTGRES_PASSWORD", "quiz");

    // instance fields allow using alternative connection parameters (used by tests
    // or callers)
    private final String url;
    private final String user;
    private final String pass;

    public DbQuestionRepository() {
        this(URL, USER, PASSWORD);
    }

    public DbQuestionRepository(String url, String user, String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.url, this.user, this.pass);
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
