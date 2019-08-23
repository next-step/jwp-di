package next.controller;

import next.dto.QnaCreatedDto;
import next.dto.QnaUpdatedDto;
import next.dto.UserCreatedDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.test.NsWebTestClient;

import java.net.URI;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class QnaAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(QnaAcceptanceTest.class);

    private NsWebTestClient client;

    @BeforeEach
    void setUp() {
        client = NsWebTestClient.of(8080);
    }

    @Test
    @DisplayName("QNA 추가/수정/삭제")
    void crud() {
        // 사용자 추가
        UserCreatedDto user =
                new UserCreatedDto("jun", "password", "현준", "jun@nextstep.camp");
        client.createResource("/api/users", user, UserCreatedDto.class);

        // QNA 추가
        QnaCreatedDto expectedQuestion = new QnaCreatedDto("jun", "hello", "welcome jun");
        final URI location = client.basicAuth(user.getUserId(), user.getPassword())
                .createResource("/api/qna", expectedQuestion, QnaCreatedDto.class);

        QnaCreatedDto question = client.getResource(location, QnaCreatedDto.class);
        qnaEquals(question, expectedQuestion);

        // QNA 수정
        QnaUpdatedDto updatedDto = new QnaUpdatedDto(question.getQuestionId(), "jun", "changed", "change jun", new Date(), 0);
        client.updateResource(location, updatedDto, QnaUpdatedDto.class);
        QnaUpdatedDto expectedDto = client.getResource(location, QnaUpdatedDto.class);
        qnaEquals(expectedDto, updatedDto);

    }

    void qnaEquals(QnaCreatedDto actual, QnaCreatedDto expected) {
        assertThat(actual.getWriter()).isEqualTo(expected.getWriter());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getContents()).isEqualTo(expected.getContents());
    }

    void qnaEquals(QnaUpdatedDto actual, QnaUpdatedDto expected) {
        assertThat(actual.getWriter()).isEqualTo(expected.getWriter());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getContents()).isEqualTo(expected.getContents());
    }


}
