package org.example.municipaltheater.repositories.TokensRepositories;

import org.example.municipaltheater.models.Authentication.response.BlackListedToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListedTokensRepository extends MongoRepository<BlackListedToken, String> {
    boolean existsByToken(String token);
}
