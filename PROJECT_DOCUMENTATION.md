# QuizGame - Полная документация проекта

## 📋 Обзор проекта

**QuizGame** - это Java приложение для создания и проведения викторин с использованием PostgreSQL базы данных. Приложение работает в консоли, выбирает случайный вопрос из БД и выводит его с вариантами ответов.

**Текущее состояние:** РАБОЧЕЕ - приложение успешно запускается и получает вопросы из БД с правильной кодировкой UTF-8.

---

## 🏗️ Структура проекта

```
d:\QuizizGame\quzizGame\
│
├── pom.xml                          # Maven конфигурация проекта
├── README.md                        # Описание проекта
├── qodana.yaml                      # Конфиг анализа кода
├── quzizGame.iml                    # Конфиг IntelliJ IDEA
│
├── src/
│   └── main/java/ee/anton/quizgame/ # Исходный код Java приложения
│       ├── App.java                 # Главный класс - точка входа
│       ├── DbQuestionRepository.java # Работа с БД PostgreSQL
│       ├── ConsoleQuiz.java         # Логика викторины
│       └── Question.java            # Модель данных вопроса
│
├── target/                          # Скомпилированные файлы (создаются автоматически)
│   ├── classes/                     # .class файлы
│   ├── dependency/                  # Зависимости (JAR файлы)
│   └── quizgame-1.0-SNAPSHOT.jar   # Собранный JAR приложения
│
├── bin/                             # Скрипты и конфиги
│   ├── docker-compose.yml           # Docker Compose для БД
│   └── Dockerfile                   # Dockerfile (опционально)
│
├── lib/                             # Внешние библиотеки (если требуются)
│
├── run.bat                          # Windows скрипт для запуска приложения
├── insert_data.py                   # Python скрипт для загрузки тестовых данных
├── init.sql                         # SQL скрипт инициализации БД
│
└── .env                             # Переменные окружения для Docker

```

---

## 🔧 Конфигурация проекта

### pom.xml

**Главные параметры:**
- **GroupId:** `ee.anton` - уникальный идентификатор организации
- **ArtifactId:** `quizgame` - имя приложения
- **Version:** `1.0-SNAPSHOT` - версия (SNAPSHOT означает разработка)
- **Java version:** `17` - используется Java 17

