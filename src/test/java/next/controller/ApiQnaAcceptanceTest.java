package next.controller;

import next.model.Question;
import next.model.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.test.NsWebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiQnaAcceptanceTest {

    private static final String QNA_API_DEFAULT_URL = "/api/qna";
    private static final Logger logger = LoggerFactory.getLogger(ApiQnaAcceptanceTest.class);

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
}