package com.ftn.sbnz.sv10.model.models;

public enum AlertType {
    PACKET_LOSS_WARNING,
    PACKET_LOSS_HIGH,
    PACKET_LOSS_CRITICAL,
    
    LATENCY_WARNING,
    LATENCY_HIGH,
    LATENCY_CRITICAL,
    
    DHCP_SLOW,
    DHCP_FAILED,
    DNS_SLOW
}
