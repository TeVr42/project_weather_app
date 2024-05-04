package cz.stin.service;

import cz.stin.model.FavLocation;
import cz.stin.repository.ILocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public class FavLocationService {
    @Autowired
    private ILocationRepository locationRepository;

    public void addFavLocation(FavLocation location) {
        locationRepository.save(location);
    }

    public List<FavLocation> findLocationsByUsername(String username) {
        return locationRepository.findByUsername(username);
    }
}

