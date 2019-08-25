package next.controller;

import next.dto.QuestionCreateDto;
import next.dto.QuestionUpdateDto;
import next.model.Question;
import next.model.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.test.NsWebTestClient;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiQnaControllerTest {

    private static final String QNA_API_DEFAULT_URL = "/api/qna";
    private static final Logger logger = LoggerFactory.getLogger(ApiQnaControllerTest.class);

    private NsWebTestClient client;

    @BeforeEach
    void setUp() {
        client = NsWebTestClient.of(8080);
    }

    @DisplayName("Qna 리스트 보기")
    @Test
    void list() {
        List<Question> lists = client.getResources(QNA_API_DEFAULT_URL + "/list", Question.class);
        logger.debug("data: {}", lists);
        assertThat(lists).isNotNull();
    }

    @DisplayName("Qna의 답변 삭제 성공")
    @Test
    void deleteAnswer() {
        String parameter = "answerId=6";
        Result result = client.deleteResource(QNA_API_DEFAULT_URL + "/deleteAnswer?" + parameter);
        logger.debug("result : {}", result);
        assertThat(result.isStatus()).isTrue();
    }

    @Test
    @DisplayName("Qna 글쓰기/수정/조회")
    void crud() {
        // 글쓰기
        QuestionCreateDto expected = new QuestionCreateDto("wirter", "title", "contents");
        URI location = client.createResource(QNA_API_DEFAULT_URL, expected, QuestionCreateDto.class);
        logger.debug("location : {}", location);

        // 조회
        Question actual = client.getResource(location, Question.class);
        assertThat(actual.getWriter()).isEqualTo(expected.getWriter());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getContents()).isEqualTo(expected.getContents());
        logger.debug("Create data confirm : {}", actual);

        // 수정
        QuestionUpdateDto updateQuestion = new QuestionUpdateDto("title2", "contents2");
        client.updateResource(location, updateQuestion, QuestionUpdateDto.class);

        actual = client.getResource(location, Question.class);
        assertThat(actual.getWriter()).isEqualTo(expected.getWriter());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getContents()).isEqualTo(expected.getContents());
    }
}