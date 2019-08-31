package next.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import next.dto.AnswerCreatedDto;
import next.model.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.test.NsWebTestClient;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class QnaAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(QnaAcceptanceTest.class);

    private NsWebTestClient client;

    @BeforeEach
    void setUp() {
        client = NsWebTestClient.of(8080);
    }

    @DisplayName("질문 조회")
    @Test
    void crud() {
        List<Question> questions = client.getResources("/api/qna/list", Question.class);
        assertThat(questions.size()).isEqualTo(8);
    }
}
