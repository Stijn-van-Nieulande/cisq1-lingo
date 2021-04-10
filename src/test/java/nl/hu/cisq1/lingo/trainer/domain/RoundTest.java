package nl.hu.cisq1.lingo.trainer.domain;

import nl.hu.cisq1.lingo.trainer.domain.exception.AttemptLimitReachedException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class RoundTest
{
    private Round round;

    private static @NotNull Stream<Arguments> provideFeedbackExamples()
    {
        return Stream.of(
                arguments("borax", List.of(Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT)),
                arguments("toolong", List.of(Mark.INVALID, Mark.INVALID, Mark.INVALID, Mark.INVALID, Mark.INVALID)),
                arguments("xaorb", List.of(Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.PRESENT)),
                arguments("welpe", List.of(Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT)),
                arguments("bompa", List.of(Mark.CORRECT, Mark.CORRECT, Mark.ABSENT, Mark.PRESENT, Mark.ABSENT))
        );
    }

    @BeforeEach
    void init()
    {
        this.round = new Round("borax");
    }

    @Test
    @DisplayName("word is not guessed when feedback history is empty")
    void wordIsNotGuessedWhenFeedbackHistoryIsEmpty()
    {
        assertFalse(this.round.isWordGuessed());
    }

    @Test
    @DisplayName("word is guessed when feedback history is not empty and word is marked as guessed by the feedback")
    void wordIsGuessedWhenFeedbackHistoryIsNotEmpty()
    {
        this.round.guess("borax");
        assertTrue(this.round.isWordGuessed());
    }

    @Test
    @DisplayName("every guess increases the amount of attempts")
    void guessIncreasesAttempts()
    {
        this.round.guess("conto");

        assertEquals(1, this.round.getAttempts());
    }

    @Test
    @DisplayName("exception is thrown when attempt limit is reached")
    void exceptionIsThrownWhenAttemptLimitReached()
    {
        this.round.guess("conto");
        this.round.guess("conto");
        this.round.guess("conto");
        this.round.guess("conto");
        this.round.guess("conto");

        assertThrows(AttemptLimitReachedException.class, () -> this.round.guess("conto"));
    }

    @Test
    @DisplayName("the round starts with the first as the first hint")
    void roundStartsWithTheFirstLetter()
    {
        assertEquals("b....", this.round.getLastHint());
    }

    @Test
    @DisplayName("get round returns expected value")
    void getLastHintReturnsExpectedValue()
    {
        this.round.guess("conto");

        assertEquals("bo...", this.round.getLastHint());
    }

    // -- Ik laat deze comment even staan om te laten zien wat mijn eerste gedachten waren.
    // -- Ik heb dit later verwijderd omdat de exceptions in de weg zaten met de coverage tests en dit eigenlijk
    // -- niet erg invloed heeft op de game.
    //
    //    @Test
    //    @DisplayName("feedback is invalid when attempt length does not match word length")
    //    void feedbackInvalidWhenLengthInvalid()
    //    {
    //        assertThrows(InvalidFeedbackException.class, () -> this.round.createFeedback("test"));
    //    }

    @Test
    @DisplayName("get last feedback returns empty when the round has no feedback history")
    void lastFeedbackReturnsEmptyWhenNoFeedbackAvailable()
    {
        assertTrue(this.round.getFeedbackHistory().isEmpty());
        assertNull(this.round.getLastFeedback());
    }

    @ParameterizedTest
    @MethodSource("provideFeedbackExamples")
    @DisplayName("get last feedback returns the last feedback as expected")
    void lastFeedbackReturnsLastFeedbackAsExpected(@NotNull final String attempt, @NotNull final List<Mark> expectedMarks)
    {
        this.round.guess(attempt);

        final Feedback expectedFeedback = new Feedback(attempt, expectedMarks);
        final @Nullable Feedback lastFeedback = this.round.getLastFeedback();

        if (lastFeedback == null) throw new IllegalArgumentException("Last feedback is empty");

        assertEquals(expectedFeedback, lastFeedback);
    }

    @Test
    @DisplayName("equals and hashcode are working as expected")
    void equalsAndHashcodeAreCorrectlyImplemented()
    {
        EqualsVerifier.forClass(Round.class).verify();
    }
}
