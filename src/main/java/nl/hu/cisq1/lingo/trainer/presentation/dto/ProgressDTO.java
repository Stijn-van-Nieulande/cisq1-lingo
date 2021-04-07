package nl.hu.cisq1.lingo.trainer.presentation.dto;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import nl.hu.cisq1.lingo.trainer.domain.Feedback;

import java.util.List;

@EqualsAndHashCode
@ToString
public class ProgressDTO
{
    public final Long id;
    public final String gameStatus;
    public final int score;
    public final List<Feedback> feedbackHistory;
    public final String lastHint;
    public final int numberOfRounds;

    public ProgressDTO(final Long id, final String gameStatus, final int score, final List<Feedback> feedbackHistory, final String lastHint, final int numberOfRounds)
    {
        this.id = id;
        this.gameStatus = gameStatus;
        this.score = score;
        this.feedbackHistory = feedbackHistory;
        this.lastHint = lastHint;
        this.numberOfRounds = numberOfRounds;
    }
}
