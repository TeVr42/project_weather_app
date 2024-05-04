package cz.stin.service;

import cz.stin.model.FavLocation;
import cz.stin.repository.ILocationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class FavLocationServiceTest {

    @Mock
    private ILocationRepository locationRepository;

    @InjectMocks
    private FavLocationService favLocationService;

    @Test
    void addFavLocation_ValidLocation_LocationSaved() {
        FavLocation location = new FavLocation();
        location.setUsername("username");
        location.setLocation("Liberec");
        favLocationService.addFavLocation(location);
        verify(locationRepository, times(1)).save(location);
    }

    @Test
    void findLocationsByUsername_ValidUsername_ReturnsLocations() {
        List<FavLocation> expectedLocations = new ArrayList<>();

        FavLocation location1 = new FavLocation();
        location1.setUsername("username");
        location1.setLocation("Liberec");
        FavLocation location2 = new FavLocation();
        location2.setUsername("username");
        location2.setLocation("Praha");

        expectedLocations.add(location1);
        expectedLocations.add(location2);
        when(locationRepository.findByUsername("username")).thenReturn(expectedLocations);

        List<FavLocation> actualLocations = favLocationService.findLocationsByUsername("username");
        assertEquals(expectedLocations, actualLocations);
    }

    @Test
    void findLocationIdByUsernameAndLocation_ExistingLocation_ReturnsId() {
        FavLocation location = new FavLocation();
        location.setUsername("username");
        location.setLocation("Liberec");
        location.setId(1L);
        when(locationRepository.findByUsernameAndLocation("username", "location")).thenReturn(location);
        Long expectedId = 1L;
        Long actualId = favLocationService.findLocationIdByUsernameAndLocation("username", "location");
        assertEquals(expectedId, actualId);
    }

    @Test
    void findLocationIdByUsernameAndLocation_NonExistingLocation_ReturnsNull() {
        when(locationRepository.findByUsernameAndLocation("username", "location")).thenReturn(null);
        Long actualId = favLocationService.findLocationIdByUsernameAndLocation("username", "location");
        assertNull(actualId);
    }

    @Test
    void removeFavLocation_ExistingLocation_LocationRemoved() {
        Long locationId = 1L;
        FavLocation location = new FavLocation();
        location.setUsername("username");
        location.setLocation("Liberec");
        location.setId(locationId);
        when(locationRepository.findByUsernameAndLocation("username", "location")).thenReturn(location);
        favLocationService.removeFavLocation("username", "location");
        verify(locationRepository, times(1)).deleteById(locationId);
    }

    @Test
    void removeFavLocation_NonExistingLocation_NothingRemoved() {
        when(locationRepository.findByUsernameAndLocation("username", "location")).thenReturn(null);
        favLocationService.removeFavLocation("username", "location");
        verify(locationRepository, never()).deleteById(anyLong());
    }
}
