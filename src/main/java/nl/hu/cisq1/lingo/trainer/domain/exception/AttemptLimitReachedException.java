package nl.hu.cisq1.lingo.trainer.domain.exception;

public class AttemptLimitReachedException extends RuntimeException
{
    public AttemptLimitReachedException(final String message)
    {
        super(message);
    }
}
