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
  // enum vrednosti iz backend-a (za dropdown-ove)
  symptoms: string[] = [];
  deviceTypes: string[] = [];
  connectionTypes: string[] = [];

  // forma - osnovni problem
  symptom = '';
  deviceType = '';
  connectionType = '';

  // dinamicke liste
  tests: { name: string; success: boolean }[] = [];
  measurements: { parameter: string; value: number }[] = [];
  pings: { target: string; attempts: number; successful: number }[] = [];

  // rezultat
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
      },
      error: () => {
        this.error = 'Backend nije dostupan na :8080. Pokreni Spring servis.';
      }
    });
  }

  addTest() { this.tests.push({ name: '', success: true }); }
  removeTest(i: number) { this.tests.splice(i, 1); }

  addMeasurement() { this.measurements.push({ parameter: 'packet_loss', value: 0 }); }
  removeMeasurement(i: number) { this.measurements.splice(i, 1); }

  addPing() { this.pings.push({ target: '', attempts: 10, successful: 10 }); }
  removePing(i: number) { this.pings.splice(i, 1); }

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

  // brzi demo - popuni primer scenario
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
