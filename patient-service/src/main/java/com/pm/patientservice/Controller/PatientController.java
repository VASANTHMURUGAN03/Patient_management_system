package com.pm.patientservice.Controller;

import com.pm.patientservice.DTO.PatientRequestDTO;
import com.pm.patientservice.DTO.PatientResponseDTO;
import com.pm.patientservice.Service.Impl.PatientService;
import com.pm.patientservice.Validators.CreatePatientValidationGroup;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }


    @GetMapping("/getAllPatients")
    public ResponseEntity<List<PatientResponseDTO>> getPatients(){
        List<PatientResponseDTO> patientResponseDTOS=patientService.getAllPatient();
        return ResponseEntity.ok().body(patientResponseDTOS);
    }

    @PostMapping("/addPatient")
    public ResponseEntity<PatientResponseDTO> createPatient(@Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patientRequestDTO){
        PatientResponseDTO responseDTO=patientService.createPatient(patientRequestDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/updatePatient/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id, @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO){
        PatientResponseDTO responseDTO=patientService.updatePatient(id,patientRequestDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

}
