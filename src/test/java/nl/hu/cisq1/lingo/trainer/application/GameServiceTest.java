package nl.hu.cisq1.lingo.trainer.application;

import nl.hu.cisq1.lingo.trainer.data.SpringGameRepository;
import nl.hu.cisq1.lingo.trainer.domain.Feedback;
import nl.hu.cisq1.lingo.trainer.domain.Game;
import nl.hu.cisq1.lingo.trainer.domain.GameState;
import nl.hu.cisq1.lingo.trainer.domain.Mark;
import nl.hu.cisq1.lingo.trainer.domain.exception.GameNotFoundException;
import nl.hu.cisq1.lingo.trainer.domain.exception.WordNotExistsException;
import nl.hu.cisq1.lingo.trainer.presentation.dto.ProgressDTO;
import nl.hu.cisq1.lingo.words.data.SpringWordRepository;
import nl.hu.cisq1.lingo.words.domain.Word;
import nl.hu.cisq1.lingo.words.domain.exception.WordLengthNotSupportedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameServiceTest
{
    private SpringGameRepository gameRepository;
    private SpringWordRepository wordRepository;
    private GameService gameService;

    @BeforeEach
    void init()
    {
        this.gameRepository = mock(SpringGameRepository.class);
        this.wordRepository = mock(SpringWordRepository.class);
        this.gameService = new GameService(this.gameRepository, this.wordRepository);
    }

    @Test
    @DisplayName("provides all registered games")
    void provideAllGames()
    {
        final Game game = new Game();
        final List<Game> games = List.of(game);

        when(this.gameRepository.findAll()).thenReturn(games);

        assertEquals(1, this.gameService.findAll().size());
    }

    @Test
    @DisplayName("starting a new game returns the correct progress")
    void newGameReturnsNewGameProgress()
    {
        final ProgressDTO expectedProgress = new ProgressDTO(null, GameState.PLAYING.name(), 0, List.of(), "b....", 1);

        when(this.wordRepository.findRandomWordByLength(5)).thenReturn(Optional.of(new Word("borax")));

        assertEquals(expectedProgress, this.gameService.newGame());
    }

    @Test
    @DisplayName("starting a new game throws exception when word length not supported")
    void newGameThrowsExceptionWhenWordLengthNotSupported()
    {
        assertThrows(WordLengthNotSupportedException.class, () -> this.gameService.newGame());
    }

    @Test
    @DisplayName("requesting the game progress by game id returns the expected progress result")
    void getProgressReturnsExpectedGameProgress()
    {
        final Game game = new Game();
        final ProgressDTO expectedProgress = new ProgressDTO(null, GameState.WAITING.name(), 0, List.of(), null, 0);

        when(this.gameRepository.findById(0L)).thenReturn(Optional.of(game));

        assertEquals(expectedProgress, this.gameService.getProgress(0L));
    }

    @Test
    @DisplayName("requesting the game progress by game id throws exception when no game found")
    void getProgressThrowsWhenNoGameFound()
    {
        assertThrows(GameNotFoundException.class, () -> this.gameService.getProgress(0L));
    }

    @Test
    @DisplayName("requesting a new round by game id returns the expected progress result")
    void newRoundReturnsExpectedProgress()
    {
        final Game game = new Game();
        final ProgressDTO expectedProgress = new ProgressDTO(null, GameState.PLAYING.name(), 0, List.of(), "b....", 1);

        when(this.gameRepository.findById(0L)).thenReturn(Optional.of(game));
        when(this.wordRepository.findRandomWordByLength(5)).thenReturn(Optional.of(new Word("borax")));

        assertEquals(expectedProgress, this.gameService.startNewRound(0L));
    }

    @Test
    @DisplayName("requesting a new round by game id throws exception when no game found")
    void newRoundThrowsWhenNoGameFound()
    {
        assertThrows(GameNotFoundException.class, () -> this.gameService.startNewRound(0L));
    }

    @Test
    @DisplayName("requesting a new round by game id throws exception when word length not supported")
    void newRoundThrowsWhenWordLengthNotSupported()
    {
        final Game game = new Game();

        when(this.gameRepository.findById(0L)).thenReturn(Optional.of(game));

        assertThrows(WordLengthNotSupportedException.class, () -> this.gameService.startNewRound(0L));
    }

    @Test
    @DisplayName("guessing a word on the given game id returns the expected progress result")
    void guessingGivesExpectedResult()
    {
        final Game game = new Game();
        game.startNewRound("borax");

        final Feedback expectedFeedback = new Feedback("conto", List.of(Mark.ABSENT, Mark.CORRECT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT));
        final ProgressDTO expectedProgress = new ProgressDTO(null, GameState.PLAYING.name(), 0, List.of(expectedFeedback), "bo...", 1);

        when(this.gameRepository.findById(0L)).thenReturn(Optional.of(game));
        when(this.wordRepository.findWordByValue("conto")).thenReturn(Optional.of(new Word("conto")));

        assertEquals(expectedProgress, this.gameService.guess(0L, "conto"));
    }

    @Test
    @DisplayName("guessing a word on the given game id throws exception when no game found")
    void guessingThrowsWhenNoGameFound()
    {
        assertThrows(GameNotFoundException.class, () -> this.gameService.guess(0L, "conto"));
    }

    @Test
    @DisplayName("guessing a word on the given game id throws exception when word doesn't exists")
    void guessingThrowsWhenWordNotExists()
    {
        final Game game = new Game();

        when(this.gameRepository.findById(0L)).thenReturn(Optional.of(game));

        assertThrows(WordNotExistsException.class, () -> this.gameService.guess(0L, "conto"));
    }
}
