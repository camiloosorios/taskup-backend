package com.uptask.api.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "tokens")
public class Token {

    private String id;

    @NotBlank
    private String token;

    @NotBlank
    private String user;

    @Indexed(expireAfterSeconds = 60)
    private LocalDateTime createdAt;

}
