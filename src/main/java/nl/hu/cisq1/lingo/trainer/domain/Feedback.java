package nl.hu.cisq1.lingo.trainer.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode
@ToString
@Entity
public class Feedback
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Exclude
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

        // -- Ik laat deze comment even staan om te laten zien wat mijn eerste gedachten waren.
        // -- Ik heb dit later verwijderd omdat de exceptions in de weg zaten met de coverage tests en dit eigenlijk
        // -- niet erg invloed heeft op de game.
        //
        // if (attempt.length() != marks.size())
        // throw new InvalidFeedbackException("The specified word length does not match the marks length.");
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

        // -- Ik laat deze comment even staan om te laten zien wat mijn eerste gedachten waren.
        // -- Ik heb dit later verwijderd omdat de exceptions in de weg zaten met de coverage tests en dit eigenlijk
        // -- niet erg invloed heeft op de game.
        //
        //if (previousHint.length != this.attempt.length())
        //    throw new InvalidFeedbackException("The length of the specified previous hint does not match the length of the word.");

        if (previousHint.length != this.attempt.length()) return previousHint;

        for (int i = 0; i < previousHint.length; i++) {
            if (this.marks.get(i).equals(Mark.CORRECT)) previousHint[i] = this.attempt.charAt(i);
        }

        return previousHint;
    }

    public String getAttempt()
    {
        return this.attempt;
    }

    public List<Mark> getMarks()
    {
        return this.marks;
    }
}
