package org.example.municipaltheater.repositories.ShowsAndEventsRepositories;

import org.example.municipaltheater.models.ShowModels.Show;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowsRepository extends MongoRepository<Show, String> {
    Optional<Show> findByShowID(@NotBlank String id);
    List<Show> findByShowNameContainingIgnoreCase(String showName);
    Show findByShowPhotoURL(String showPhotoURL);
    boolean existsByShowName(String showName);
}