**Зависимости:**
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.4</version>
</dependency>
```
PostgreSQL JDBC драйвер версии 42.7.4 для подключения к БД.

**Плагины:**
1. **maven-dependency-plugin** - копирует все зависимости в папку `target/dependency/`
2. **maven-jar-plugin** - создает JAR файл с указанием main класса

---

## 💻 Описание Java классов

### 1️⃣ App.java (главный класс)

**Назначение:** Точка входа в приложение, орхестрирует весь процесс.

```java
package ee.anton.quizgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        // Шаг 1: Создание репозитория для работы с БД
        DbQuestionRepository repo = new DbQuestionRepository();
        
        // Шаг 2: Получение всех вопросов из базы данных
        List<Question> dbQuestions = repo.findAll();
        
        // Шаг 3: Создание викторины
        ConsoleQuiz consoleQuiz = new ConsoleQuiz(dbQuestions);
        
        // Шаг 4: Выбор случайного вопроса
        Question question = consoleQuiz.askQuestion(dbQuestions);
        
        // Шаг 5: Вывод вопроса на экран
        question.displayQuestion(question);
    }
}
```

**Поток выполнения:**
1. Инициализирует DbQuestionRepository
2. Загружает все вопросы из PostgreSQL
3. Создает объект ConsoleQuiz
4. Выбирает случайный вопрос
5. Отображает его в консоль

---

### 2️⃣ DbQuestionRepository.java (работа с БД)

**Назначение:** Отвечает за всю работу с PostgreSQL - чтение и запись вопросов.

```java
package ee.anton.quizgame;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbQuestionRepository {
    // Параметры подключения из переменных окружения
    private static final String DB_HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String DB_PORT = System.getenv().getOrDefault("DB_PORT", "5432");
    private static final String DB_NAME = System.getenv().getOrDefault("POSTGRES_DB", "quizdb");
    
    // Строка подключения с параметрами кодировки UTF-8
    private static final String URL = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?characterEncoding=UTF-8&useUnicode=true";

    private static final String USER = System.getenv().getOrDefault("POSTGRES_USER", "quiz");
    private static final String PASSWORD = System.getenv().getOrDefault("POSTGRES_PASSWORD", "quiz");

    // Создание подключения к БД
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Получение всех вопросов из таблицы
    public List<Question> findAll() {
        String sql = "SELECT id, question_text, options, correct_option, question_type FROM questions";
        List<Question> list = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String text = rs.getString("question_text");

                // Получение массива вариантов ответа из PostgreSQL
                Array arr = rs.getArray("options");
                String[] opts = (String[]) arr.getArray();

                int correct = rs.getInt("correct_option");
                String type = rs.getString("question_type");

                // Создание объекта Question
                Question q = new Question(id, text, Arrays.asList(opts), correct, type);
                list.add(q);
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB error: " + e.getMessage(), e);
        }

        return list;
    }

    // Вставка нового вопроса в БД
    public void insert(Question q) {
        String sql = "INSERT INTO questions (question_text, options, correct_option, question_type) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, q.getQuestionText());
            // PostgreSQL требует явного создания массива
            ps.setArray(2, conn.createArrayOf("text", q.getOptions().toArray()));
            ps.setInt(3, q.getCorrectOption());
            ps.setString(4, q.getQuestionType());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB insert error: " + e.getMessage(), e);
        }
    }
}
```

**Ключевые моменты:**
- Использует переменные окружения для конфигурации (легко менять без переcompilирования)
- URL включает `?characterEncoding=UTF-8&useUnicode=true` для поддержки кириллицы
- `try-with-resources` автоматически закрывает соединение
- `PreparedStatement` предотвращает SQL-инъекции
- Массивы в PostgreSQL требуют специальной обработки через `conn.createArrayOf()`

**Структура таблицы БД (questions):**
```
id                  - BIGSERIAL PRIMARY KEY (уникальный ID)
question_text       - VARCHAR(255) (текст вопроса)
options             - text[] (массив вариантов ответа)
correct_option      - INT (индекс правильного ответа, 0-3)
question_type       - VARCHAR(50) (тип вопроса: "multiple_choice" и т.д.)
```

---

### 3️⃣ ConsoleQuiz.java (логика викторины)

**Назначение:** Отвечает за выбор вопросов и логику викторины.

```java
package ee.anton.quizgame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ConsoleQuiz {
    private List<Question> questions;

    // Конструктор создает защитную копию списка вопросов
    public ConsoleQuiz(List<Question> questions) {
        this.questions = new ArrayList<>(questions);
    }

    // Выбирает случайный вопрос из списка
    public Question askQuestion(List<Question> questions) {
        int idx = ThreadLocalRandom.current().nextInt(questions.size());
        return questions.get(idx);
    }
}
```

**Замечания:**
- `ThreadLocalRandom` используется вместо обычного `Random` для лучшей производительности
- Защитная копия в конструкторе предотвращает изменение исходного списка

---

### 4️⃣ Question.java (модель данных)

**Назначение:** Представляет структуру вопроса с его свойствами.

```java
package ee.anton.quizgame;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private Long id;
    private String questionText;
    private List<String> options;
    private int correctOption;
    private String questionType;

    // Конструктор инициализирует все поля
    public Question(Long id, String questionText, List<String> options,
            int correctOption, String questionType) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
        this.questionType = questionType;
    }

    // Выводит вопрос и варианты ответов в консоль
    public void displayQuestion(Question question) {
        List<String> choseVariants = new ArrayList<>(List.of("A","B","C","D","E","F"));
        System.out.println(question.getQuestionText());
        for (String option : question.getOptions()) {
            System.out.println(option);
        }
    }

    // Getter/Setter методы для всех полей
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public int getCorrectOption() { return correctOption; }
    public void setCorrectOption(int correctOption) { this.correctOption = correctOption; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
}
```

**Замечание:** Поле `choseVariants` в методе `displayQuestion()` сейчас не используется, но оно может быть полезно для отображения меток ответов (A, B, C, D).

---

## 🐳 Docker и база данных

### docker-compose.yml

Находится в папке `bin/`:

```yaml
version: "3.9"

