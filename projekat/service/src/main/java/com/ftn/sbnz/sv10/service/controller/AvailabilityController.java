package com.ftn.sbnz.sv10.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ftn.sbnz.sv10.service.BackwardChainingService;
import com.ftn.sbnz.sv10.service.dto.AvailabilityRequest;

@RestController
@RequestMapping("/api/availability")
@CrossOrigin(origins = "*")
public class AvailabilityController {

    @Autowired
    private BackwardChainingService backwardChainingService;

    /**
     * Backward chaining provera dostupnosti. Prima cilj, listu radnih servisa
     * (ServiceWorks) i stablo zavisnosti (ServiceDependsOn). Vraca da li je
     * cilj dostupan i koji listovi su slomljeni ako nije.
     */
    @PostMapping
    public ResponseEntity<BackwardChainingService.AvailabilityResult> check(
            @RequestBody AvailabilityRequest request) {

        BackwardChainingService.AvailabilityResult result =
                backwardChainingService.checkAvailability(
                        request.getTarget(),
                        request.getWorks(),
                        request.getDependencies());

        return ResponseEntity.ok(result);
    }
}
