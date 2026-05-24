package com.ftn.sbnz.sv10.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.sv10.model.models.Hypothesis;
import com.ftn.sbnz.sv10.model.models.NetworkAlert;
import com.ftn.sbnz.sv10.model.models.NetworkProblem;
import com.ftn.sbnz.sv10.model.models.NetworkTest;
import com.ftn.sbnz.sv10.model.models.PingTest;
import com.ftn.sbnz.sv10.model.models.Resolution;
import com.ftn.sbnz.sv10.model.models.Solution;

@Service
public class DiagnosticsService {
    @Autowired
    private KieContainer kieContainer;
 
    public DiagnosisReport diagnose(NetworkProblem problem,
                                    List<NetworkTest> tests,
                                    List<NetworkAlert> alerts,
                                    List<PingTest> pings) {
 
        KieSession ks = kieContainer.newKieSession();
        try {
            ks.insert(problem);
            tests.forEach(ks::insert);
            alerts.forEach(ks::insert);
            pings.forEach(ks::insert);
 
            int fired = ks.fireAllRules();
            System.out.println("===== AKTIVIRANO " + fired + " PRAVILA =====");
 
            DiagnosisReport report = new DiagnosisReport();
            ks.getObjects().forEach(o -> {
                if (o instanceof Hypothesis)      report.hypotheses.add((Hypothesis) o);
                else if (o instanceof Solution)   report.solutions.add((Solution) o);
                else if (o instanceof Resolution) report.resolutions.add((Resolution) o);
            });
            report.solutions.sort(Comparator.comparingInt(Solution::getPriority));
            return report;
 
        } finally {
            ks.dispose();
        }
    }
 
    public static class DiagnosisReport {
        public List<Hypothesis> hypotheses = new ArrayList<>();
        public List<Solution>   solutions  = new ArrayList<>();
        public List<Resolution> resolutions = new ArrayList<>();
    }
}
