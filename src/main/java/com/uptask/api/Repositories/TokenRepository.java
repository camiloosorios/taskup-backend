package com.uptask.api.Repositories;

import com.uptask.api.models.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends MongoRepository<Token, String> {

    @Query("{'token': ?0}")
    Token findByToken(String token);
}
