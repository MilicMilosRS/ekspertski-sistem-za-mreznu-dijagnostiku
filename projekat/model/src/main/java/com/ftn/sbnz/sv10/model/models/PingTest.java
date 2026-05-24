package com.ftn.sbnz.sv10.model.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PingTest {
    private String target;
    private int attempts;
    private int successful;
    public double getLossRate(){return 1.0 - (double) successful / attempts;};
}
