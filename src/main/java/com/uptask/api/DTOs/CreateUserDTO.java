package com.uptask.api.DTOs;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CreateUserDTO {

    private String name;

    private String email;

    private String password;

    private String passwordConfirmation;
}
