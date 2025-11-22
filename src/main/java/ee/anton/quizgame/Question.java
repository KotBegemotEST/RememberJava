package ee.anton.quizgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Question {

    private Long id;
    private String questionText;
    private List<String> options;
    private int correctOption;
    private String questionType;
    // Reuse a single Scanner for System.in to avoid resource leak and closing
    // System.in
    private static final Scanner SCANNER = new Scanner(System.in);

    public Question(Long id, String questionText, List<String> options,
            int correctOption, String questionType) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
        this.questionType = questionType;
    }

    /**
     * Display this question and its options. Options are labeled A, B, C, ...
     */
    public void displayQuestion() {
        List<String> choseVariants = new ArrayList<>(List.of("A", "B", "C", "D", "E", "F"));
        System.out.println(this.getQuestionText());
        int i = 0;
        for (String option : this.getOptions()) {
            i++;
            // Используем форматированную строку: буква варианта и сам текст опции
            System.out.printf("%s) %s%n", choseVariants.get(i - 1), option);
        }
    }

    /**
     * Read a single-line answer from the user (letter A..F) and return true if it
     * matches the correct option.
     * The method tolerates both 1-based and 0-based stored correctOption values: if
     * correctOption is in 1..n,
     * it is treated as 1-based; if it's in 0..n-1 it is treated as 0-based.
     */
    public boolean inputAnswerToQuestion() {
        System.out.println("Please enter your answer: ");
        String userAnswer;
        try {
            userAnswer = SCANNER.nextLine();
        } catch (java.util.NoSuchElementException ex) {
            System.out.println("No input available. Exiting.");
            System.exit(0);
            return false;
        }
        System.out.println("You entered: " + userAnswer);
        return inputAnswerToQuestion(userAnswer);
    }

    /**
     * Test-friendly variant: check the given userAnswer string and return whether
     * it's correct.
     */
    public boolean inputAnswerToQuestion(String userAnswer) {
        if (userAnswer == null || userAnswer.isBlank()) {
            return false;
        }

        String normalized = userAnswer.trim().toUpperCase();
        char ch = normalized.charAt(0);
        int selectedIndex = -1; // 0-based
        if (ch >= 'A' && ch <= 'Z') {
            selectedIndex = ch - 'A';
        } else {
            // try to parse numeric answers (1-based)
            try {
                int num = Integer.parseInt(normalized);
                selectedIndex = num - 1;
            } catch (NumberFormatException ex) {
                return false;
            }
        }

        int correctIdx;
        if (this.correctOption >= 1 && this.correctOption <= this.options.size()) {
            correctIdx = this.correctOption - 1; // stored as 1-based
        } else if (this.correctOption >= 0 && this.correctOption < this.options.size()) {
            correctIdx = this.correctOption; // stored as 0-based
        } else {
            // unknown format — fail safe
            return false;
        }

        return selectedIndex == correctIdx;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(int correctOption) {
        this.correctOption = correctOption;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
