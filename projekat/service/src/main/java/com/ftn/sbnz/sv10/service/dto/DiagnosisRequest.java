package com.ftn.sbnz.sv10.service.dto;

import java.util.ArrayList;
import java.util.List;

import com.ftn.sbnz.sv10.model.models.NetworkMeasurement;
import com.ftn.sbnz.sv10.model.models.NetworkProblem;
import com.ftn.sbnz.sv10.model.models.NetworkTest;
import com.ftn.sbnz.sv10.model.models.PingTest;

public class DiagnosisRequest {
    private NetworkProblem problem;
    private List<NetworkTest> tests = new ArrayList<>();
    private List<NetworkMeasurement> measurements = new ArrayList<>();
    private List<PingTest> pings = new ArrayList<>();

    public NetworkProblem getProblem() { return problem; }
    public void setProblem(NetworkProblem p) { this.problem = p; }
    public List<NetworkTest> getTests() { return tests; }
    public void setTests(List<NetworkTest> t) { this.tests = t; }
    public List<NetworkMeasurement> getMeasurements() { return measurements; }
    public void setMeasurements(List<NetworkMeasurement> m) { this.measurements = m; }
    public List<PingTest> getPings() { return pings; }
    public void setPings(List<PingTest> p) { this.pings = p; }
}
