package next.service;

import next.dao.AnswerDao;
import next.model.Answer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class AnswerServiceTest {

    private AnswerService answerService;

    @BeforeEach
    void setUp() {
        final AnswerDao answerDao = new AnswerDao();

        answerService = new AnswerService(answerDao);
    }

    @DisplayName("answer를 추가한다.")
    @Test
    void save() {
        // given
        final Answer answer = new Answer("jaeyeonling", "Hello Answer", 1);

        // when
        final Answer savedAnswer = answerService.save(answer);

        // then
        assertThat(savedAnswer.getWriter()).isEqualTo(answer.getWriter());
        assertThat(savedAnswer.getContents()).isEqualTo(answer.getContents());
        assertThat(savedAnswer.getQuestionId()).isEqualTo(answer.getQuestionId());
    }


    @DisplayName("추가한 answer를 조회한다.")
    @Test
    void findById() {
        // given
        final Answer answer = new Answer("jaeyeonling", "Hello Answer", 1);
        final Answer savedAnswer = answerService.save(answer);

        // when
        final Answer foundAnswer = answerService.findById(savedAnswer.getAnswerId()).get();

        // then
        assertThat(foundAnswer.getWriter()).isEqualTo(answer.getWriter());
        assertThat(foundAnswer.getContents()).isEqualTo(answer.getContents());
        assertThat(foundAnswer.getQuestionId()).isEqualTo(answer.getQuestionId());
    }

    @DisplayName("추가한 answer들을 조회한다.")
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 6, 10, 100, 500})
    void findAllByQuestionId(final int count) {
        // given
        final long questionId = new Random().nextLong();
        IntStream.range(0, count)
                .mapToObj(number -> new Answer("userId" + number, "content" + number, questionId))
                .forEach(answerService::save);

        // when
        final List<Answer> savedAnswers = answerService.findAllByQuestionId(questionId);

        // then
        assertThat(savedAnswers).hasSize(count);
    }

    @DisplayName("answer를 제거한다.")
    @Test
    void deleteById() {
        // given
        final Answer answer = new Answer("jaeyeonling", "Hello Answer", 1);
        final Answer savedAnswer = answerService.save(answer);

        // when
        answerService.deleteById(savedAnswer.getAnswerId());
        final Optional<Answer> foundAnswer = answerService.findById(savedAnswer.getAnswerId());

        // then
        assertThat(foundAnswer).isEmpty();
    }
}