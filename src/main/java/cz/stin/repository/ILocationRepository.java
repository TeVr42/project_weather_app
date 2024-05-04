package cz.stin.repository;

import cz.stin.model.FavLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILocationRepository extends JpaRepository<FavLocation, Long> {
    List<FavLocation> findByUsername(String username);
}