package com.ftn.sbnz.sv10.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ftn.sbnz.sv10.model.models.AlertType;
import com.ftn.sbnz.sv10.model.models.ConnectionType;
import com.ftn.sbnz.sv10.model.models.DeviceType;
import com.ftn.sbnz.sv10.model.models.NetworkAlert;
import com.ftn.sbnz.sv10.model.models.NetworkProblem;
import com.ftn.sbnz.sv10.model.models.NetworkTest;
import com.ftn.sbnz.sv10.model.models.AlertType;
import com.ftn.sbnz.sv10.model.models.PingTest;
import com.ftn.sbnz.sv10.model.models.Symptom;

@Component
public class DiagnosticsDemo implements CommandLineRunner{
    @Autowired
    private DiagnosticsService diagnosticsService;
 
    @Override
    public void run(String... args) {
        scenarioNoInternet();
        System.out.println();
        scenarioWifiNoConnection();
    }
 
    private void scenarioNoInternet() {
        System.out.println(" SCENARIO 1: laptop NO_INTERNET / WiFi");
 
        NetworkProblem problem = new NetworkProblem(
            Symptom.NO_INTERNET, ConnectionType.WIFI, DeviceType.LAPTOP, ""
        );
 
        List<NetworkTest> tests = Arrays.asList(
            new NetworkTest("local_network_works", true, System.currentTimeMillis()),
            new NetworkTest("gateway_ping", true, System.currentTimeMillis()),
            new NetworkTest("dns_server_ping", false, System.currentTimeMillis())
        );
 
        List<NetworkAlert> alerts = Arrays.asList(
            new NetworkAlert(AlertType.DNS_SLOW, "DNS odgovara > 500ms", System.currentTimeMillis()),
            new NetworkAlert(AlertType.PACKET_LOSS_HIGH, "Packet loss preko 15%", System.currentTimeMillis()),
            new NetworkAlert(AlertType.LATENCY_HIGH, "Latencija preko 100ms", System.currentTimeMillis())
        );
 
        // prosek loss-a: 23% (Preko praga od 15%)
        List<PingTest> pings = Arrays.asList(
            new PingTest("gateway", 10, 10),    //   0% loss
            new PingTest("8.8.8.8", 10,  6),    //  40% loss
            new PingTest("1.1.1.1", 10,  7)     //  30% loss
        );
 
        var report = diagnosticsService.diagnose(problem, tests, alerts, pings);
        printReport(report);
    }
 
    private void scenarioWifiNoConnection() {
        System.out.println(" SCENARIO 2: laptop NO_CONNECTION / WiFi");
 
        NetworkProblem problem = new NetworkProblem(
            Symptom.NO_CONNECTION, ConnectionType.WIFI, DeviceType.LAPTOP, ""
        );
 
        var report = diagnosticsService.diagnose(
            problem,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        );
        printReport(report);
    }
 
    private void printReport(DiagnosticsService.DiagnosisReport report) {
        System.out.println();
        System.out.println("##### IZVESTAJ #####");
        System.out.println("Hipoteze (" + report.hypotheses.size() + "):");
        report.hypotheses.forEach(h -> System.out.println("  - " + h));
        System.out.println("Resenja (" + report.solutions.size() + "):");
        report.solutions.forEach(s -> System.out.println("  - " + s));
        System.out.println("Rezolucije (" + report.resolutions.size() + "):");
        report.resolutions.forEach(r -> System.out.println("  - " + r));
    }
}
