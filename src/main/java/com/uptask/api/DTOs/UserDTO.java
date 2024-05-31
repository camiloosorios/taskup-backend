package com.uptask.api.DTOs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDTO {

    private String id;

    private String name;

    private String email;

    private String password;

    private Boolean confirmed;
}
