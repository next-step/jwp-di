package next.service;

import next.model.Question;
import next.repository.JdbcQuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionServiceTest {

    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        final JdbcQuestionRepository jdbcQuestionRepository = new JdbcQuestionRepository();

        questionService = new QuestionService(jdbcQuestionRepository);
    }

    @DisplayName("question을 추가한다.")
    @Test
    void save() {
        // given
        final Question question = new Question("jaeyeonling", "Hello Question", "Nice meet you");

        // when
        final Question savedQuestion = questionService.save(question);

        // then
        assertThat(savedQuestion.getWriter()).isEqualTo(question.getWriter());
        assertThat(savedQuestion.getTitle()).isEqualTo(question.getTitle());
        assertThat(savedQuestion.getContents()).isEqualTo(question.getContents());
    }

    @DisplayName("추가한 question을 조회한다.")
    @Test
    void findById() {
        // given
        final Question question = new Question("jaeyeonling", "Hello Question", "Nice meet you");
        final Question savedQuestion = questionService.save(question);

        // when
        final Question foundQuestion = questionService.findById(savedQuestion.getQuestionId()).get();

        // then
        assertThat(foundQuestion.getWriter()).isEqualTo(question.getWriter());
        assertThat(foundQuestion.getTitle()).isEqualTo(question.getTitle());
        assertThat(foundQuestion.getContents()).isEqualTo(question.getContents());
    }

    @DisplayName("추가한 question들을 조회한다.")
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 6, 10, 100, 500})
    void findAll(final int count) {
        // given
        questionService.deleteAll();
        IntStream.range(0, count)
                .mapToObj(number -> new Question("userId" + number,
                        "title" + number, "contents" + number))
                .forEach(questionService::save);

        // when
        final List<Question> savedQuestions = questionService.findAll();

        // then
        assertThat(savedQuestions).hasSize(count);
    }

    @DisplayName("question을 수정한다.")
    @Test
    void update() {
        // given
        final Question savedQuestion = questionService.save(new Question("jaeyeonling",
                "Hello Question", "Nice meet you"));

        final Question updateQuestion = new Question("jaeyeonling", "HELL O Question",
                "Nice meet you too");

        // when
        savedQuestion.update(updateQuestion);
        questionService.update(savedQuestion);
        final Question foundQuestion = questionService.findById(savedQuestion.getQuestionId()).get();

        // then
        assertThat(foundQuestion.getTitle()).isEqualTo(updateQuestion.getTitle());
        assertThat(foundQuestion.getContents()).isEqualTo(updateQuestion.getContents());
    }

    @DisplayName("question의 댓글 갯수를 증가한다.")
    @Test
    void updateCountOfAnswer() {
        // given
        final Question savedQuestion = questionService.save(new Question("jaeyeonling",
                "Hello Question", "Nice meet you"));
        final int beforeCountOfAnswer = savedQuestion.getCountOfAnswer();

        // when
        questionService.updateCountOfAnswer(savedQuestion.getQuestionId());
        final Question foundQuestion = questionService.findById(savedQuestion.getQuestionId()).get();

        // then
        assertThat(foundQuestion.getCountOfAnswer()).isEqualTo(beforeCountOfAnswer);
    }

    @DisplayName("question를 제거한다.")
    @Test
    void deleteById() {
        // given
        final Question savedQuestion = questionService.save(new Question("jaeyeonling",
                "Hello Question", "Nice meet you"));

        // when
        questionService.deleteById(savedQuestion.getQuestionId());
        final Optional<Question> foundQuestion = questionService.findById(savedQuestion.getQuestionId());

        // then
        assertThat(foundQuestion).isEmpty();
    }
}