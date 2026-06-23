package com.ftn.sbnz.sv10.model.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NetworkMeasurement {
    private String parameter;   // "packet_loss" | "latency" | "dhcp_response_time" | "dns_response_time"
    private double value;

    @Override
    public String toString() {
        return "[NetworkMeasurement] " + parameter + " = " + value;
    }
}
