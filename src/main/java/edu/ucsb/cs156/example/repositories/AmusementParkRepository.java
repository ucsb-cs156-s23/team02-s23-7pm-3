package edu.ucsb.cs156.example.repositories;
import edu.ucsb.cs156.example.entities.AmusementPark;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmusementParkRepository extends CrudRepository<AmusementPark, Long> {

}