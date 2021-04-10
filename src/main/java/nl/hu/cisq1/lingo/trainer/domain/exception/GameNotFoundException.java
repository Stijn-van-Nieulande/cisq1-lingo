package nl.hu.cisq1.lingo.trainer.domain.exception;

public class GameNotFoundException extends RuntimeException
{
    public GameNotFoundException(final String message)
    {
        super(message);
    }
}
