package com.ftn.sbnz.sv10.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ftn.sbnz.sv10.service.DiagnosticsService;
import com.ftn.sbnz.sv10.service.dto.DiagnosisRequest;

@RestController
@RequestMapping("/api/diagnostics")
@CrossOrigin(origins = "*")
public class DiagnosticsController {

    @Autowired
    private DiagnosticsService diagnosticsService;

    @PostMapping
    public ResponseEntity<DiagnosticsService.DiagnosisReport> diagnose(
            @RequestBody DiagnosisRequest request) {

        DiagnosticsService.DiagnosisReport report = diagnosticsService.diagnose(
                request.getProblem(),
                request.getTests(),
                request.getMeasurements(),
                request.getPings());

        return ResponseEntity.ok(report);
    }
}
