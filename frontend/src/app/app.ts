import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from './api.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
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
      error: () => {
        this.error = 'Backend nije dostupan na :8080. Pokreni Spring servis.';
      }
    });
  }

  addTest() {
    const first = this.availableTests[0]?.name ?? '';
    this.tests.push({ name: first, success: true });
  }
  removeTest(i: number) { this.tests.splice(i, 1); }

  addMeasurement() {
    const first = this.availableMeasurements[0]?.parameter ?? '';
    this.measurements.push({ parameter: first, value: 0 });
  }
  removeMeasurement(i: number) { this.measurements.splice(i, 1); }

  addPing() { this.pings.push({ target: '', attempts: 10, successful: 10 }); }
  removePing(i: number) { this.pings.splice(i, 1); }

  unitFor(parameter: string): string {
    return this.availableMeasurements.find(m => m.parameter === parameter)?.unit ?? '';
  }

  diagnose() {
    this.error = '';
    this.report = null;
    this.loading = true;

    const request = {
      problem: {
        symptom: this.symptom,
        deviceType: this.deviceType,
        connectionType: this.connectionType,
        details: ''
      },
      tests: this.tests,
      measurements: this.measurements,
      pings: this.pings
    };

    this.api.diagnose(request).subscribe({
      next: (r) => { this.report = r; this.loading = false; },
      error: (e) => {
        this.error = 'Greska pri dijagnostici: ' + (e.message || 'nepoznata');
        this.loading = false;
      }
    });
  }

  loadExample() {
    this.symptom = 'NO_INTERNET';
    this.deviceType = 'LAPTOP';
    this.connectionType = 'WIFI';
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
}