package nl.hu.cisq1.lingo.trainer.domain.exception;

public class WordNotExistsException extends RuntimeException
{
    public WordNotExistsException(final String message)
    {
        super(message);
    }
}
