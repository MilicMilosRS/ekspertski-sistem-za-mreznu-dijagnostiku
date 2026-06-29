package com.ftn.sbnz.sv10.service.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
        result.put("tests", availableTests());
        result.put("measurements", availableMeasurements());
        return ResponseEntity.ok(result);
    }

    // Svi NetworkTest name-ovi koje pravila prepoznaju, sa ljudskim opisom.
    private List<Map<String, String>> availableTests() {
        List<Map<String, String>> list = new ArrayList<>();
        list.add(test("local_network_works", "Lokalna mreza radi"));
        list.add(test("local_loop_test", "Local loopback test (ping 127.0.0.1)"));
        list.add(test("gateway_ping", "Ping do default gateway-a"));
        list.add(test("dns_server_ping", "Ping do DNS servera"));
        list.add(test("adapter_visible", "Mrezni adapter vidljiv u sistemu"));
        list.add(test("device_manager_error", "Greska adaptera u Device Manageru"));
        list.add(test("specific_port_unreachable", "Odredjeni port nedostupan"));
        list.add(test("other_ports_work", "Ostali portovi rade"));
        list.add(test("other_services_unreachable", "Ostali servisi nedostupni"));
        list.add(test("wifi_signal_strength_low", "Slab WiFi signal"));
        return list;
    }

    // Svi parametri merenja koje template prepoznaje, sa opisom i jedinicom.
    private List<Map<String, String>> availableMeasurements() {
        List<Map<String, String>> list = new ArrayList<>();
        list.add(meas("packet_loss", "Packet loss", "%"));
        list.add(meas("latency", "Latencija", "ms"));
        list.add(meas("dhcp_response_time", "DHCP vreme odziva", "ms"));
        list.add(meas("dns_response_time", "DNS vreme odziva", "ms"));
        list.add(meas("bandwidth_usage", "Iskoriscenost propusnog opsega", "%"));
        list.add(meas("failed_logins", "Broj neuspesnih login pokusaja", "broj"));
        list.add(meas("port_scan_attempts", "Broj port scan pokusaja", "broj"));
        return list;
    }

    private Map<String, String> test(String name, String label) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("name", name);
        m.put("label", label);
        return m;
    }

    private Map<String, String> meas(String parameter, String label, String unit) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("parameter", parameter);
        m.put("label", label);
        m.put("unit", unit);
        return m;
    }
}
