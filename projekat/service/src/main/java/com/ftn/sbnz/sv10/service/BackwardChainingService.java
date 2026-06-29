package com.ftn.sbnz.sv10.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.sv10.model.models.ServiceDependsOn;
import com.ftn.sbnz.sv10.model.models.ServiceWorks;

/**
 * Backward chaining preko isAvailable query-ja. Pita "da li je cilj dostupan",
 * i ako nije - identifikuje koji konkretno listovi stabla zavisnosti nedostaju.
 */
@Service
public class BackwardChainingService {

    @Autowired
    private KieBase kieBase;

    public AvailabilityResult checkAvailability(String target,
                                                List<ServiceWorks> works,
                                                List<ServiceDependsOn> dependencies) {
        KieSession ks = kieBase.newKieSession();
        try {
            works.forEach(ks::insert);
            dependencies.forEach(ks::insert);

            // glavni upit: da li je cilj dostupan
            QueryResults results = ks.getQueryResults("isAvailable", target);
            boolean available = results.size() > 0;

            // dodatno: nadji koji su tacno listovi slomljeni (za objasnjenje)
            Set<String> working = new HashSet<>();
            works.forEach(w -> working.add(w.getService()));

            List<String> broken = new ArrayList<>();
            collectBrokenLeaves(target, working, dependencies, broken, new HashSet<>());

            AvailabilityResult res = new AvailabilityResult(target, available, broken);
            System.out.println("[BC] " + res);
            return res;

        } finally {
            ks.dispose();
        }
    }

    /**
     * Rekurzivno obilazi stablo i sakuplja listove (cvorove bez zavisnosti)
     * koji nemaju ServiceWorks dokaz. To su tacke gde je lanac pukao.
     */
    private void collectBrokenLeaves(String node, Set<String> working,
                                     List<ServiceDependsOn> deps, List<String> broken,
                                     Set<String> visited) {
        if (!visited.add(node)) return;       // zastita od ciklusa

        // ako cvor ima direktan dokaz da radi, ne kopamo dalje
        if (working.contains(node)) return;

        List<String> children = new ArrayList<>();
        for (ServiceDependsOn d : deps) {
            if (d.getService().equals(node)) children.add(d.getDependency());
        }

        if (children.isEmpty()) {
            // list bez ServiceWorks dokaza = slomljena tacka
            broken.add(node);
            return;
        }
        // unutrasnji cvor - proveri svu decu
        for (String c : children) {
            collectBrokenLeaves(c, working, deps, broken, visited);
        }
    }

    public static class AvailabilityResult {
        public final String target;
        public final boolean available;
        public final List<String> brokenLeaves;

        public AvailabilityResult(String target, boolean available, List<String> brokenLeaves) {
            this.target = target;
            this.available = available;
            this.brokenLeaves = brokenLeaves;
        }

        @Override
        public String toString() {
            if (available) return "'" + target + "' DOSTUPAN";
            return "'" + target + "' NIJE DOSTUPAN, problem u: " + brokenLeaves;
        }
    }
}
