package next.dto;

public class QuestionUpdateDto {

    private long questionId;
    private String title;
    private String contents;

    private QuestionUpdateDto() {
    }

    public QuestionUpdateDto(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public void update(long questionId) {
        this.questionId = questionId;
    }
}