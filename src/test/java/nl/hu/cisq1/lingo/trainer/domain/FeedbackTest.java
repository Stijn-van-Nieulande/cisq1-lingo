package nl.hu.cisq1.lingo.trainer.domain;

import nl.hu.cisq1.lingo.trainer.domain.exception.InvalidFeedbackException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class FeedbackTest
{
    private static @NotNull Stream<Arguments> provideHintExamples()
    {
        return Stream.of(
                arguments("a....", "a....", new Feedback("adder", List.of(Mark.CORRECT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT))),
                arguments("a...r", "a....", new Feedback("adder", List.of(Mark.CORRECT, Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.CORRECT))),
                arguments("a.d.r", "a...r", new Feedback("adder", List.of(Mark.CORRECT, Mark.INVALID, Mark.CORRECT, Mark.PRESENT, Mark.CORRECT)))
        );
    }

    @Test
    @DisplayName("word is guessed if all letters are correct")
    public void wordIsGuessed()
    {
        final Feedback feedback = new Feedback("woord", List.of(Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT));
        assertTrue(feedback.isWordGuessed());
    }

    @Test
    @DisplayName("word is not guessed if some of the letters are not correct")
    public void wordIsNotGuessed()
    {
        final Feedback feedback = new Feedback("woord", List.of(Mark.INVALID, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT));
        assertFalse(feedback.isWordGuessed());
    }

    @Test
    @DisplayName("guess is invalid if some of the letters are invalid")
    public void guessIsInvalid()
    {
        final Feedback feedback = new Feedback("woord", List.of(Mark.INVALID, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT));
        assertTrue(feedback.isGuessInvalid());
    }

    @Test
    @DisplayName("guess is not invalid if none of the letters are invalid")
    public void guessIsNotInvalid() // Oftewel isValid? (⓿_⓿)
    {
        final Feedback feedback = new Feedback("woord", List.of(Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT));
        assertFalse(feedback.isGuessInvalid());
    }

    @Test
    @DisplayName("exception is thrown when word length doesn't match marks length")
    public void wordLengthDoesNotCorrespond()
    {
        assertThrows(InvalidFeedbackException.class, () -> new Feedback("woord", List.of(Mark.CORRECT)));
    }

    @Test
    @DisplayName("exception is thrown when word length doesn't match previous hint length")
    public void wordLengthDoesNotCorrespondPreviousHintLength()
    {
        assertThrows(InvalidFeedbackException.class, () -> {
            final Feedback feedback = new Feedback("woord", List.of(Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT));
            feedback.giveHint("w...".toCharArray());
        });
    }

    @ParameterizedTest
    @MethodSource("provideHintExamples")
    @DisplayName("gives hint based on provided previous hint")
    public void giveHintHasExpectedResult(@NotNull final String expected, @NotNull final String previousHint, @NotNull final Feedback feedback)
    {
        Objects.requireNonNull(expected);
        Objects.requireNonNull(previousHint);
        Objects.requireNonNull(feedback);

        final char[] expectedHint = expected.toCharArray();
        final char[] givenHint = feedback.giveHint(previousHint.toCharArray());

        assertArrayEquals(expectedHint, givenHint);
    }

    @Test
    @DisplayName("equals and hashcode are working as expected")
    public void equalsAndHashcodeAreCorrectlyImplemented()
    {
        final Feedback feedback1 = new Feedback("woord", List.of(Mark.INVALID, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT));
        final Feedback feedback2 = new Feedback("woord", List.of(Mark.INVALID, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT));
        final Feedback feedback3 = new Feedback("adder", List.of(Mark.INVALID, Mark.PRESENT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT));
        final Feedback feedback4 = new Feedback("adder", List.of(Mark.INVALID, Mark.ABSENT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT));

        assertEquals(feedback1, feedback1); // if (this == o) return true;
        assertNotEquals(feedback1, new Object()); // if (!(o instanceof Feedback)) return false;
        assertNotEquals(feedback1, feedback3); // Objects.equals(this.attempt, feedback.attempt)
        assertNotEquals(feedback3, feedback4); // && this.marks.equals(feedback.marks);

        assertEquals(feedback1.hashCode(), feedback2.hashCode()); // hashcode
    }
}
