package nl.hu.cisq1.lingo.trainer.presentation.dto;

import nl.hu.cisq1.lingo.trainer.domain.Feedback;

import java.util.List;

public class ProgressDTO
{
    public final long id;
    public final String gameStatus;
    public final int score;
    public final List<Feedback> feedbackHistory;
    public final String lastHint;
    public final int numberOfRounds;

    public ProgressDTO(final long id, final String gameStatus, final int score, final List<Feedback> feedbackHistory, final String lastHint, final int numberOfRounds)
    {
        this.id = id;
        this.gameStatus = gameStatus;
        this.score = score;
        this.feedbackHistory = feedbackHistory;
        this.lastHint = lastHint;
        this.numberOfRounds = numberOfRounds;
    }
}
