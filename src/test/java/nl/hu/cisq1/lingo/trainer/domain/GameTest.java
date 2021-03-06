package nl.hu.cisq1.lingo.trainer.domain;

import nl.hu.cisq1.lingo.trainer.domain.exception.GameStateException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class GameTest
{
    private Game game;

    private static @NotNull Stream<Arguments> provideNextWordLengthExamples()
    {
        return Stream.of(arguments("borax", 6), arguments("aaiing", 7), arguments("babyvet", 5));
    }

    @BeforeEach
    void init()
    {
        this.game = new Game();
    }

    @Test
    @DisplayName("get current round returns empty when the game has no rounds")
    void currentRoundReturnsEmptyWhenNoRoundsAvailable()
    {
        assertEquals(Optional.empty(), this.game.getCurrentRound());
    }

    @Test
    @DisplayName("exception is thrown when a round is still in progress")
    void cannotStartNewRoundIfAlreadyInProgress()
    {
        this.game.startNewRound("borax");
        assertThrows(GameStateException.class, () -> this.game.startNewRound("borax"));
    }

    @Test
    @DisplayName("exception is thrown when word is guessed and there is no active round yet")
    void cannotGuessWhenNoActiveRound()
    {
        assertThrows(GameStateException.class, () -> this.game.guessWord("borax"));
    }

    @Test
    @DisplayName("exception is thrown when word is guessed and the game is not in the playing state")
    void cannotGuessWhenGameNotPlaying()
    {
        // WON
        this.game.startNewRound("borax");
        this.game.guessWord("borax");
        assertThrows(GameStateException.class, () -> this.game.guessWord("borax"));
    }

    @Test
    @DisplayName("exception is thrown when word is not guessed within attempt limit")
    void wordIsNotGuessedWithinAttemptLimit()
    {
        this.game.startNewRound("borax");
        this.game.guessWord("conto");
        this.game.guessWord("conto");
        this.game.guessWord("conto");
        this.game.guessWord("conto");
        this.game.guessWord("conto");

        assertThrows(GameStateException.class, () -> this.game.guessWord("weebo"));
    }

    @Test
    @DisplayName("player has won the game when the word is guessed")
    void playerHasWonTheGameWhenWordGuessed()
    {
        this.game.startNewRound("borax");
        this.game.guessWord("borax");

        assertEquals(GameState.WON, this.game.getGameState());
    }

    @Test
    @DisplayName("player has lost the game when the word is not guessed")
    void playerHasLostTheGameWhenWordNotGuessed()
    {
        this.game.startNewRound("borax");
        this.game.guessWord("conto");
        this.game.guessWord("conto");
        this.game.guessWord("conto");
        this.game.guessWord("conto");
        this.game.guessWord("conto");

        assertEquals(GameState.LOST, this.game.getGameState());
    }

    @Test
    @DisplayName("score is added as expected when the word is guessed")
    void scoreIsAtExpectedResult()
    {
        this.game.startNewRound("borax");
        this.game.guessWord("borax");

        assertEquals(25, this.game.getScore());
    }

    @Test
    @DisplayName("score is added as expected when the word is guessed (multiple guesses)")
    void scoreIsAtExpectedResultMultipleGuesses()
    {
        this.game.startNewRound("borax");
        this.game.guessWord("conto");
        this.game.guessWord("conto");
        this.game.guessWord("borax");

        assertEquals(15, this.game.getScore());
    }

    @Test
    @DisplayName("gives expected next word length when no round available")
    void nextWordLengthGivesExpectedResult()
    {
        assertEquals(5, this.game.getNextWordLength());
    }

    @ParameterizedTest
    @MethodSource("provideNextWordLengthExamples")
    @DisplayName("gives expected next word length based on the current word length")
    void nextWordLengthGivesExpectedResult(final String word, final int expectedNextWordLength)
    {
        this.game.startNewRound(word);
        this.game.guessWord(word);

        assertEquals(expectedNextWordLength, this.game.getNextWordLength());
    }

    @Test
    @DisplayName("the game state is still on WAITING when no rounds are available")
    void gameStateIsStillWaitingWhenNoRoundsAvailable()
    {
        this.game.performGameChecks();
        assertEquals(GameState.WAITING, this.game.getGameState());
    }

    @Test
    @DisplayName("the game state is still on PLAYING when not lost, won or waiting, word limit isn't reached and has feedback")
    void gameStateIsStillWaitingWhenNoRoundsAvailable2()
    {
        this.game.startNewRound("borax");
        this.game.guessWord("conto");

        assertEquals(GameState.PLAYING, this.game.getGameState());
    }

    @Test
    @DisplayName("the game state is still on PLAYING when not lost, won or waiting, word limit isn't reached and has no feedback")
    void gameStateIsStillWaitingWhenNoRoundsAvailable3()
    {
        this.game.startNewRound("borax");

        final Optional<Round> currentRound = this.game.getCurrentRound();

        if (currentRound.isEmpty()) throw new IllegalArgumentException("Current round is empty");

        assertNull(currentRound.get().getLastFeedback());
        assertEquals(GameState.PLAYING, this.game.getGameState());
    }

    @Test
    @DisplayName("the game state is LOST when the word guess limit is reached and the word is not guessed yet")
    void gameStateIsLostWhenWordGuessLimitIsReachedAndNotGuessed()
    {
        this.game.startNewRound("borax");
        this.game.guessWord("conto");
        this.game.guessWord("conto");
        this.game.guessWord("conto");
        this.game.guessWord("conto");
        this.game.guessWord("conto");

        final Optional<Round> currentRound = this.game.getCurrentRound();

        if (currentRound.isEmpty()) throw new IllegalArgumentException("Current round is empty");

        assertTrue(currentRound.get().isWordGuessLimitReached());
        assertFalse(currentRound.get().isWordGuessed());
        assertEquals(GameState.LOST, this.game.getGameState());
    }

    @Test
    @DisplayName("the game state is WON when the word is guessed")
    void gameStateIsWonWhenWordIsGuessed()
    {
        this.game.startNewRound("borax");
        this.game.guessWord("borax");

        assertEquals(GameState.WON, this.game.getGameState());
    }

    @Test
    @DisplayName("equals and hashcode are working as expected")
    void equalsAndHashcodeAreCorrectlyImplemented()
    {
        EqualsVerifier.forClass(Game.class).verify();
    }
}
