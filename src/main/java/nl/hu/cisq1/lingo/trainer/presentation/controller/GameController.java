package nl.hu.cisq1.lingo.trainer.presentation.controller;

import nl.hu.cisq1.lingo.trainer.application.GameService;
import nl.hu.cisq1.lingo.trainer.domain.exception.AttemptLimitReachedException;
import nl.hu.cisq1.lingo.trainer.domain.exception.GameNotFoundException;
import nl.hu.cisq1.lingo.trainer.domain.exception.GameStateException;
import nl.hu.cisq1.lingo.trainer.domain.exception.WordNotExistsException;
import nl.hu.cisq1.lingo.trainer.presentation.dto.ProgressDTO;
import nl.hu.cisq1.lingo.words.domain.exception.WordLengthNotSupportedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/trainer")
public class GameController
{
    private final GameService service;

    public GameController(final GameService service)
    {
        this.service = service;
    }

    @GetMapping
    public List<ProgressDTO> findAll()
    {
        return this.service.findAll();
    }

    @PostMapping
    public ProgressDTO newGame()
    {
        try {
            return this.service.newGame();
        } catch (final GameStateException | WordLengthNotSupportedException e) {
            // idk what http status to apply....
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{gameId}")
    public ProgressDTO getGameProgress(@PathVariable("gameId") final Long gameId)
    {
        try {
            return this.service.getProgress(gameId);
        } catch (final GameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{gameId}/new-round")
    public ProgressDTO newRound(@PathVariable("gameId") final Long gameId)
    {
        try {
            return this.service.startNewRound(gameId);
        } catch (final GameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (final WordLengthNotSupportedException | GameStateException e) {
            // idk what http status to apply....
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{gameId}/guess")
    public ProgressDTO guess(@PathVariable("gameId") final Long gameId, @RequestParam final String attempt)
    {
        try {
            return this.service.guess(gameId, attempt);
        } catch (final GameNotFoundException | WordNotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (final AttemptLimitReachedException | GameStateException e) {
            // idk what http status to apply....
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
