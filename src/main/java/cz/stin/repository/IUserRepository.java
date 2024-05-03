package cz.stin.repository;

import cz.stin.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}