package com.ftn.sbnz.sv10.model.models;

public enum AlertType {
    // packet loss
    PACKET_LOSS_WARNING,
    PACKET_LOSS_HIGH,
    PACKET_LOSS_CRITICAL,

    // latencija
    LATENCY_WARNING,
    LATENCY_HIGH,
    LATENCY_CRITICAL,

    // DHCP / DNS
    DHCP_SLOW,
    DHCP_FAILED,
    DNS_SLOW,

    // propusni opseg
    BANDWIDTH_WARNING,
    BANDWIDTH_SATURATED,

    // bezbednosne pretnje
    FAILED_LOGINS_WARNING,
    FAILED_LOGINS_CRITICAL,
    PORT_SCAN_DETECTED
}
