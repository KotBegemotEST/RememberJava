import psycopg2

conn = psycopg2.connect(
    host="localhost",
    port="5432",
    database="quizdb",
    user="quiz",
    password="quiz"
)

cursor = conn.cursor()

# Очистить таблицу
cursor.execute("DELETE FROM questions")

QUESTIONS = [
    {"questionText": "What does JVM stand for?",
     "options": ["Java Virtual Machine", "Java Variable Manager", "Java Version Module", "Joint Virtual Memory"],
     "correctOption": 0, "questionType": "MCQ"},

    {"questionText": "Which collection does not allow duplicate elements in Java?",
     "options": ["ArrayList", "LinkedList", "HashSet", "Vector"],
     "correctOption": 2, "questionType": "MCQ"},

    {"questionText": "Which SQL clause filters rows after aggregation?",
     "options": ["WHERE", "GROUP BY", "HAVING", "ORDER BY"],
     "correctOption": 2, "questionType": "MCQ"},

    {"questionText": "Which HTTP method is idempotent?",
     "options": ["POST", "PUT", "PATCH", "CONNECT"],
     "correctOption": 1, "questionType": "MCQ"},

    {"questionText": "Which Big-O describes binary search on a sorted array?",
     "options": ["O(n)", "O(log n)", "O(n log n)", "O(1)"],
     "correctOption": 1, "questionType": "MCQ"},

    {"questionText": "Which keyword prevents inheritance in Java?",
     "options": ["final", "static", "sealed", "transient"],
     "correctOption": 0, "questionType": "MCQ"},

    {"questionText": "Which SQL command creates a new table?",
     "options": ["ALTER", "CREATE", "INSERT", "MERGE"],
     "correctOption": 1, "questionType": "MCQ"},

    {"questionText": "Which IP address class is reserved for multicast?",
     "options": ["Class A", "Class B", "Class C", "Class D"],
     "correctOption": 3, "questionType": "MCQ"},

    {"questionText": "Which result type does SQL COUNT(*) return?",
     "options": ["INTEGER", "BOOLEAN", "TEXT", "DATE"],
     "correctOption": 0, "questionType": "MCQ"},

    {"questionText": "Which data structure is FIFO?",
     "options": ["Stack", "Queue", "Tree", "Graph"],
     "correctOption": 1, "questionType": "MCQ"},

    {"questionText": "Which keyword defines a constant in Java?",
     "options": ["const", "final", "let", "immutable"],
     "correctOption": 1, "questionType": "MCQ"},

    {"questionText": "What does ACID 'I' stand for in databases?",
     "options": ["Indexing", "Isolation", "Iteration", "Integrity"],
     "correctOption": 1, "questionType": "MCQ"},

    {"questionText": "Which HTTP status code means 'Created'?",
     "options": ["200", "201", "202", "204"],
     "correctOption": 1, "questionType": "MCQ"},

    {"questionText": "Which is a lossless compression format?",
     "options": ["JPEG", "MP3", "PNG", "MPEG-4"],
     "correctOption": 2, "questionType": "MCQ"},

    {"questionText": "Which Java type is NOT primitive?",
     "options": ["int", "double", "boolean", "String"],
     "correctOption": 3, "questionType": "MCQ"},

    {"questionText": "Which SQL constraint enforces uniqueness including NULLs in Postgres?",
     "options": ["PRIMARY KEY", "UNIQUE", "CHECK", "FOREIGN KEY"],
     "correctOption": 1, "questionType": "MCQ"},

    {"questionText": "What is the default TCP port for HTTPS?",
     "options": ["80", "21", "443", "25"],
     "correctOption": 2, "questionType": "MCQ"},

    {"questionText": "Which keyword starts a Java stream pipeline terminal operation?",
     "options": ["filter", "map", "collect", "peek"],
     "correctOption": 2, "questionType": "MCQ"},

    {"questionText": "Which normalization form removes partial dependencies?",
     "options": ["1NF", "2NF", "3NF", "BCNF"],
     "correctOption": 1, "questionType": "MCQ"},

    {"questionText": "Which tool manages Java project lifecycle and deps?",
     "options": ["npm", "pip", "maven", "cargo"],
     "correctOption": 2, "questionType": "MCQ"},
]


for question_text, options, correct_option, question_type in questions:
    cursor.execute(
        "INSERT INTO questions (question_text, options, correct_option, question_type) VALUES (%s, %s, %s, %s)",
        (question_text, options, correct_option, question_type)
    )

conn.commit()
cursor.close()
conn.close()

print("Вопросы добавлены успешно!")
