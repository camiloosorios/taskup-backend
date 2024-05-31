package com.uptask.api.models;

import com.uptask.api.DTOs.UserDTO;
import com.uptask.api.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CompletedBy {

    private UserDTO user;

    private String status;

}
