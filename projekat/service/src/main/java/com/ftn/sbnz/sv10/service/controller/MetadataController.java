package com.ftn.sbnz.sv10.service.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ftn.sbnz.sv10.model.models.AlertType;
import com.ftn.sbnz.sv10.model.models.ConnectionType;
import com.ftn.sbnz.sv10.model.models.DeviceType;
import com.ftn.sbnz.sv10.model.models.Symptom;

/**
 * Pomocni endpoint - vraca sve enum vrednosti tako da frontend moze
 * dinamicki da popuni dropdown menije (simptomi, tipovi uredjaja itd.)
 * bez hardkodovanja na klijentu.
 */
@RestController
@RequestMapping("/api/metadata")
@CrossOrigin(origins = "*")
public class MetadataController {

    @GetMapping("/enums")
    public ResponseEntity<Map<String, Object>> enums() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("symptoms", Symptom.values());
        result.put("deviceTypes", DeviceType.values());
        result.put("connectionTypes", ConnectionType.values());
        result.put("alertTypes", AlertType.values());
        return ResponseEntity.ok(result);
    }
}
