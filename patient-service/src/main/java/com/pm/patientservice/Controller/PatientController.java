package com.pm.patientservice.Controller;

import com.pm.patientservice.DTO.PatientRequestDTO;
import com.pm.patientservice.DTO.PatientResponseDTO;
import com.pm.patientservice.Service.Impl.PatientService;
import com.pm.patientservice.Validators.CreatePatientValidationGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient",description = "API for managing Patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }


    @GetMapping("/getAllPatients")
    @Operation(summary = "Get a Patients")
    public ResponseEntity<List<PatientResponseDTO>> getPatients(){
        List<PatientResponseDTO> patientResponseDTOS=patientService.getAllPatient();
        return ResponseEntity.ok().body(patientResponseDTOS);
    }

    @PostMapping("/addPatient")
    @Operation(summary = "Add a Patients")
    public ResponseEntity<PatientResponseDTO> createPatient(@Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patientRequestDTO){
        PatientResponseDTO responseDTO=patientService.createPatient(patientRequestDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/updatePatient/{id}")
    @Operation(summary = "Update a Patients")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id, @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO){
        PatientResponseDTO responseDTO=patientService.updatePatient(id,patientRequestDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/deletePatient/{id}")
    @Operation(summary = "Delete a Patients")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id){
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

}
