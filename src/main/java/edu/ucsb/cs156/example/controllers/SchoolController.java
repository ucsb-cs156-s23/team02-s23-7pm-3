package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.School;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.SchoolRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDateTime;

@Api(description = "School")
@RequestMapping("/api/schools")
@RestController
@Slf4j
public class SchoolController extends ApiController {

    @Autowired
    SchoolRepository schoolRepository;

    @ApiOperation(value = "List all schools")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<School> allSchools() {
        Iterable<School> schools = schoolRepository.findAll();
        return schools;
    }

    @ApiOperation(value = "Get a single school")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public School getById(
            @ApiParam("id") @RequestParam Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(School.class, id));

        return school;
    }

    @ApiOperation(value = "Create a new school")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public School postSchool(
            @ApiParam("name") @RequestParam String name,
            @ApiParam("address") @RequestParam String address,
            @ApiParam("description") @RequestParam String description)
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        //log.info("localDateTime={}", localDateTime);

        School school = new School();
        school.setName(name);
        school.setAddress(address);
        school.setDescription(description);

        School savedSchool = schoolRepository.save(school);

        return savedSchool;
    }

    @ApiOperation(value = "Delete a School")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteSchool(
            @ApiParam("id") @RequestParam Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(School.class, id));

        schoolRepository.delete(school);
        return genericMessage("School with id %s deleted".formatted(id));
    }

    @ApiOperation(value = "Update a single school")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public School updateSchool(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid School incoming) {

        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(School.class, id));

        school.setName(incoming.getName());
        school.setAddress(incoming.getAddress());
        school.setDescription(incoming.getDescription());

        schoolRepository.save(school);

        return school;
    }
}