services:
  postgres:
    image: postgres:16
    container_name: quiz-db
    restart: unless-stopped
    env_file:
      - .env
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  adminer:
    image: adminer
    restart: unless-stopped
    ports:
      - "8080:8080"

volumes:
  pgdata:
```

**Объяснение:**
- **postgres:16** - официальный образ PostgreSQL версии 16
- **container_name: quiz-db** - имя контейнера (используется в приложении)
- **env_file: .env** - загружает переменные окружения из .env файла
- **ports: 5432:5432** - пробрасывает порт PostgreSQL
- **adminer** - веб-интерфейс для управления БД (доступ через http://localhost:8080)
- **volumes** - сохраняет данные БД

### .env файл

```
POSTGRES_DB=quizdb
POSTGRES_USER=quiz
POSTGRES_PASSWORD=quiz
DB_HOST=localhost
DB_PORT=5432
```

**Значения:**
- `POSTGRES_DB` - имя базы данных
- `POSTGRES_USER` - пользователь БД
- `POSTGRES_PASSWORD` - пароль пользователя
- `DB_HOST` - хост (localhost для локальной машины)
- `DB_PORT` - порт PostgreSQL (по умолчанию 5432)

---

## 🚀 Как запустить приложение

### Шаг 1: Запуск базы данных

```powershell
cd d:\QuizizGame\quzizGame\src
docker-compose up -d
```

**Проверить статус:**
```powershell
docker ps
```

### Шаг 2: Загрузка тестовых данных

**Вариант 1 - Python:**
```powershell
python insert_data.py
```

**Вариант 2 - SQL файл:**
```powershell
cat init.sql | docker exec -i quiz-db psql -U quiz -d quizdb
```

### Шаг 3: Пересборка проекта (если были изменения в коде)

```powershell
cd d:\QuizizGame\quzizGame
mvn clean package -q
```

### Шаг 4: Запуск приложения

**Используя скрипт (рекомендуется):**
```powershell
cmd /c run.bat
```

**Или вручную (если нет .bat файла):**
```powershell
cd d:\QuizizGame\quzizGame
java -Dfile.encoding=UTF-8 -cp "target/quizgame-1.0-SNAPSHOT.jar;target/dependency/*" ee.anton.quizgame.App
```

---

## 📊 Поток данных в приложении

```
┌─────────────────────────────────────────────────┐
│ 1. Пользователь запускает run.bat               │
│    (устанавливает UTF-8 кодировку консоли)      │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│ 2. App.main() начинает выполнение                │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│ 3. DbQuestionRepository.findAll()                │
│    └─ Подключение к PostgreSQL                  │
│    └─ SQL запрос: SELECT * FROM questions       │
│    └─ Преобразование результатов в Question     │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│ 4. ConsoleQuiz.askQuestion()                     │
│    └─ Выбор случайного Question из списка       │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│ 5. Question.displayQuestion()                    │
│    └─ Вывод текста вопроса                      │
│    └─ Вывод всех вариантов ответов              │
└─────────────────────────────────────────────────┘
```

---

## 🔧 Команды для работы с проектом

### Maven команды

```bash
# Полная пересборка проекта
mvn clean package

# Быстрая пересборка (без очистки)
mvn package

# Только компиляция (без создания JAR)
mvn compile

# Очистка собранных файлов
mvn clean

