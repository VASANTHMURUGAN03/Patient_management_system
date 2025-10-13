package com.pm.patientservice.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatientRequestDTO {

    @NotNull
    @Size(max = 100)
    private String name;
    @Email
    private String email;
    @NotNull
    private String address;
    @NotNull
    private String dateOfBirth;
    @NotNull
    private String registeredDate;
}
