import java.util.ArrayList;
import java.util.List;

public class Question {

    private Long id;
    private String questionText;
    private List<String> options;
    private int correctOption;
    private String questionType;

    public Question(Long id, String questionText, List<String> options,
            int correctOption, String questionType) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
        this.questionType = questionType;
    }


    public void displayQuestion(Question question) {
        List<String> choseVariants =  new ArrayList<>(List.of("A","B","C","D","E","F"));
        System.out.println(question.getQuestionText());
        for (String option : question.getOptions()) {
            System.out.println(option);
        }
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
