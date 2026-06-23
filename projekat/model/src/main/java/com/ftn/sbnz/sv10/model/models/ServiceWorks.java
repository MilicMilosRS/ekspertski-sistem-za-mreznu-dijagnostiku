package com.ftn.sbnz.sv10.model.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceWorks {
    private String service;

    @Override
    public String toString() {
        return "[ServiceWorks] " + service;
    }
}
