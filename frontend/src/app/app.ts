import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from './api.service';

interface LeafNode { service: string; label: string; group: string; works: boolean; }
interface Dependency { service: string; dependency: string; }
interface BcScenario {
  id: string;
  title: string;
  goal: string;
  leaves: LeafNode[];
  dependencies: Dependency[];
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  tab: 'diagnostics' | 'bc' = 'diagnostics';

  // ---- DIJAGNOSTIKA ----
  symptoms: string[] = [];
  deviceTypes: string[] = [];
  connectionTypes: string[] = [];
  availableTests: { name: string; label: string }[] = [];
  availableMeasurements: { parameter: string; label: string; unit: string }[] = [];
  symptom = '';
  deviceType = '';
  connectionType = '';
  tests: { name: string; success: boolean }[] = [];
  measurements: { parameter: string; value: number }[] = [];
  pings: { target: string; attempts: number; successful: number }[] = [];
  report: any = null;
  loading = false;
  error = '';

  // ---- BACKWARD CHAINING ----
  scenarios: BcScenario[] = [
    {
      id: 'internet',
      title: 'Da li korisnik može pristupiti internetu?',
      goal: 'internet_access',
      leaves: [
        { service: 'dns_server_dostupan',  label: 'DNS server dostupan',       group: 'DNS radi',         works: true },
        { service: 'dns_cache_validan',    label: 'DNS cache validan',         group: 'DNS radi',         works: true },
        { service: 'ping_gateway_uspesan', label: 'Ping gateway uspešan',      group: 'Gateway dostupan', works: true },
        { service: 'dhcp_ili_staticki_ip', label: 'DHCP radi ili statički IP', group: 'IP konfigurisan',  works: true },
        { service: 'driver_instaliran',    label: 'Driver instaliran',         group: 'IP → adapter',     works: true }
      ],
      dependencies: [
        { service: 'internet_access', dependency: 'dns_radi' },
        { service: 'internet_access', dependency: 'gateway_dostupan' },
        { service: 'internet_access', dependency: 'ip_konfigurisan' },
        { service: 'dns_radi',         dependency: 'dns_server_dostupan' },
        { service: 'dns_radi',         dependency: 'dns_cache_validan' },
        { service: 'gateway_dostupan', dependency: 'ping_gateway_uspesan' },
        { service: 'ip_konfigurisan',  dependency: 'dhcp_ili_staticki_ip' },
        { service: 'ip_konfigurisan',  dependency: 'adapter_vidljiv' },
        { service: 'adapter_vidljiv',  dependency: 'adapter_ukljucen' },
        { service: 'adapter_ukljucen', dependency: 'driver_instaliran' }
      ]
    },
    {
      id: 'stable',
      title: 'Da li je konekcija stabilna?',
      goal: 'stabilna_konekcija',
      leaves: [
        { service: 'nema_packet_loss_alarma', label: 'Nema packet loss alarma',     group: 'Nizak packet loss', works: true },
        { service: 'nema_latency_alarma',     label: 'Nema latency alarma',          group: 'Niska latencija',   works: true },
        { service: 'jak_wifi_signal',         label: 'Jak WiFi signal',              group: 'Stabilan signal',   works: true },
        { service: 'nema_cestih_disconnect',  label: 'Nema čestih diskonekcija',     group: 'Stabilan signal',   works: true }
      ],
      dependencies: [
        { service: 'stabilna_konekcija', dependency: 'nizak_packet_loss' },
        { service: 'stabilna_konekcija', dependency: 'niska_latencija' },
        { service: 'stabilna_konekcija', dependency: 'stabilan_signal' },
        { service: 'nizak_packet_loss',  dependency: 'nema_packet_loss_alarma' },
        { service: 'niska_latencija',    dependency: 'nema_latency_alarma' },
        { service: 'stabilan_signal',    dependency: 'jak_wifi_signal' },
        { service: 'stabilan_signal',    dependency: 'nema_cestih_disconnect' }
      ]
    },
    {
      id: 'secure',
      title: 'Da li je mreža sigurna?',
      goal: 'sigurna_mreza',
      leaves: [
        { service: 'firewall_ukljucen', label: 'Firewall uključen',          group: 'Firewall aktivan', works: true },
        { service: 'nema_port_scan',    label: 'Nema port scan pokušaja',     group: 'Nema upada',       works: true },
        { service: 'nema_brute_force',  label: 'Nema brute force pokušaja',   group: 'Nema upada',       works: true },
        { service: 'wpa2_ili_jaca',     label: 'WPA2 ili jača enkripcija',    group: 'Enkripcija',       works: true }
      ],
      dependencies: [
        { service: 'sigurna_mreza',  dependency: 'firewall_aktivan' },
        { service: 'sigurna_mreza',  dependency: 'nema_upada' },
        { service: 'sigurna_mreza',  dependency: 'enkripcija' },
        { service: 'firewall_aktivan', dependency: 'firewall_ukljucen' },
        { service: 'nema_upada',       dependency: 'nema_port_scan' },
        { service: 'nema_upada',       dependency: 'nema_brute_force' },
        { service: 'enkripcija',       dependency: 'wpa2_ili_jaca' }
      ]
    }
  ];

