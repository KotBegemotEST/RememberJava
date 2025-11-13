DELETE FROM questions;
INSERT INTO questions (question_text, options, correct_option, question_type) 
VALUES ('Какой язык программирования мы изучаем?', 
        '{Java,Python,C++,JavaScript}', 0, 'multiple_choice');
