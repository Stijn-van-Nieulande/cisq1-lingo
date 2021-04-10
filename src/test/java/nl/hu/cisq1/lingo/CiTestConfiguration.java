package nl.hu.cisq1.lingo;

import nl.hu.cisq1.lingo.trainer.GameTestDataFixtures;
import nl.hu.cisq1.lingo.trainer.data.SpringGameRepository;
import nl.hu.cisq1.lingo.words.WordTestDataFixtures;
import nl.hu.cisq1.lingo.words.data.SpringWordRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@Profile("ci")
@TestConfiguration
public class CiTestConfiguration
{
    @Bean
    CommandLineRunner importWords(final SpringWordRepository repository)
    {
        return new WordTestDataFixtures(repository);
    }

    @Bean
    CommandLineRunner importGames(final SpringGameRepository repository)
    {
        return new GameTestDataFixtures(repository);
    }
}
