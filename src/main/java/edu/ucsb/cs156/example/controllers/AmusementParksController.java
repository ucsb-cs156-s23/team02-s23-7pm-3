package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.AmusementPark;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.AmusementParkRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

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
@Api(description = "AmusementParks")
@RequestMapping("/api/amusementparks")
@RestController
@Slf4j
public class AmusementParksController extends ApiController{
    @Autowired
    AmusementParkRepository amusementParksRepository;

    @ApiOperation(value = "List all amusement parks")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<AmusementPark> allCommonss() {
        Iterable<AmusementPark> commons = amusementParksRepository.findAll();
        return commons;
    }

    @ApiOperation(value = "Get a single commons")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public AmusementPark getById(
            @ApiParam("code") @RequestParam Long id) {
        AmusementPark commons = amusementParksRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AmusementPark.class, id));

        return commons;
    }

    @ApiOperation(value = "Create a new commons")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public AmusementPark postCommons(
        @ApiParam("id") @RequestParam Long id,
        @ApiParam("name") @RequestParam String name,
        @ApiParam("address") @RequestParam String address,
        @ApiParam("description") @RequestParam String description
        )
        {

        AmusementPark commons = new AmusementPark();
        commons.setId(id);
        commons.setName(name);
        commons.setAddress(address);
        commons.setDescription(description);

        AmusementPark savedCommons = amusementParksRepository.save(commons);

        return savedCommons;
    }

    @ApiOperation(value = "Delete a AmusementPark")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteCommons(
            @ApiParam("code") @RequestParam Long id) {
        AmusementPark commons = amusementParksRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AmusementPark.class, id));

        amusementParksRepository.delete(commons);
        return genericMessage("AmusementPark with id %s deleted".formatted(id));
    }

    @ApiOperation(value = "Update a single commons")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public AmusementPark updateCommons(
            @ApiParam("code") @RequestParam Long id,
            @RequestBody @Valid AmusementPark incoming) {

        AmusementPark commons = amusementParksRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AmusementPark.class, id));

        commons.setId(incoming.getId());
        commons.setName(incoming.getName());
        commons.setAddress(incoming.getAddress());
        commons.setDescription(incoming.getDescription());

        amusementParksRepository.save(commons);

        return commons;
    }

}
