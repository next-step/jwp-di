package next.dto;

public class AnswerCreatedDto {
    private String writer;
    private String contents;
    private long questionId;

    private AnswerCreatedDto() {
    }

    public AnswerCreatedDto(String writer, String contents, long questionId) {
        this.writer = writer;
        this.contents = contents;
        this.questionId = questionId;
    }

    public String getWriter() {
        return writer;
    }

    public String getContents() {
        return contents;
    }

    public long getQuestionId() {
        return questionId;
    }
}
