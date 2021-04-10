package nl.hu.cisq1.lingo.trainer;

import nl.hu.cisq1.lingo.trainer.data.SpringGameRepository;
import nl.hu.cisq1.lingo.trainer.domain.Game;
import org.springframework.boot.CommandLineRunner;

public class GameTestDataFixtures implements CommandLineRunner
{
    private final SpringGameRepository gameRepository;

    public GameTestDataFixtures(final SpringGameRepository gameRepository)
    {
        this.gameRepository = gameRepository;
    }

    @Override
    public void run(final String... args) throws Exception
    {
        final Game gameWithStatePlaying = new Game();
        gameWithStatePlaying.startNewRound("borax");
        this.gameRepository.save(gameWithStatePlaying);

        final Game gameWithStateLost = new Game();
        gameWithStateLost.startNewRound("borax");
        gameWithStateLost.guessWord("conto");
        gameWithStateLost.guessWord("conto");
        gameWithStateLost.guessWord("conto");
        gameWithStateLost.guessWord("conto");
        gameWithStateLost.guessWord("conto");
        this.gameRepository.save(gameWithStateLost);
    }
}
