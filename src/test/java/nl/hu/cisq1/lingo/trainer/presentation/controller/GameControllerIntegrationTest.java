package nl.hu.cisq1.lingo.trainer.presentation.controller;

import nl.hu.cisq1.lingo.CiTestConfiguration;
import nl.hu.cisq1.lingo.trainer.data.SpringGameRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@Import(CiTestConfiguration.class)
@AutoConfigureMockMvc
class GameControllerIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpringGameRepository gameRepository;

    @Test
    void findAll()
    {
    }

    @Test
    @DisplayName("should start a new game")
    void startNewGame() throws Exception
    {
//        final RequestBuilder request = MockMvcRequestBuilders.post("/trainer");
//
//        this.mockMvc.perform(request)
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").exists())
//                .andExpect(jsonPath("$.gameStatus").exists())
//                .andExpect(jsonPath("$.score").exists())
//                .andExpect(jsonPath("$.feedbackHistory").exists())
//                .andExpect(jsonPath("$.lastHint").exists())
//                .andExpect(jsonPath("$.numberOfRounds").exists());
    }

    // FIXME: Add fail new game

    @Test
    void getGameProgress()
    {
    }

    @Test
    @DisplayName("should start a new round")
    void startNewRound()
    {
//        final Game game = new Game();
//        game.startNewRound("borax");
//
//        final RequestBuilder request = MockMvcRequestBuilders.post("/trainer/new-round");
//
//        this.mockMvc.perform(request)
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").exists())
//                .andExpect(jsonPath("$.gameStatus").exists())
//                .andExpect(jsonPath("$.score").exists())
//                .andExpect(jsonPath("$.feedbackHistory").exists())
//                .andExpect(jsonPath("$.lastHint").exists())
//                .andExpect(jsonPath("$.numberOfRounds").exists());
    }

    @Test
    void guess()
    {
    }
}
