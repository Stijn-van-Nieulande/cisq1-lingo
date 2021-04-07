package nl.hu.cisq1.lingo.trainer.domain;

import nl.hu.cisq1.lingo.trainer.domain.exception.InvalidFeedbackException;
import org.jetbrains.annotations.NotNull;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.Objects;

@Entity
public class Feedback
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String attempt;

    @ElementCollection
    private List<Mark> marks;

    public Feedback()
    {
    }

    public Feedback(final String attempt, @NotNull final List<Mark> marks)
    {
        this.attempt = attempt;
        this.marks = Objects.requireNonNull(marks);

        if (attempt.length() != marks.size())
            throw new InvalidFeedbackException("The specified word length does not match the marks length.");
    }

    public boolean isWordGuessed()
    {
        return this.marks.stream().allMatch(mark -> mark.equals(Mark.CORRECT));
    }

    public boolean isGuessInvalid()
    {
        return this.marks.stream().anyMatch(mark -> mark.equals(Mark.INVALID));
    }

    /**
     * Get a new hint based on the previous hint.
     *
     * @param previousHint The previous hint.
     * @return The new hint.
     */
    public char[] giveHint(final char[] previousHint)
    {
        Objects.requireNonNull(previousHint, "Previous hint cannot be null");

        if (previousHint.length != this.attempt.length())
            throw new InvalidFeedbackException("The length of the specified previous hint does not match the length of the word.");

        for (int i = 0; i < this.attempt.length(); i++) {
            if (this.marks.get(i).equals(Mark.CORRECT)) previousHint[i] = this.attempt.charAt(i);
        }

        return previousHint;
    }

    public Long getId()
    {
        return this.id;
    }

    public String getAttempt()
    {
        return this.attempt;
    }

    public List<Mark> getMarks()
    {
        return this.marks;
    }

    @Override
    public String toString()
    {
        return "Feedback{"
                + "attempt='" + this.attempt + '\''
                + ", marks=" + this.marks
                + '}';
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Feedback)) return false;
        final Feedback feedback = (Feedback) o;
        return Objects.equals(this.attempt, feedback.attempt)
                && this.marks.equals(feedback.marks);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.attempt, this.marks);
    }
}
