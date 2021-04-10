package nl.hu.cisq1.lingo.trainer.presentation.controller;

import nl.hu.cisq1.lingo.CiTestConfiguration;
import nl.hu.cisq1.lingo.trainer.data.SpringGameRepository;
import nl.hu.cisq1.lingo.trainer.domain.Game;
import nl.hu.cisq1.lingo.trainer.domain.GameState;
import nl.hu.cisq1.lingo.words.data.SpringWordRepository;
import nl.hu.cisq1.lingo.words.domain.Word;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Import(CiTestConfiguration.class)
@AutoConfigureMockMvc
class GameControllerIntegrationTest
{
    @MockBean
    private SpringGameRepository gameRepository;

    @MockBean
    private SpringWordRepository wordRepository;

    @Autowired
    private MockMvc mockMvc;

    private Game game;

    @BeforeEach
    @DisplayName("setup before each test")
    void init()
    {
        this.game = new Game();
        this.game.startNewRound("borax");
        this.gameRepository.save(this.game);
    }

    @AfterEach
    @DisplayName("cleanup after each test")
    void teardown()
    {
        this.gameRepository.deleteAll();
    }

    @Test
    void findAll()
    {
    }

    @Test
    @DisplayName("should start a new game")
    void startNewGame() throws Exception
    {
        final RequestBuilder request = MockMvcRequestBuilders.post("/trainer");

        when(this.wordRepository.findRandomWordByLength(5)).thenReturn(Optional.of(new Word("conto")));

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.gameStatus", is(GameState.PLAYING.name())))
                .andExpect(jsonPath("$.score", is(0)))
                .andExpect(jsonPath("$.feedbackHistory", hasSize(0)))
                .andExpect(jsonPath("$.lastHint", is("c....")))
                .andExpect(jsonPath("$.numberOfRounds", is(1)));
    }

    @Test
    void getGameProgress()
    {
    }

    @Test
    @DisplayName("should start a new round")
    void startNewRound() throws Exception
    {
        when(this.gameRepository.findById(0L)).thenReturn(Optional.of(this.game));
        when(this.wordRepository.findRandomWordByLength(6)).thenReturn(Optional.of(new Word("aaiing")));

        // make sure a new round can be started
        this.game.guessWord("borax");
        this.gameRepository.save(this.game);

        final RequestBuilder request = MockMvcRequestBuilders.post("/trainer/{gameId}/new-round", 0);

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.gameStatus", is(GameState.PLAYING.name())))
                .andExpect(jsonPath("$.score", is(25)))
                .andExpect(jsonPath("$.feedbackHistory", hasSize(0)))
                .andExpect(jsonPath("$.lastHint", is("a.....")))
                .andExpect(jsonPath("$.numberOfRounds", is(2)));
    }

    @Test
    void guess()
    {
    }
}