  selectedScenarioId = 'internet';
  groupedLeaves: { group: string; items: LeafNode[] }[] = [];
  bcResult: any = null;
  bcLoading = false;
  bcError = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.getEnums().subscribe({
      next: (e) => {
        this.symptoms = e.symptoms;
        this.deviceTypes = e.deviceTypes;
        this.connectionTypes = e.connectionTypes;
        this.availableTests = e.tests;
        this.availableMeasurements = e.measurements;
      },
      error: () => { this.error = 'Backend nije dostupan na :8080. Pokreni Spring servis.'; }
    });
    this.recomputeGroups();
  }

  get scenario(): BcScenario {
    return this.scenarios.find(s => s.id === this.selectedScenarioId)!;
  }

  // grupe racunamo samo pri promeni scenarija (ne u getteru - pravi beskonacnu petlju)
  recomputeGroups() {
    const map = new Map<string, LeafNode[]>();
    for (const l of this.scenario.leaves) {
      if (!map.has(l.group)) map.set(l.group, []);
      map.get(l.group)!.push(l);
    }
    this.groupedLeaves = Array.from(map.entries()).map(([group, items]) => ({ group, items }));
  }

  // ---- DIJAGNOSTIKA ----
  addTest() { const f = this.availableTests[0]?.name ?? ''; this.tests.push({ name: f, success: true }); }
  removeTest(i: number) { this.tests.splice(i, 1); }
  addMeasurement() { const f = this.availableMeasurements[0]?.parameter ?? ''; this.measurements.push({ parameter: f, value: 0 }); }
  removeMeasurement(i: number) { this.measurements.splice(i, 1); }
  addPing() { this.pings.push({ target: '', attempts: 10, successful: 10 }); }
  removePing(i: number) { this.pings.splice(i, 1); }
  unitFor(p: string): string { return this.availableMeasurements.find(m => m.parameter === p)?.unit ?? ''; }

  diagnose() {
    this.error = ''; this.report = null; this.loading = true;
    const request = {
      problem: { symptom: this.symptom, deviceType: this.deviceType, connectionType: this.connectionType, details: '' },
      tests: this.tests, measurements: this.measurements, pings: this.pings
    };
    this.api.diagnose(request).subscribe({
      next: (r) => { this.report = r; this.loading = false; },
      error: (e) => { this.error = 'Greska pri dijagnostici: ' + (e.message || 'nepoznata'); this.loading = false; }
    });
  }

  loadExample() {
    this.symptom = 'NO_INTERNET'; this.deviceType = 'LAPTOP'; this.connectionType = 'WIFI';
    this.tests = [
      { name: 'local_network_works', success: true },
      { name: 'gateway_ping', success: true },
      { name: 'dns_server_ping', success: false }
    ];
    this.measurements = [
      { parameter: 'packet_loss', value: 22 },
      { parameter: 'latency', value: 150 },
      { parameter: 'dns_response_time', value: 800 }
    ];
    this.pings = [
      { target: 'gateway', attempts: 10, successful: 10 },
      { target: '8.8.8.8', attempts: 10, successful: 6 },
      { target: '1.1.1.1', attempts: 10, successful: 7 }
    ];
  }

  // ---- BACKWARD CHAINING ----
  onScenarioChange() { this.bcResult = null; this.bcAllWorking(); this.recomputeGroups(); }
  bcAllWorking() { this.scenario.leaves.forEach(l => l.works = true); }
  bcBreakFirst() {
    this.scenario.leaves.forEach(l => l.works = true);
    if (this.scenario.leaves.length) this.scenario.leaves[0].works = false;
  }

  checkAvailability() {
    this.bcError = ''; this.bcResult = null; this.bcLoading = true;
    const sc = this.scenario;
    const works = sc.leaves.filter(l => l.works).map(l => ({ service: l.service }));
    const request = { target: sc.goal, works, dependencies: sc.dependencies };

    this.api.checkAvailability(request).subscribe({
      next: (r) => { this.bcResult = r; this.bcLoading = false; },
      error: (e) => { this.bcError = 'Greska: ' + (e.message || 'nepoznata'); this.bcLoading = false; }
    });
  }

  leafLabel(service: string): string {
    return this.scenario.leaves.find(l => l.service === service)?.label ?? service;
  }
}