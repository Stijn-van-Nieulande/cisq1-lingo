package nl.hu.cisq1.lingo.trainer.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import nl.hu.cisq1.lingo.trainer.domain.exception.AttemptLimitReachedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
@Table(name = "round")
public class Round
{
    private static final int ATTEMPT_LIMIT = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Exclude
    private Long id;

    private String wordToGuess;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Feedback> feedbackHistory = new ArrayList<>();

    private Integer attempts = 0;

    @NotNull
    private String lastHint = "";

    public Round(final String wordToGuess)
    {
        this.wordToGuess = wordToGuess;
        this.lastHint = wordToGuess.charAt(0) + ".".repeat(wordToGuess.length() - 1);
    }

    public Round()
    {
    }

    public int getWordLength()
    {
        return this.wordToGuess.length();
    }

    public boolean isWordGuessed()
    {
        if (this.feedbackHistory.isEmpty()) return false;
        return this.feedbackHistory.get(this.feedbackHistory.size() - 1).isWordGuessed();
    }

    public boolean isWordGuessLimitReached()
    {
        return this.attempts >= ATTEMPT_LIMIT;
    }

    /**
     * Try to guess the word.
     *
     * @param attempt The word guess attempt.
     */
    public void guess(@NotNull final String attempt)
    {
        Objects.requireNonNull(attempt, "Attempt cannot be null.");

        if (this.isWordGuessLimitReached())
            throw new AttemptLimitReachedException("The attempt limit of " + ATTEMPT_LIMIT + " has been reached.");

        this.attempts++;

        this.createFeedback(attempt);
        this.giveHint();
    }

    /**
     * Create new feedback based on the word attempt and the word to guess.
     *
     * @param attempt The word guess attempt.
     */
    public void createFeedback(@NotNull final String attempt)
    {
        Objects.requireNonNull(attempt, "Attempt cannot be null.");
        final List<Mark> marks = new ArrayList<>();

        if (attempt.length() != this.wordToGuess.length()) {
            for (int i = 0; i < this.wordToGuess.length(); i++) {
                marks.add(Mark.INVALID);
            }
            this.feedbackHistory.add(new Feedback(attempt, marks));
            return;
        }

        final char[] attemptCharArray = attempt.toCharArray();
        final char[] wordCharArray = this.wordToGuess.toCharArray();

        // dirty way to quickly preload the list with all absent marks
        for (int i = 0; i < this.wordToGuess.length(); i++) marks.add(Mark.ABSENT);

        for (int i = 0; i < this.wordToGuess.length(); i++) {
            if (attemptCharArray[i] == wordCharArray[i]) marks.set(i, Mark.CORRECT);
            else {
                final int letterIndex = this.wordToGuess.indexOf(attemptCharArray[i]);
                if (letterIndex != -1 && marks.get(letterIndex).equals(Mark.ABSENT))
                    marks.set(letterIndex, Mark.PRESENT);
            }
        }

        this.feedbackHistory.add(new Feedback(attempt, marks));
    }

    private void giveHint()
    {
        final Feedback lastFeedback = this.getLastFeedback();
        if (lastFeedback == null) return;
        this.lastHint = new String(lastFeedback.giveHint(this.lastHint.toCharArray()));
    }

    public List<Feedback> getFeedbackHistory()
    {
        return this.feedbackHistory;
    }

    @Nullable
    public Feedback getLastFeedback()
    {
        if (this.feedbackHistory.isEmpty()) return null;
        return this.feedbackHistory.get(this.feedbackHistory.size() - 1);
    }

    public Integer getAttempts()
    {
        return this.attempts;
    }

    public @NotNull String getLastHint()
    {
        return this.lastHint;
    }
}
