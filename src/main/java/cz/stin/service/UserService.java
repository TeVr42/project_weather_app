package cz.stin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import cz.stin.repository.IUserRepository;
import cz.stin.model.AppUser;

@Component
@Service
public class UserService {
    @Autowired
    private IUserRepository userRepository;

    public void addUser(AppUser appUser) {
        userRepository.save(appUser);
    }

    public AppUser findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

