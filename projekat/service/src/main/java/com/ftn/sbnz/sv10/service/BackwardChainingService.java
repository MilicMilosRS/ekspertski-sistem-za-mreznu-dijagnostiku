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
 * Backward chaining preko isAvailable query-ja.
 *
 * Topologija (ServiceDependsOn) NIJE ulaz - ona je staticko znanje koje
 * topology.drl ubacuje pri startu sesije. Korisnik salje samo cilj i koje
 * cinjenice (ServiceWorks) rade.
 */
@Service
public class BackwardChainingService {

    @Autowired
    private KieBase kieBase;

    public AvailabilityResult checkAvailability(String target, List<ServiceWorks> works) {
        KieSession ks = kieBase.newKieSession();
        try {
            // dinamicki dokazi koje korisnik salje
            works.forEach(ks::insert);

            // okini topology.drl da ubaci staticko znanje o zavisnostima
            ks.fireAllRules();

            // glavni upit: da li je cilj dostupan
            QueryResults results = ks.getQueryResults("isAvailable", target);
            boolean available = results.size() > 0;

            // procitaj topologiju iz sesije (ubacio je topology.drl) da nadjemo
            // koji su tacno listovi slomljeni - samo za prikaz na frontu
            List<ServiceDependsOn> deps = new ArrayList<>();
            ks.getObjects().forEach(o -> {
                if (o instanceof ServiceDependsOn) deps.add((ServiceDependsOn) o);
            });

            Set<String> working = new HashSet<>();
            works.forEach(w -> working.add(w.getService()));

            List<String> broken = new ArrayList<>();
            collectBrokenLeaves(target, working, deps, broken, new HashSet<>());

            AvailabilityResult res = new AvailabilityResult(target, available, broken);
            System.out.println("[BC] " + res);
            return res;

        } finally {
            ks.dispose();
        }
    }

    private void collectBrokenLeaves(String node, Set<String> working,
                                     List<ServiceDependsOn> deps, List<String> broken,
                                     Set<String> visited) {
        if (!visited.add(node)) return;
        if (working.contains(node)) return;

        List<String> children = new ArrayList<>();
        for (ServiceDependsOn d : deps) {
            if (d.getService().equals(node)) children.add(d.getDependency());
        }

        if (children.isEmpty()) {
            broken.add(node);
            return;
        }
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