# Вывод информации о проекте
mvn help:describe
```

### Docker команды

```bash
# Запуск контейнеров в фоновом режиме
docker-compose up -d

# Просмотр логов БД
docker logs quiz-db

# Остановка контейнеров
docker-compose down

# Перезапуск контейнеров
docker-compose restart

# Удаление всех данных (осторожно!)
docker-compose down -v
```

### Команды PostgreSQL (внутри контейнера)

```bash
# Подключение к БД
docker exec -it quiz-db psql -U quiz -d quizdb

# Просмотр всех вопросов
docker exec quiz-db psql -U quiz -d quizdb -c "SELECT * FROM questions;"

# Создание таблицы
docker exec quiz-db psql -U quiz -d quizdb -c "CREATE TABLE IF NOT EXISTS questions (...)"

# Удаление всех вопросов
docker exec quiz-db psql -U quiz -d quizdb -c "DELETE FROM questions;"
```

---

## 📝 Примеры данных в БД

**Текущие вопросы в таблице:**

| id | question_text | options | correct_option | question_type |
|----|---|---|---|---|
| 1 | Какой язык программирования мы изучаем? | {Java,Python,C++,JavaScript} | 0 | multiple_choice |
| 2 | Какой год создания Java? | {1991,1995,2000,2005} | 1 | multiple_choice |
| 3 | Кто создал Java? | {James Gosling,Guido van Rossum,Bjarne Stroustrup,Dennis Ritchie} | 0 | multiple_choice |

**Формат данных:**
- `correct_option: 0` = первый вариант (индексация с нуля)
- `correct_option: 1` = второй вариант
- И т.д.

---

## 🎯 Возможные улучшения для дальнейшей разработки

### 1. Получение ответа от пользователя
```java
Scanner scanner = new Scanner(System.in);
System.out.print("Ваш ответ (0-3): ");
int userAnswer = scanner.nextInt();
```

### 2. Проверка правильности ответа
```java
if (userAnswer == question.getCorrectOption()) {
    System.out.println("✓ Правильно!");
    score++;
} else {
    System.out.println("✗ Неправильно! Правильный ответ: " + 
                       question.getOptions().get(question.getCorrectOption()));
}
```

### 3. Цикл для нескольких вопросов
```java
for (int i = 0; i < 5; i++) {
    Question question = consoleQuiz.askQuestion(dbQuestions);
    question.displayQuestion(question);
    // получи и проверь ответ
}
```

### 4. Система очков
```java
public class Score {
    private int correct = 0;
    private int total = 0;
    
    public void addCorrect() { correct++; total++; }
    public void addWrong() { total++; }
    public double getPercentage() { return (correct * 100.0) / total; }
}
```

### 5. Фильтрация вопросов по типу
```java
public List<Question> findByType(String type) {
    // SELECT * FROM questions WHERE question_type = ?
}
```

---

## ⚠️ Важные замечания

1. **Кодировка UTF-8** - критична для русского текста. Всегда используйте:
   - `-Dfile.encoding=UTF-8` при запуске Java
   - `chcp 65001` в Windows консоли
   - Параметры `?characterEncoding=UTF-8&useUnicode=true` в URL БД

2. **PostgreSQL контейнер** - должен быть запущен перед стартом приложения

3. **Защита от SQL-инъекций** - используются `PreparedStatement`, никогда не конкатенируйте SQL строки!

4. **Переменные окружения** - легко менять конфиг без переcompilірования через `.env` файл

5. **Try-with-resources** - автоматически закрывает соединения и ресурсы

---

## 📞 Контактная информация проекта

- **Repository:** RememberJava
- **Owner:** KotBegemotEST
- **Branch:** main
- **Project:** QuizGame
- **Version:** 1.0-SNAPSHOT

---

**Документация актуальна на:** 11 ноября 2025 года

Для продолжения разработки - обновляйте эту документацию при добавлении новых функций!
