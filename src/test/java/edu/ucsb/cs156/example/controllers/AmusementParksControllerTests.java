package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.AmusementPark;
import edu.ucsb.cs156.example.repositories.AmusementParkRepository;

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

@WebMvcTest(controllers = AmusementParksController.class)
@Import(TestConfig.class)
public class AmusementParksControllerTests extends ControllerTestCase{
        @MockBean
        AmusementParkRepository amusementParksRepository;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/amusementparks/admin/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/amusementparks/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/amusementparks/all"))
                                .andExpect(status().is(200)); // logged
        }

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/amusementparks?id=1"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        // Authorization tests for /api/amusementparks/post
        // (Perhaps should also have these for put and delete)

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/amusementparks/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/amusementparks/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        // Tests with mocks for database actions

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange

                AmusementPark amusementParks = AmusementPark.builder()
                                .name("Disney")
                                .address("1313 Disneyland Dr, Anaheim, CA 92802")
                                .description("Disney land park in LA is a theme park built for fans")
                                .build();

                when(amusementParksRepository.findById(eq(1L))).thenReturn(Optional.of(amusementParks));

                // act
                MvcResult response = mockMvc.perform(get("/api/amusementparks?id=1"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(amusementParksRepository, times(1)).findById(eq(1L));
                String expectedJson = mapper.writeValueAsString(amusementParks);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(amusementParksRepository.findById(eq(2L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/amusementparks?id=2"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(amusementParksRepository, times(1)).findById(eq(2L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("AmusementPark with id 2 not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_amusementParks() throws Exception {

                // arrange

                AmusementPark UniversalStudio = AmusementPark.builder()
                                .name("UniversalStudio")
                                .address("100 Universal City Plaza, Universal City, CA 91608")
                                .description("A Universal Studio theme park built for Universal Studio film fans")
                                .build();

                AmusementPark LegoLand = AmusementPark.builder()
                                .name("LegoLand")
                                .address("One Legoland Dr, Carlsbad, CA 92008")
                                .description("A lego theme park built for lego fans")
                                .build();

                ArrayList<AmusementPark> expectedAmusementParks = new ArrayList<>();
                expectedAmusementParks.addAll(Arrays.asList(UniversalStudio, LegoLand));

                when(amusementParksRepository.findAll()).thenReturn(expectedAmusementParks);

                // act
                MvcResult response = mockMvc.perform(get("/api/amusementparks/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(amusementParksRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedAmusementParks);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_amusementParks() throws Exception {
                // arrange

                AmusementPark LegoLand = AmusementPark.builder()
                                .name("LegoLand")
                                .address("One Legoland Dr, Carlsbad, CA 92008")
                                .description("A lego theme park built for lego fans")
                                .build();

                when(amusementParksRepository.save(eq(LegoLand))).thenReturn(LegoLand);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/amusementparks/post?name=LegoLand&address=One Legoland Dr, Carlsbad, CA 92008&description=A lego theme park built for lego fans")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(amusementParksRepository, times(1)).save(LegoLand);
                String expectedJson = mapper.writeValueAsString(LegoLand);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_date() throws Exception {
                // arrange

                AmusementPark UniversalStudio = AmusementPark.builder()
                                .name("UniversalStudio")
                                .address("100 Universal City Plaza, Universal City, CA 91608")
                                .description("A Universal Studio theme park built for Universal Studio film fans")
                                .build();

                when(amusementParksRepository.findById(eq(2L))).thenReturn(Optional.of(UniversalStudio));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/amusementparks?id=2")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(amusementParksRepository, times(1)).findById(2L);
                verify(amusementParksRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("AmusementPark with id 2 deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_amusementParks_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(amusementParksRepository.findById(eq(2L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/amusementparks?id=2")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(amusementParksRepository, times(1)).findById(2L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("AmusementPark with id 2 not found", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_amusementParks() throws Exception {
                // arrange

                AmusementPark UniversalStudioOrig = AmusementPark.builder()
                                .name("Universal")
                                .address("100 Universal City Plaza, CA 91608")
                                .description("A Universal Studio theme park built for Universal Studio film fans")
                                .build();

                AmusementPark UniversalStudioEdited = AmusementPark.builder()
                                .name("UniversalStudio")
                                .address("100 Universal City Plaza, Universal City, CA 91608")
                                .description("A Universal Studio theme park built for Universal Studio film fans")
                                .build();

                String requestBody = mapper.writeValueAsString(UniversalStudioEdited);

                when(amusementParksRepository.findById(eq(5L))).thenReturn(Optional.of(UniversalStudioOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/amusementparks?id=5")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(amusementParksRepository, times(1)).findById(5L);
                verify(amusementParksRepository, times(1)).save(UniversalStudioEdited); // should be saved with updated info
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_amusementParks_that_does_not_exist() throws Exception {
                // arrange

                AmusementPark editedAmusementParks = AmusementPark.builder()
                                .name("UniversalStudio")
                                .address("100 Universal City Plaza, Universal City, CA 91608")
                                .description("A Universal Studio theme park built for Universal Studio film fans")
                                .build();

                String requestBody = mapper.writeValueAsString(editedAmusementParks);

                when(amusementParksRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/amusementparks?id=7")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(amusementParksRepository, times(1)).findById(7L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("AmusementPark with id 7 not found", json.get("message"));

        }

}
