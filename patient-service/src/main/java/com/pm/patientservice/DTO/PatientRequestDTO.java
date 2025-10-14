package com.pm.patientservice.DTO;

import com.pm.patientservice.Validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatientRequestDTO {

    @NotNull
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Address is required")
    private String address;
    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth;
    @NotNull(groups = CreatePatientValidationGroup.class,message = "Registered date is required")
    private String registeredDate;
}
