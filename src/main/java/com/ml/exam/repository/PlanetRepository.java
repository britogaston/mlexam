package com.ml.exam.repository;

import com.ml.exam.model.Planet;
import org.springframework.data.repository.CrudRepository;

public interface PlanetRepository extends CrudRepository<Planet, String> {
}
