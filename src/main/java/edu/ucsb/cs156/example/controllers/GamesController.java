package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.Game;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.GameRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(description = "Games")
@RequestMapping("/api/games")
@RestController
@Slf4j
public class GamesController extends ApiController {

    @Autowired
    GameRepository gameRepository;


    @ApiOperation(value = "Get a single game")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public Game getById(
            @ApiParam("id") @RequestParam Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Game.class, id));

        return game;
    }
  
    @ApiOperation(value = "List all games")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<Game> allGames() {
        Iterable<Game> games = gameRepository.findAll();
        return games;
    }

    @ApiOperation(value = "Create a new game")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public Game postGame(
            @ApiParam("name") @RequestParam String name,
            @ApiParam("creator") @RequestParam String creator,
            @ApiParam("genre") @RequestParam String genre)
            throws JsonProcessingException {

        Game game = new Game();
        game.setName(name);
        game.setCreator(creator);
        game.setGenre(genre);

        Game savedGame = gameRepository.save(game);

        return savedGame;
    }
}
