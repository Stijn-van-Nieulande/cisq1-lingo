package nl.hu.cisq1.lingo.trainer.domain;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class Feedback
{
    private final String attempt;
    @NotNull
    private final List<Mark> marks;

    public Feedback(String attempt, @NotNull List<Mark> marks)
    {
        this.attempt = attempt;
        this.marks = Objects.requireNonNull(marks);
    }

    public boolean isWordGuessed()
    {
        return this.marks.stream().allMatch(mark -> mark.equals(Mark.CORRECT));
    }

    public boolean isGuessInvalid()
    {
        return this.marks.stream().anyMatch(mark -> mark.equals(Mark.INVALID));
    }

//    public char[] giveHint()
//    {
//
//    }

    @Override
    public String toString()
    {
        return "Feedback{"
                + "attempt='" + this.attempt + '\''
                + ", marks=" + this.marks
                + '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Feedback)) return false;
        Feedback feedback = (Feedback) o;
        return Objects.equals(this.attempt, feedback.attempt)
                && this.marks.equals(feedback.marks);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.attempt, this.marks);
    }
}
