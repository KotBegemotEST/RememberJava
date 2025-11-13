# Примеры кода для расширения функциональности QuizGame

## 1️⃣ Добавляем получение ответа пользователя

### Текущий код (App.java):
```java
public static void main(String[] args) throws Exception {
    DbQuestionRepository repo = new DbQuestionRepository();
    List<Question> dbQuestions = repo.findAll();

    ConsoleQuiz consoleQuiz = new ConsoleQuiz(dbQuestions);
    Question question = consoleQuiz.askQuestion(dbQuestions);
    question.displayQuestion(question);
}
```

### Улучшенный код с получением ответа:
```java
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        DbQuestionRepository repo = new DbQuestionRepository();
        List<Question> dbQuestions = repo.findAll();

        ConsoleQuiz consoleQuiz = new ConsoleQuiz(dbQuestions);
        Question question = consoleQuiz.askQuestion(dbQuestions);
        question.displayQuestion(question);

        // 🆕 Получение ответа пользователя
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nВаш ответ (0-3): ");
        
        try {
            int userAnswer = scanner.nextInt();
            
            // Проверка правильности
            if (userAnswer == question.getCorrectOption()) {
                System.out.println("✅ Правильно!");
            } else {
                System.out.println("❌ Неправильно!");
                System.out.println("Правильный ответ: " + 
                    question.getOptions().get(question.getCorrectOption()));
            }
        } catch (Exception e) {
            System.out.println("❌ Ошибка ввода!");
        } finally {
            scanner.close();
        }
    }
}
```

---

## 2️⃣ Система очков и статистика

### Новый класс Score.java:
```java
package ee.anton.quizgame;

public class Score {
    private int correct = 0;
    private int total = 0;
    private int streak = 0;          // Текущая полоса побед
    private int maxStreak = 0;       // Максимальная полоса
    
    public void addCorrect() {
        correct++;
        total++;
        streak++;
        if (streak > maxStreak) {
            maxStreak = streak;
        }
    }
    
    public void addWrong() {
        total++;
        streak = 0;
    }
    
    public double getPercentage() {
        if (total == 0) return 0;
        return (correct * 100.0) / total;
    }
    
    public void printStatistics() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║         СТАТИСТИКА ВИКТОРИНЫ        ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ Всего вопросов:     " + 
            String.format("%15d", total) + " ║");
        System.out.println("║ Правильных ответов: " + 
            String.format("%15d", correct) + " ║");
        System.out.println("║ Процент:            " + 
            String.format("%14.1f%%", getPercentage()) + " ║");
        System.out.println("║ Текущая полоса:     " + 
            String.format("%15d", streak) + " ║");
        System.out.println("║ Максимальная полоса:" + 
            String.format("%15d", maxStreak) + " ║");
        System.out.println("╚════════════════════════════════════╝\n");
    }
    
    public int getCorrect() { return correct; }
    public int getTotal() { return total; }
    public int getStreak() { return streak; }
    public int getMaxStreak() { return maxStreak; }
}
```

### Использование в App.java:
```java
public static void main(String[] args) throws Exception {
    DbQuestionRepository repo = new DbQuestionRepository();
    List<Question> dbQuestions = repo.findAll();
    
    Score score = new Score();  // 🆕
    Scanner scanner = new Scanner(System.in);
    
    // Цикл для нескольких вопросов
    for (int i = 0; i < 5 && i < dbQuestions.size(); i++) {
        ConsoleQuiz consoleQuiz = new ConsoleQuiz(dbQuestions);
        Question question = consoleQuiz.askQuestion(dbQuestions);
        
        System.out.println("\n❓ Вопрос " + (i + 1) + " из 5");
        question.displayQuestion(question);
        
        System.out.print("Ваш ответ (0-" + (question.getOptions().size()-1) + "): ");
        
        try {
            int userAnswer = scanner.nextInt();
            
            if (userAnswer >= 0 && userAnswer < question.getOptions().size()) {
                if (userAnswer == question.getCorrectOption()) {
                    System.out.println("✅ Правильно!");
                    score.addCorrect();  // 🆕
                } else {
                    System.out.println("❌ Неправильно!");
                    System.out.println("Правильный ответ: " + 
                        question.getOptions().get(question.getCorrectOption()));
                    score.addWrong();  // 🆕
                }
            } else {
                System.out.println("❌ Неверный номер!");
                score.addWrong();  // 🆕
            }
        } catch (Exception e) {
            System.out.println("❌ Ошибка ввода!");
            scanner.nextLine();  // Очистить буфер
            score.addWrong();  // 🆕
        }
    }
    
    score.printStatistics();  // 🆕 Показать результаты
    scanner.close();
}
```

