package next.dto;

public class QuestionCreateDto {

    private String title;
    private String contents;

    private QuestionCreateDto() {
    }

    public QuestionCreateDto(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }
}