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

    @ApiOperation(value = "Update a single game")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public Game updateGame(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid Game incoming) {

        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Game.class, id));

        game.setName(incoming.getName());
        game.setCreator(incoming.getCreator());
        game.setGenre(incoming.getGenre());

        gameRepository.save(game);

        return game;
    }
}
