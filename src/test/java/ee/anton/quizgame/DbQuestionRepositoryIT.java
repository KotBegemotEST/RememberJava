package ee.anton.quizgame;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ee.anton.quizgame.Question;

public class DbQuestionRepositoryIT {

    // Значения по умолчанию (как у тебя сейчас — из env)
    private static final String DEF_DB_HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String DEF_DB_PORT = System.getenv().getOrDefault("DB_PORT", "5432");
    private static final String DEF_DB_NAME = System.getenv().getOrDefault("POSTGRES_DB", "quizdb");
    private static final String DEF_URL = "jdbc:postgresql://" + DEF_DB_HOST + ":" + DEF_DB_PORT + "/" + DEF_DB_NAME
            + "?characterEncoding=UTF-8&useUnicode=true";
    private static final String DEF_USER = System.getenv().getOrDefault("POSTGRES_USER", "quiz");
    private static final String DEF_PASSWORD = System.getenv().getOrDefault("POSTGRES_PASSWORD", "quiz");

    // Экземплярные поля, которые реально использует getConnection()
    private final String url;
    private final String user;
    private final String password;

    /** Прод-режим: читаем из env (как было у тебя). */
    public DbQuestionRepositoryIT() {
        this.url = DEF_URL;
        this.user = DEF_USER;
        this.password = DEF_PASSWORD;
    }

    /** Тест-режим: явно передаём JDBC-URL/логин/пароль (от Testcontainers). */
    public DbQuestionRepositoryIT(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
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

                Question q = new Question(id, text, Arrays.asList(opts), correct, type);
                list.add(q);
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB error: " + e.getMessage(), e);
        }
        return list;
    }

    public void insert(Question q) {
        String sql = "INSERT INTO questions (question_text, options, correct_option, question_type) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, q.getQuestionText());
            ps.setArray(2, conn.createArrayOf("text", q.getOptions().toArray()));
            ps.setInt(3, q.getCorrectOption());
            ps.setString(4, q.getQuestionType());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("DB insert error: " + e.getMessage(), e);
        }
    }
}
