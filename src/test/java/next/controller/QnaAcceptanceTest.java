package next.controller;

import next.dto.QuestionDto;
import next.dto.UserCreatedDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.test.NsWebTestClient;

import java.net.URI;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class QnaAcceptanceTest {

    private NsWebTestClient client;

    @BeforeEach
    void setUp() {
        client = NsWebTestClient.of(8080);
    }

    @DisplayName("QNA 추가/수정/삭제")
    @Test
    void crud() {
        // 사용자 추가
        UserCreatedDto user =
                new UserCreatedDto("jun", "password", "현준", "jun@nextstep.camp");
        client.createResource("/api/users", user, UserCreatedDto.class);

        // QNA 추가
        QuestionDto expectedQuestion = new QuestionDto("jun", "hello", "welcome jun");
        final URI location = client.basicAuth(user.getUserId(), user.getPassword())
                .createResource("/api/qna", expectedQuestion, QuestionDto.class);

        QuestionDto question = client.getResource(location, QuestionDto.class);
        qnaEquals(question, expectedQuestion);

        // QNA 수정
        QuestionDto updatedDto = new QuestionDto(question.getQuestionId(), "jun", "changed", "change jun", new Date(), 0);
        client.updateResource(location, updatedDto, QuestionDto.class);
        QuestionDto expectedDto = client.getResource(location, QuestionDto.class);
        qnaEquals(expectedDto, updatedDto);

    }

    void qnaEquals(QuestionDto actual, QuestionDto expected) {
        assertThat(actual.getWriter()).isEqualTo(expected.getWriter());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getContents()).isEqualTo(expected.getContents());
    }


}
