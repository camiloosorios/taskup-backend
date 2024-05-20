package com.uptask.api.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class User {

    @Id
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    @Indexed(unique = true)
    private String email;

    @NotBlank
    @Min(8)
    private String password;

    @NotNull
    private Boolean confirmed;

}
