package next.dto;

import next.model.Question;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;
import java.util.StringJoiner;

public class QnaUpdatedDto {

    private long questionId;

    private String writer;

    private String title;

    private String contents;

    private Date createdDate;

    private int countOfComment;

    public Question toQuestion() {
        return new Question(this.questionId,
                this.writer,
                this.title,
                this.contents,
                this.createdDate,
                this.countOfComment);

    }

    public QnaUpdatedDto() {
    }

    public QnaUpdatedDto(String writer, String title, String contents) {
        this(0, writer, title, contents, new Date(), 0);
    }

    public QnaUpdatedDto(long questionId, String writer, String title, String contents, Date createdDate,
                         int countOfComment) {
        this.questionId = questionId;
        this.writer = writer;
        this.title = title;
        this.contents = contents;
        this.createdDate = createdDate;
        this.countOfComment = countOfComment;
    }

    public long getQuestionId() {
        return questionId;
    }

    public String getWriter() {
        return writer;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public int getCountOfComment() {
        return countOfComment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        QnaUpdatedDto that = (QnaUpdatedDto) o;

        return new EqualsBuilder()
                .append(writer, that.writer)
                .append(title, that.title)
                .append(contents, that.contents)
                .append(createdDate, that.createdDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(writer)
                .append(title)
                .append(contents)
                .append(createdDate)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", QnaUpdatedDto.class.getSimpleName() + "[", "]")
                .add("questionId='" + questionId + "'")
                .add("writer='" + writer + "'")
                .add("title='" + title + "'")
                .add("contents='" + contents + "'")
                .add("createdDate=" + createdDate)
                .add("countOfComment=" + countOfComment)
                .toString();
    }

}
