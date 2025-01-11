package org.example.municipaltheater.repositories.ShowsAndEventsRepositories;

import org.example.municipaltheater.models.EventModels.Event;
import org.example.municipaltheater.models.ShowModels.Show;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowsRepository extends MongoRepository<Show, String> {
    boolean existsByShowNameAndShowDateAndShowStartTime(@NotBlank(message = "Show name is required.") String showName, @NotNull(message = "Show date is required.") Date showDate, @NotNull(message = "Show starting time is required.") LocalTime showStartTime);
    boolean existsByShowPhotoURL(String showPhotoURL);
    Optional<Show> findByShowPhotoURL(String showPhotoURL);
    List<Show> findByShowNameContainingIgnoreCase(String showName);

}
