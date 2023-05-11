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
        public void admin_can_edit_an_existing_game() throws Exception {
                // arrange

                Game gameOrig = Game.builder()
                                .name("Bucket Montage")
                                .creator("Plum")
                                .genre("RPG")
                                .build();

                Game gameEdited = Game.builder()
                                .name("Montage Bucket")
                                .creator("Suiku")
                                .genre("ARPG")
                                .build();

                String requestBody = mapper.writeValueAsString(gameEdited);

                when(gameRepository.findById(eq(67L))).thenReturn(Optional.of(gameOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/games?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(gameRepository, times(1)).findById(67L);
                verify(gameRepository, times(1)).save(gameEdited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_game_that_does_not_exist() throws Exception {
                // arrange

                Game editedGame = Game.builder()
                                .name("Bucket Montage")
                                .creator("Plum")
                                .genre("RPG")
                                .build();

                String requestBody = mapper.writeValueAsString(editedGame);

                when(gameRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/games?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(gameRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("Game with id 67 not found", json.get("message"));

        }
}
