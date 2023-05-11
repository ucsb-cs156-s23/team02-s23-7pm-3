package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.Game;
import edu.ucsb.cs156.example.repositories.GameRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = GamesController.class)
@Import(TestConfig.class)
public class GamesControllerTests extends ControllerTestCase {

        @MockBean
        GameRepository gameRepository;

        @MockBean
        UserRepository userRepository;

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_game() throws Exception {
                // arrange

                Game game1 = Game.builder()
                                .name("Bucket Montage")
                                .creator("Plum")
                                .genre("RPG")
                                .build();

                when(gameRepository.findById(eq(15L))).thenReturn(Optional.of(game1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/games?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(gameRepository, times(1)).findById(15L);
                verify(gameRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("Game with id 15 deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_game_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(gameRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/games?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(gameRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("Game with id 15 not found", json.get("message"));
        }
}
