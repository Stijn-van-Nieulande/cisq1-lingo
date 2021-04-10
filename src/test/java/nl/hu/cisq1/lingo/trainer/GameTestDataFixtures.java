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

        System.out.println("[debug] 1.1: " + gameWithStatePlaying.getId());
        System.out.println("[debug] 1.2: " + gameWithStatePlaying.getGameState());

        final Game gameWithStateLost = new Game();

        System.out.println("[debug] 2.1: " + gameWithStateLost.getId());
        System.out.println("[debug] 2.2: " + gameWithStateLost.getGameState());

        gameWithStatePlaying.startNewRound("borax");
        gameWithStatePlaying.guessWord("conto");
        gameWithStatePlaying.guessWord("conto");
        gameWithStatePlaying.guessWord("conto");
        gameWithStatePlaying.guessWord("conto");
        gameWithStatePlaying.guessWord("conto");
        this.gameRepository.save(gameWithStateLost);
    }
}
