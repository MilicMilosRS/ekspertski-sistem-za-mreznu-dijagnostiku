package com.ftn.sbnz.sv10.service.dto;

import java.util.ArrayList;
import java.util.List;

import com.ftn.sbnz.sv10.model.models.ServiceWorks;

public class AvailabilityRequest {
    private String target;
    private List<ServiceWorks> works = new ArrayList<>();

    public String getTarget() { return target; }
    public void setTarget(String t) { this.target = t; }
    public List<ServiceWorks> getWorks() { return works; }
    public void setWorks(List<ServiceWorks> w) { this.works = w; }
}
