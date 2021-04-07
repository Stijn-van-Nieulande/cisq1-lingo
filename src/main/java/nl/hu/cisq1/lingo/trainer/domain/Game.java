package nl.hu.cisq1.lingo.trainer.domain;

import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import nl.hu.cisq1.lingo.trainer.domain.exception.GameStateException;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode
@ToString
@Entity
@Table(name = "game")
public class Game
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int score;

    @Enumerated(EnumType.STRING)
    private GameState gameState = GameState.WAITING;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Round> rounds = new ArrayList<>();

    public Game()
    {
    }

    @NotNull
    public Optional<Round> getCurrentRound()
    {
        if (this.rounds.isEmpty()) return Optional.empty();
        return Optional.of(this.rounds.get(this.rounds.size() - 1));
    }

    /**
     * Start a new round if possible with the chosen word to guess.
     *
     * @param word The word to guess.
     */
    public void startNewRound(@NotNull final String word)
    {
        if (this.isPlaying()) throw new GameStateException("A round is already in progress.");
        this.rounds.add(new Round(word));
        this.gameState = GameState.PLAYING;
    }

    /**
     * Try to guess the word.
     *
     * @param attempt The word guess attempt.
     */
    public void guessWord(@NotNull final String attempt)
    {
        Objects.requireNonNull(attempt, "Attempt cannot be null.");
        if (!this.isPlaying()) throw new GameStateException("There is no active round yet.");
        this.getCurrentRound().ifPresent(round -> round.guess(attempt));
        this.performGameChecks();
    }

    public boolean isPlaying()
    {
        return this.gameState.equals(GameState.PLAYING);
    }

    private void performGameChecks()
    {
        final Optional<Round> currentRound = this.getCurrentRound();
        if (currentRound.isEmpty()) return;

        if (currentRound.get().isWordGuessLimitReached() && !currentRound.get().isWordGuessed()) {
            this.gameState = GameState.LOST;
            return;
        }

        final Optional<Feedback> lastAttempt = currentRound.get().getLastFeedback();
        if (lastAttempt.isEmpty()) return;

        if (lastAttempt.get().isWordGuessed()) {
            this.score += 5 * (5 - currentRound.get().getAttempts()) + 5;
            this.gameState = GameState.WON;
        }
    }

    /**
     * Ge the next word length based on the last round.
     * If the last round had a word length of 5 then the next word length would be 6. This counts until 7 and then the
     * word length falls back again to 5. So if the last word length is 7 the next word length would be 5.
     *
     * @return The next word length.
     */
    public int getNextWordLength()
    {
        final Optional<Round> round = this.getCurrentRound();
        if (round.isEmpty()) return 5;
        return Math.max(5, round.get().getWordLength() % 7 + 1);
    }

    public Long getId()
    {
        return this.id;
    }

    public int getScore()
    {
        return this.score;
    }

    public GameState getGameState()
    {
        return this.gameState;
    }

    public List<Round> getRounds()
    {
        return this.rounds;
    }
}
