package com.ftn.sbnz.sv10.model.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NetworkProblem {
    private Symptom symptom;
    private ConnectionType connectionType;
    private DeviceType deviceType;
    private String details;
}
