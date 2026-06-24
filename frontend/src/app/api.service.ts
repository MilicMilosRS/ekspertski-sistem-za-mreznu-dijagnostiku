import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private base = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  diagnose(request: any): Observable<any> {
    return this.http.post(`${this.base}/diagnostics`, request);
  }

  checkAvailability(request: any): Observable<any> {
    return this.http.post(`${this.base}/availability`, request);
  }

  getEnums(): Observable<any> {
    return this.http.get(`${this.base}/metadata/enums`);
  }
}