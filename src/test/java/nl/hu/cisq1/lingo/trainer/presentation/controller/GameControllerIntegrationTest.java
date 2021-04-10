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

import java.util.List;
import java.util.Optional;

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
    @DisplayName("get all registered games should provide one game")
    void findAll() throws Exception
    {
        when(this.gameRepository.findAll()).thenReturn(List.of(this.game));

        final RequestBuilder request = MockMvcRequestBuilders.get("/trainer");

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("should start a new game")
    void startNewGame() throws Exception
    {
        when(this.wordRepository.findRandomWordByLength(5)).thenReturn(Optional.of(new Word("conto")));

        final RequestBuilder request = MockMvcRequestBuilders.post("/trainer");

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
    @DisplayName("start a new game should fail if the word length is not supported")
    void startNewGameShouldFailIfWordLengthNotSupported() throws Exception
    {
        final RequestBuilder request = MockMvcRequestBuilders.post("/trainer");
        this.mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("get progress should return the expected game progress")
    void getGameProgress() throws Exception
    {
        when(this.gameRepository.findById(0L)).thenReturn(Optional.of(this.game));

        final RequestBuilder request = MockMvcRequestBuilders.get("/trainer/{gameId}", 0);

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.gameStatus", is(GameState.PLAYING.name())))
                .andExpect(jsonPath("$.score", is(0)))
                .andExpect(jsonPath("$.feedbackHistory", hasSize(0)))
                .andExpect(jsonPath("$.lastHint", is("b....")))
                .andExpect(jsonPath("$.numberOfRounds", is(1)));
    }

    @Test
    @DisplayName("get progress should fail if the game is not found")
    void getGameProgressShouldFailIfTheGameIsNotFound() throws Exception
    {
        final RequestBuilder request = MockMvcRequestBuilders.get("/trainer/{gameId}", 0);
        this.mockMvc.perform(request).andExpect(status().isNotFound());
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
    @DisplayName("start a new round should fail if the game is not found")
    void startNewRoundShouldFailIfTheGameIsNotFound() throws Exception
    {
        final RequestBuilder request = MockMvcRequestBuilders.post("/trainer/{gameId}/new-round", 0);
        this.mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("start a new round should fail if another round is already in progress")
    void startNewRoundShouldFailIfAnotherRoundIsAlreadyInProgress() throws Exception
    {
        when(this.gameRepository.findById(0L)).thenReturn(Optional.of(this.game));

        final RequestBuilder request = MockMvcRequestBuilders.post("/trainer/{gameId}/new-round", 0);
        this.mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should provide new hint after guessing")
    void guessProvidesNewHint() throws Exception
    {
        when(this.gameRepository.findById(0L)).thenReturn(Optional.of(this.game));
        when(this.wordRepository.findWordByValue("conto")).thenReturn(Optional.of(new Word("conto")));

        final RequestBuilder request = MockMvcRequestBuilders
                .post("/trainer/{gameId}/guess", 0)
                .param("attempt", "conto");

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.gameStatus", is(GameState.PLAYING.name())))
                .andExpect(jsonPath("$.score", is(0)))
                .andExpect(jsonPath("$.feedbackHistory", hasSize(1)))
                .andExpect(jsonPath("$.lastHint", is("bo...")))
                .andExpect(jsonPath("$.numberOfRounds", is(1)));
    }

    @Test
    @DisplayName("guess should fail if the game is not found")
    void guessShouldFailIfTheGameIsNotFound() throws Exception
    {
        final RequestBuilder request = MockMvcRequestBuilders
                .post("/trainer/{gameId}/guess", 0)
                .param("attempt", "conto");
        this.mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("guess should fail if the round has ended")
    void guessShouldFailIfRoundHasEnded() throws Exception
    {
        when(this.gameRepository.findById(0L)).thenReturn(Optional.of(this.game));
        when(this.wordRepository.findWordByValue("conto")).thenReturn(Optional.of(new Word("conto")));

        this.game.guessWord("borax");
        this.gameRepository.save(this.game);

        final RequestBuilder request = MockMvcRequestBuilders
                .post("/trainer/{gameId}/guess", 0)
                .param("attempt", "conto");
        this.mockMvc.perform(request).andExpect(status().isBadRequest());
    }
}
