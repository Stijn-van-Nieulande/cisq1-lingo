package nl.hu.cisq1.lingo.trainer.domain;

import nl.hu.cisq1.lingo.trainer.domain.exception.AttemptLimitReachedException;
import nl.hu.cisq1.lingo.trainer.domain.exception.InvalidFeedbackException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoundTest
{
    private Round round;

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
    @DisplayName("feedback is invalid when attempt length does not match word length")
    void feedbackInvalidWhenLengthInvalid()
    {
        assertThrows(InvalidFeedbackException.class, () -> this.round.createFeedback("test"));
    }

    @Test
    @DisplayName("get last feedback returns empty when the round has no feedback history")
    void lastFeedbackReturnsEmptyWhenNoFeedbackAvailable()
    {
        assertTrue(this.round.getFeedbackHistory().isEmpty());
        assertEquals(Optional.empty(), this.round.getLastFeedback());
    }

    @Test
    @DisplayName("get last feedback returns the last feedback as expected")
    void lastFeedbackReturnsLastFeedbackAsExpected()
    {
        this.round.guess("bompa");

        final Feedback expectedFeedback = new Feedback("bompa", List.of(Mark.CORRECT, Mark.CORRECT, Mark.ABSENT, Mark.PRESENT, Mark.ABSENT));
        final @NotNull Optional<Feedback> lastFeedback = this.round.getLastFeedback();

        if (lastFeedback.isEmpty()) throw new IllegalArgumentException("Last feedback is empty");

        assertEquals(expectedFeedback, lastFeedback.get());
    }
}
