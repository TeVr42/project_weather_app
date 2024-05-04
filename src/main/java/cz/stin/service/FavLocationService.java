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

    public Long findLocationIdByUsernameAndLocation(String username, String location) {
        FavLocation favLocation = locationRepository.findByUsernameAndLocation(username, location);
        if (favLocation != null) {
            return favLocation.getId();
        }
        return null;
    }

    public void removeFavLocation(String username, String location) {
        Long locationId = findLocationIdByUsernameAndLocation(username, location);
        if (locationId != null) {
            locationRepository.deleteById(locationId);
        }
    }
}