---

## 3️⃣ Фильтрация вопросов по типу

### Добавить метод в DbQuestionRepository.java:
```java
public List<Question> findByType(String type) {
    String sql = "SELECT id, question_text, options, correct_option, question_type " +
                 "FROM questions WHERE question_type = ?";
    List<Question> list = new ArrayList<>();

    try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, type);  // 🆕 Установка параметра
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            long id = rs.getLong("id");
            String text = rs.getString("question_text");
            Array arr = rs.getArray("options");
            String[] opts = (String[]) arr.getArray();
            int correct = rs.getInt("correct_option");
            String qType = rs.getString("question_type");

            Question q = new Question(id, text, Arrays.asList(opts), correct, qType);
            list.add(q);
        }

    } catch (SQLException e) {
        throw new RuntimeException("DB error: " + e.getMessage(), e);
    }

    return list;
}
```

### Использование:
```java
// Получить только вопросы типа "multiple_choice"
List<Question> mcQuestions = repo.findByType("multiple_choice");
```

---

## 4️⃣ Красивый вывод с буквами (A, B, C, D)

### Обновить Question.java:
```java
public void displayQuestion(Question question) {
    List<String> letters = List.of("A", "B", "C", "D", "E", "F");
    
    System.out.println("\n═══════════════════════════════════════════════════");
    System.out.println("❓ " + question.getQuestionText());
    System.out.println("───────────────────────────────────────────────────");
    
    for (int i = 0; i < question.getOptions().size(); i++) {
        String letter = i < letters.size() ? letters.get(i) : String.valueOf(i);
        System.out.println(letter + ") " + question.getOptions().get(i));
    }
    
    System.out.println("═══════════════════════════════════════════════════");
}
```

### Результат:
```
═══════════════════════════════════════════════════
❓ Какой язык программирования мы изучаем?
───────────────────────────────────────────────────
A) Java
B) Python
C) C++
D) JavaScript
═══════════════════════════════════════════════════
```

---

## 5️⃣ Система уровней сложности

### Добавить в Question.java:
```java
public enum Difficulty {
    EASY(1),
    MEDIUM(2),
    HARD(3);
    
    private int level;
    
    Difficulty(int level) {
        this.level = level;
    }
    
    public int getLevel() {
        return level;
    }
}
```

### Обновить таблицу в БД:
```sql
ALTER TABLE questions ADD COLUMN difficulty VARCHAR(20) DEFAULT 'MEDIUM';
```

### Добавить метод в DbQuestionRepository:
```java
public List<Question> findByDifficulty(String difficulty) {
    String sql = "SELECT * FROM questions WHERE difficulty = ? ORDER BY id";
    List<Question> list = new ArrayList<>();

    try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, difficulty);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            // ... преобразование как обычно
        }

    } catch (SQLException e) {
        throw new RuntimeException("DB error: " + e.getMessage(), e);
    }

    return list;
}
```

---

## 6️⃣ Сохранение результатов в БД

### Создать таблицу results:
```sql
CREATE TABLE results (
    id BIGSERIAL PRIMARY KEY,
    player_name VARCHAR(100),
    correct_answers INT,
    total_questions INT,
    percentage FLOAT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Новый класс ResultRepository.java:
```java
package ee.anton.quizgame;

import java.sql.*;

public class ResultRepository {
    
    private static final String DB_HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String DB_PORT = System.getenv().getOrDefault("DB_PORT", "5432");
    private static final String DB_NAME = System.getenv().getOrDefault("POSTGRES_DB", "quizdb");
    private static final String URL = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?characterEncoding=UTF-8&useUnicode=true";

