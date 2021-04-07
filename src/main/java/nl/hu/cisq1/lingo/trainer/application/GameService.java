package nl.hu.cisq1.lingo.trainer.application;

import nl.hu.cisq1.lingo.trainer.data.SpringGameRepository;
import nl.hu.cisq1.lingo.trainer.domain.Feedback;
import nl.hu.cisq1.lingo.trainer.domain.Game;
import nl.hu.cisq1.lingo.trainer.domain.exception.GameNotFoundException;
import nl.hu.cisq1.lingo.trainer.domain.exception.WordNotExistsException;
import nl.hu.cisq1.lingo.trainer.presentation.dto.ProgressDTO;
import nl.hu.cisq1.lingo.words.data.SpringWordRepository;
import nl.hu.cisq1.lingo.words.domain.Word;
import nl.hu.cisq1.lingo.words.domain.exception.WordLengthNotSupportedException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Transactional
public class GameService
{
    private final SpringGameRepository gameRepository;
    private final SpringWordRepository wordRepository;

    public GameService(final SpringGameRepository gameRepository, final SpringWordRepository wordRepository)
    {
        this.gameRepository = gameRepository;
        this.wordRepository = wordRepository;
    }

    public List<ProgressDTO> findAll()
    {
        return this.gameRepository.findAll().stream().map(this::mapGameToProgress).collect(Collectors.toList());
    }

    public ProgressDTO newGame()
    {
        final Game game = new Game();
        final Word word = this.wordRepository.findRandomWordByLength(5).orElseThrow(() -> new WordLengthNotSupportedException(5));

        game.startNewRound(word.getValue());
        this.gameRepository.save(game);

        return this.mapGameToProgress(game);
    }

    public ProgressDTO getProgress(final long gameId)
    {
        final Game game = this.findGameById(gameId);
        return this.mapGameToProgress(game);
    }

    public ProgressDTO startNewRound(final long gameId)
    {
        final Game game = this.findGameById(gameId);
        final int nextWordLength = game.getNextWordLength();

        final Word word = this.wordRepository.findRandomWordByLength(nextWordLength).orElseThrow(() -> new WordLengthNotSupportedException(nextWordLength));
        game.startNewRound(word.getValue());

        this.gameRepository.save(game);

        return this.mapGameToProgress(game);
    }

    public ProgressDTO guess(final long gameId, final String attempt)
    {
        final Game game = this.findGameById(gameId);

        if (this.wordRepository.findWordByValue(attempt).isEmpty())
            throw new WordNotExistsException("The word \"" + attempt + "\" does not exists.");

        game.guessWord(attempt);

        this.gameRepository.save(game);

        return this.mapGameToProgress(game);
    }

    private Game findGameById(final Long id)
    {
        return this.gameRepository.findById(id).orElseThrow(() -> new GameNotFoundException("Game with id " + id + " is not found."));
    }

    private ProgressDTO mapGameToProgress(final Game game)
    {
        final List<Feedback> feedbackHistory = new ArrayList<>();
        final AtomicReference<String> lastHint = new AtomicReference<>(null);

        game.getCurrentRound().ifPresent(round -> {
            feedbackHistory.addAll(round.getFeedbackHistory());
            lastHint.set(round.getLastHint());
        });

        return new ProgressDTO(
                game.getId(),
                game.getGameState().name(),
                game.getScore(),
                feedbackHistory,
                lastHint.get(),
                game.getRounds().size());
    }
}
