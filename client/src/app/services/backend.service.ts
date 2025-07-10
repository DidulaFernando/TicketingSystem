import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BackendService {
  // Base URLs for the two backends
  private apiUrlSpringBoot = 'http://localhost:8080/api'; // For Spring Boot backend
  private apiUrlNodeJs = 'http://localhost:8090/api'; // For Node.js backend

  constructor(private http: HttpClient) {}

  // ------- SPRING BOOT BACKEND METHODS -------

  // Get ticket status information from the Spring Boot backend
  getTicketStatus(): Observable<{ totalTicketsPurchased: number; newTicketsReleased: number }> {
    return this.http.get<{ totalTicketsPurchased: number; newTicketsReleased: number }>(
      `${this.apiUrlSpringBoot}/status`
    );
  }

  // Start the system with a given configuration (Spring Boot backend)
  startSystem(config: any): Observable<string> {
    return this.http.post(`${this.apiUrlSpringBoot}/start`, config, { responseType: 'text' });
  }

  // Stop the system (Spring Boot backend)
  stopSystem(): Observable<string> {
    return this.http.post(`${this.apiUrlSpringBoot}/stop`, {}, { responseType: 'text' });
  }

  // Check if the system is ready (Spring Boot backend)
  isSystemReady(): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrlSpringBoot}/ready`);
  }

  // ------- NODE.JS BACKEND METHODS (DATABASE OPERATIONS) -------

  // Save the system's general configuration (Node.js backend)
  saveConfiguration(config: { totalTickets: number; ticketReleaseRate: number; customerRetrievalRate: number; maxTicketCapacity: number }): Observable<string> {
    return this.http.post(`${this.apiUrlNodeJs}/save-configuration`, config, { responseType: 'text' });
  }

  // Load the most recent configuration details (Node.js backend)
  loadConfiguration(): Observable<{ totalTickets: number; ticketReleaseRate: number; customerRetrievalRate: number; maxTicketCapacity: number }> {
    return this.http.get<{ totalTickets: number; ticketReleaseRate: number; customerRetrievalRate: number; maxTicketCapacity: number }>(
      `${this.apiUrlNodeJs}/load-configuration`
    );
  }

  // Save Vendors and Customers to the database (Node.js backend)
  saveEntities(entities: { name: string; type: string }[]): Observable<string> {
    return this.http.post(`${this.apiUrlNodeJs}/save-entities`, entities, { responseType: 'text' });
  }

  // Load Vendors and Customers from the database (Node.js backend)
  loadEntities(): Observable<{ vendors: string[]; customers: string[] }> {
    return this.http.get<{ vendors: string[]; customers: string[] }>(`${this.apiUrlNodeJs}/load-entities`);
  }

  // Save the full configuration, including parameters, vendors, and customers (Node.js backend)
  saveFullConfiguration(config: {
    totalTickets: number;
    ticketReleaseRate: number;
    customerRetrievalRate: number;
    maxTicketCapacity: number;
    vendors: string[];
    customers: string[];
  }): Observable<string> {
    return this.http.post(`${this.apiUrlNodeJs}/save-full-configuration`, config, { responseType: 'text' });
  }

  // Load the full configuration, including parameters, vendors, and customers (Node.js backend)
  loadFullConfiguration(): Observable<{
    totalTickets: number;
    ticketReleaseRate: number;
    customerRetrievalRate: number;
    maxTicketCapacity: number;
    vendors: string[];
    customers: string[];
  }> {
    return this.http.get<{
      totalTickets: number;
      ticketReleaseRate: number;
      customerRetrievalRate: number;
      maxTicketCapacity: number;
      vendors: string[];
      customers: string[];
    }>(`${this.apiUrlNodeJs}/load-full-configuration`);
  }
}
