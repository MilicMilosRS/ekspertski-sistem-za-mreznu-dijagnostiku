package com.ftn.sbnz.sv10.model.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDependsOn {
    private String service;
    private String dependency;

    @Override
    public String toString() {
        return "[ServiceDependsOn] " + service + " -> " + dependency;
    }
}
