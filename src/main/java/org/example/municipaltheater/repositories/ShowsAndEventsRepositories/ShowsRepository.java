package org.example.municipaltheater.repositories.ShowsAndEventsRepositories;

import org.example.municipaltheater.models.ShowModels.Show;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Repository
public interface ShowsRepository extends MongoRepository<Show, String> {
    boolean existsByShowNameAndShowDate(@NotBlank(message = "Show name is required.") String showName, @NotNull(message = "Show date is required.") Date showDate);
}
