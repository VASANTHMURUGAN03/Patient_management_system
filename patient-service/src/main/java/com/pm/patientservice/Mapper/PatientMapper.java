package com.pm.patientservice.Mapper;

import com.pm.patientservice.DTO.PatientRequestDTO;
import com.pm.patientservice.DTO.PatientResponseDTO;
import com.pm.patientservice.Entity.Patient;

import java.time.LocalDate;

public class PatientMapper {

    public  static PatientResponseDTO toDTO(Patient patient){
        PatientResponseDTO responseDTO=new PatientResponseDTO();
        responseDTO.setId(patient.getId().toString());
        responseDTO.setAddress(patient.getAddress());
        responseDTO.setEmail(patient.getEmail());
        responseDTO.setName(patient.getName());
        responseDTO.setDateOfBirth(patient.getDateOfBirth().toString());
        return responseDTO;
    }

    public static Patient toModel(PatientRequestDTO patientRequestDTO){
        Patient patient=new Patient();
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate()));
        return patient;
    }
}
