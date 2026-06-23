package com.ftn.sbnz.sv10.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ftn.sbnz.sv10.model.models.ConnectionType;
import com.ftn.sbnz.sv10.model.models.DeviceType;
import com.ftn.sbnz.sv10.model.models.NetworkMeasurement;
import com.ftn.sbnz.sv10.model.models.NetworkProblem;
import com.ftn.sbnz.sv10.model.models.NetworkTest;
import com.ftn.sbnz.sv10.model.models.PingTest;
import com.ftn.sbnz.sv10.model.models.ServiceDependsOn;
import com.ftn.sbnz.sv10.model.models.ServiceWorks;
import com.ftn.sbnz.sv10.model.models.Symptom;

@Component
public class DiagnosticsDemo implements CommandLineRunner {

    @Autowired private DiagnosticsService diagnosticsService;
    @Autowired private BackwardChainingService backwardChainingService;

    @Override
    public void run(String... args) {
        scenarioForwardAndTemplate();
        System.out.println();
        scenarioWifiNoConnection();
        System.out.println();
        scenarioBackwardChainingHealthy();
        System.out.println();
        scenarioBackwardChainingBroken();
    }

    /**
     * SCENARIO 1: forward chaining + template + accumulate.
     * Sirova merenja ulaze, template ih pretvara u alarme, glavna pravila
     * sprovode dijagnozu kroz tri nivoa.
     */
    private void scenarioForwardAndTemplate() {
        System.out.println("==================================================");
        System.out.println(" SCENARIO 1: forward + template (NO_INTERNET)");
        System.out.println("==================================================");

        NetworkProblem problem = new NetworkProblem(
                Symptom.NO_INTERNET, ConnectionType.WIFI, DeviceType.LAPTOP, "");

        List<NetworkTest> tests = Arrays.asList(
                new NetworkTest("local_network_works", true,  System.currentTimeMillis()),
                new NetworkTest("gateway_ping",        true,  System.currentTimeMillis()),
                new NetworkTest("dns_server_ping",     false, System.currentTimeMillis()));

        // sirova merenja -> template generise alarme:
        //  packet_loss=22 -> WARNING(>5) + HIGH(>15)
        //  latency=150    -> WARNING(>50) + HIGH(>100)
        //  dns_response_time=800 -> DNS_SLOW(>500)
        List<NetworkMeasurement> measurements = Arrays.asList(
                new NetworkMeasurement("packet_loss",        22.0),
                new NetworkMeasurement("latency",           150.0),
                new NetworkMeasurement("dns_response_time", 800.0),
                new NetworkMeasurement("dhcp_response_time",1500.0));
        System.out.println("Sirova merenja:");
        measurements.forEach(m -> System.out.println("  - " + m));

        List<PingTest> pings = Arrays.asList(
                new PingTest("gateway", 10, 10),
                new PingTest("8.8.8.8", 10,  6),
                new PingTest("1.1.1.1", 10,  7));

        var report = diagnosticsService.diagnose(problem, tests, measurements, pings);
        printReport(report);
    }

    /** SCENARIO 2: samo L1 hipoteza, bez testova. */
    private void scenarioWifiNoConnection() {
        System.out.println("==================================================");
        System.out.println(" SCENARIO 2: laptop NO_CONNECTION / WiFi (samo L1)");
        System.out.println("==================================================");

        NetworkProblem problem = new NetworkProblem(
                Symptom.NO_CONNECTION, ConnectionType.WIFI, DeviceType.LAPTOP, "");

        var report = diagnosticsService.diagnose(
                problem,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
        printReport(report);
    }

    /** SCENARIO 3: backward chaining - svi listovi rade -> internet dostupan. */
    private void scenarioBackwardChainingHealthy() {
        System.out.println("==================================================");
        System.out.println(" SCENARIO 3: backward chaining - sve radi");
        System.out.println("==================================================");
        backwardChainingService.checkAvailability(
                "internet_access", allLeavesWorking(), dependencyTree());
    }

    /** SCENARIO 4: backward chaining - driver nedostaje -> identifikuj slomljen list. */
    private void scenarioBackwardChainingBroken() {
        System.out.println("==================================================");
        System.out.println(" SCENARIO 4: backward chaining - driver nije instaliran");
        System.out.println("==================================================");
        List<ServiceWorks> works = new ArrayList<>(allLeavesWorking());
        works.removeIf(w -> w.getService().equals("driver_instaliran"));
        backwardChainingService.checkAvailability(
                "internet_access", works, dependencyTree());
    }

    // --- stablo zavisnosti iz predloga projekta ---
    private List<ServiceDependsOn> dependencyTree() {
        return Arrays.asList(
                new ServiceDependsOn("internet_access", "dns_radi"),
                new ServiceDependsOn("internet_access", "gateway_dostupan"),
                new ServiceDependsOn("internet_access", "ip_konfigurisan"),
                new ServiceDependsOn("dns_radi",         "dns_server_dostupan"),
                new ServiceDependsOn("dns_radi",         "dns_cache_validan"),
                new ServiceDependsOn("gateway_dostupan", "ping_gateway_uspesan"),
                new ServiceDependsOn("ip_konfigurisan",  "dhcp_ili_staticki_ip"),
                new ServiceDependsOn("ip_konfigurisan",  "adapter_vidljiv"),
                new ServiceDependsOn("adapter_vidljiv",  "adapter_ukljucen"),
                new ServiceDependsOn("adapter_ukljucen", "driver_instaliran"));
    }

    private List<ServiceWorks> allLeavesWorking() {
        return new ArrayList<>(Arrays.asList(
                new ServiceWorks("dns_server_dostupan"),
                new ServiceWorks("dns_cache_validan"),
                new ServiceWorks("ping_gateway_uspesan"),
                new ServiceWorks("dhcp_ili_staticki_ip"),
                new ServiceWorks("driver_instaliran")));
    }

    private void printReport(DiagnosticsService.DiagnosisReport report) {
        System.out.println();
        System.out.println("##### IZVESTAJ #####");
        System.out.println("Alarmi iz merenja (" + report.alerts.size() + "):");
        report.alerts.forEach(a -> System.out.println("  - " + a.getType() + ": " + a.getMessage()));
        System.out.println("Hipoteze (" + report.hypotheses.size() + "):");
        report.hypotheses.forEach(h -> System.out.println("  - " + h));
        System.out.println("Resenja (" + report.solutions.size() + "):");
        report.solutions.forEach(s -> System.out.println("  - " + s));
        System.out.println("Rezolucije (" + report.resolutions.size() + "):");
        report.resolutions.forEach(r -> System.out.println("  - " + r));
    }
}