    private static final String USER = System.getenv().getOrDefault("POSTGRES_USER", "quiz");
    private static final String PASSWORD = System.getenv().getOrDefault("POSTGRES_PASSWORD", "quiz");

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void saveResult(String playerName, int correct, int total) {
        String sql = "INSERT INTO results (player_name, correct_answers, total_questions, percentage) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, playerName);
            ps.setInt(2, correct);
            ps.setInt(3, total);
            ps.setDouble(4, (correct * 100.0) / total);

            ps.executeUpdate();
            System.out.println("✅ Результат сохранен!");

        } catch (SQLException e) {
            throw new RuntimeException("DB error: " + e.getMessage(), e);
        }
    }
}
```

### Использование в App.java:
```java
ResultRepository resultRepo = new ResultRepository();
resultRepo.saveResult("Иван", score.getCorrect(), score.getTotal());
```

---

## 7️⃣ Меню выбора сложности

### Обновить App.java:
```java
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║      ДОБРО ПОЖАЛОВАТЬ В КВИЗ       ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println("\nВыберите уровень сложности:");
        System.out.println("1) Легкий");
        System.out.println("2) Средний");
        System.out.println("3) Сложный");
        System.out.print("\nВаш выбор: ");
        
        int choice = scanner.nextInt();
        
        String difficulty = switch(choice) {
            case 1 -> "EASY";
            case 2 -> "MEDIUM";
            case 3 -> "HARD";
            default -> "MEDIUM";
        };
        
        DbQuestionRepository repo = new DbQuestionRepository();
        List<Question> dbQuestions = repo.findByDifficulty(difficulty);
        
        if (dbQuestions.isEmpty()) {
            System.out.println("❌ Вопросы не найдены!");
            scanner.close();
            return;
        }
        
        // ... остальной код викторины
        scanner.close();
    }
}
```

---

## 8️⃣ Подсказки (50/50)

### Добавить в ConsoleQuiz.java:
```java
public List<String> getHint5050(Question question) {
    List<String> options = new ArrayList<>(question.getOptions());
    int correctIdx = question.getCorrectOption();
    
    // Оставляем правильный ответ и один случайный неправильный
    List<String> hint = new ArrayList<>();
    hint.add(options.get(correctIdx));
    
    for (int i = 0; i < options.size(); i++) {
        if (i != correctIdx) {
            hint.add(options.get(i));
            break;
        }
    }
    
    return hint;
}
```

### Использование:
```java
if (userAnswer == -1) {  // Пользователь просит подсказку
    List<String> hint = consoleQuiz.getHint5050(question);
    System.out.println("💡 Подсказка: осталось 2 варианта!");
    for (String option : hint) {
        System.out.println("  - " + option);
    }
}
```

---

## 9️⃣ Проверка ввода данных

### Utility класс для валидации:
```java
package ee.anton.quizgame;

public class InputValidator {
    
    public static int getValidIntInput(Scanner scanner, int min, int max, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int input = scanner.nextInt();
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.println("❌ Пожалуйста введите число от " + min + " до " + max);
                }
            } catch (Exception e) {
                System.out.println("❌ Некорректный ввод!");
                scanner.nextLine();  // Очистить буфер
            }
        }
    }
    
    public static String getValidStringInput(Scanner scanner, int minLength, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.length() >= minLength) {
                return input;
            } else {
                System.out.println("❌ Минимальная длина: " + minLength + " символов");
            }
        }
    }
}
```

### Использование:
```java
String playerName = InputValidator.getValidStringInput(scanner, 1, "Введите ваше имя: ");
int answer = InputValidator.getValidIntInput(scanner, 0, 3, "Ваш ответ: ");
```

---

## 🔟 Логирование результатов

### Добавить логирование в DbQuestionRepository:
```java
import java.io.FileWriter;
import java.time.LocalDateTime;

public class Logger {
    
    private static final String LOG_FILE = "quiz_log.txt";
    
    public static void log(String message) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            String timestamp = LocalDateTime.now().toString();
            fw.write("[" + timestamp + "] " + message + "\n");
        } catch (Exception e) {
            System.err.println("❌ Ошибка логирования: " + e.getMessage());
        }
    }
}
```

### Использование:
```java
Logger.log("Пользователь: Иван, Ответ: 0, Правильно: true");
Logger.log("Загружено " + dbQuestions.size() + " вопросов из БД");
```

---

## 📝 Итого: Что можно добавить

| Функция | Сложность | Время | Приоритет |
|---------|-----------|-------|-----------|
| Получение ответа пользователя | Легко | 15 мин | 🔴 Критично |
| Система очков | Средне | 30 мин | 🔴 Высоко |
| Красивый вывод | Легко | 10 мин | 🟡 Среднее |
| Цикл вопросов | Легко | 10 мин | 🔴 Высоко |
| Фильтрация по типу | Средне | 20 мин | 🟡 Среднее |
| Сохранение результатов | Средне | 25 мин | 🟡 Среднее |
| Меню выбора | Легко | 15 мин | 🟢 Низко |
| Подсказки | Средне | 20 мин | 🟢 Низко |
| Валидация ввода | Легко | 20 мин | 🟡 Среднее |
| Логирование | Легко | 15 мин | 🟢 Низко |

---

**Примеры кода:** 11 ноября 2025

Используй эти примеры для расширения своего приложения!
