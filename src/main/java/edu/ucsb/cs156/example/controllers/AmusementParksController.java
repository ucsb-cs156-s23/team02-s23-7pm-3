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
    public Iterable<AmusementPark> allAmusementParkss() {
        Iterable<AmusementPark> amusementParks = amusementParksRepository.findAll();
        return amusementParks;
    }

    @ApiOperation(value = "Get a single amusementParks")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public AmusementPark getById(
            @ApiParam("id") @RequestParam Long id) {
        AmusementPark amusementParks = amusementParksRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AmusementPark.class, id));

        return amusementParks;
    }

    @ApiOperation(value = "Create a new amusementParks")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public AmusementPark postAmusementParks(
        @ApiParam("name") @RequestParam String name,
        @ApiParam("address") @RequestParam String address,
        @ApiParam("description") @RequestParam String description
        )
        {

        AmusementPark amusementParks = new AmusementPark();
        amusementParks.setName(name);
        amusementParks.setAddress(address);
        amusementParks.setDescription(description);

        AmusementPark savedAmusementParks = amusementParksRepository.save(amusementParks);

        return savedAmusementParks;
    }

    @ApiOperation(value = "Delete a AmusementPark")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteAmusementParks(
            @ApiParam("id") @RequestParam Long id) {
        AmusementPark amusementParks = amusementParksRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AmusementPark.class, id));

        amusementParksRepository.delete(amusementParks);
        return genericMessage("AmusementPark with id %s deleted".formatted(id));
    }

    @ApiOperation(value = "Update a single amusementParks")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public AmusementPark updateAmusementParks(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid AmusementPark incoming) {

        AmusementPark amusementParks = amusementParksRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AmusementPark.class, id));

        amusementParks.setName(incoming.getName());
        amusementParks.setAddress(incoming.getAddress());
        amusementParks.setDescription(incoming.getDescription());

        amusementParksRepository.save(amusementParks);

        return amusementParks;
    }

}
