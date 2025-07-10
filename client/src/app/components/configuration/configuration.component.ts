import { Component, Input, Output, EventEmitter } from '@angular/core';
import { BackendService } from '../../services/backend.service';

@Component({
  selector: 'app-configuration',
  templateUrl: './configuration.component.html',
  styleUrls: ['./configuration.component.css'],
})
export class ConfigurationComponent {
  @Input() hasStarted!: boolean;
  @Output() toggleSystemState = new EventEmitter<void>();

  // Configuration fields
  totalTickets: number | null = null;
  ticketReleaseRate: number | null = null;
  customerRetrievalRate: number | null = null;
  maxTicketCapacity: number | null = null;

  // Vendor and Customer fields
  vendorName: string = '';
  customerName: string = '';
  vendors: string[] = [];
  customers: string[] = [];

  constructor(private backendService: BackendService) {}

  // Add a vendor
  addVendor(): void {
    if (this.vendorName.trim()) {
      this.vendors.push(this.vendorName.trim());
      this.vendorName = ''; // Clear input field
    }
  }

  // Remove the last vendor
  removeVendor(): void {
    if (this.vendors.length > 0) {
      this.vendors.pop();
    }
  }

  // Add a customer
  addCustomer(): void {
    if (this.customerName.trim()) {
      this.customers.push(this.customerName.trim());
      this.customerName = ''; // Clear input field
    }
  }

  // Remove the last customer
  removeCustomer(): void {
    if (this.customers.length > 0) {
      this.customers.pop();
    }
  }

  // Start the system using the Spring Boot backend
  startSystem(): void {
    if (
      this.totalTickets !== null &&
      this.ticketReleaseRate !== null &&
      this.customerRetrievalRate !== null &&
      this.maxTicketCapacity !== null &&
      this.vendors.length > 0 &&
      this.customers.length > 0
    ) {
      const config = {
        totalTickets: this.totalTickets,
        ticketReleaseRate: this.ticketReleaseRate,
        customerRetrievalRate: this.customerRetrievalRate,
        maxTicketCapacity: this.maxTicketCapacity,
        vendors: this.vendors,
        customers: this.customers,
      };

      this.backendService.startSystem(config).subscribe(
        () => {
          alert('System started successfully!');
          this.toggleSystemState.emit();
        },
        (error) => {
          console.error('Error starting the system:', error);
          alert('Failed to start the system.');
        }
      );
    } else {
      alert('Please fill out all fields and add at least one vendor and customer.');
    }
  }

  // Stop the system using the Spring Boot backend
  endSystem(): void {
    this.backendService.stopSystem().subscribe(
      () => {
        alert('System stopped successfully!');
        this.toggleSystemState.emit();
      },
      (error) => {
        console.error('Error stopping the system:', error);
        alert('Failed to stop the system.');
      }
    );
  }

  // Save the full configuration, including parameters, vendors, and customers, to the Node.js backend
  saveFullConfiguration(): void {
    if (
      this.totalTickets !== null &&
      this.ticketReleaseRate !== null &&
      this.customerRetrievalRate !== null &&
      this.maxTicketCapacity !== null
    ) {
      const config = {
        totalTickets: this.totalTickets,
        ticketReleaseRate: this.ticketReleaseRate,
        customerRetrievalRate: this.customerRetrievalRate,
        maxTicketCapacity: this.maxTicketCapacity,
        vendors: this.vendors,
        customers: this.customers,
      };

      this.backendService.saveFullConfiguration(config).subscribe(
        () => {
          alert('Configuration saved successfully!');
        },
        (error) => {
          console.error('Error saving configuration:', error);
          alert('Failed to save configuration.');
        }
      );
    } else {
      alert('Please fill out all fields before saving.');
    }
  }

  // Save only Vendors and Customers to the Node.js backend using the full configuration endpoint
  newSaveConfiguration(): void {
    if (this.vendors.length === 0 && this.customers.length === 0) {
      alert('No vendors or customers to save.');
      return;
    }

    // Using placeholder values for required fields
    const config = {
      totalTickets: 1, // Placeholder value
      ticketReleaseRate: 1, // Placeholder value
      customerRetrievalRate: 1, // Placeholder value
      maxTicketCapacity: 1, // Placeholder value
      vendors: this.vendors,
      customers: this.customers,
    };

    this.backendService.saveFullConfiguration(config).subscribe(
      () => {
        alert('Vendors and Customers saved successfully!');
      },
      (error) => {
        console.error('Error saving entities:', error);
        alert('Failed to save Vendors and Customers.');
      }
    );
  }

  // Load only Vendors and Customers from the Node.js backend
  newLoadConfiguration(): void {
    this.backendService.loadFullConfiguration().subscribe(
      (config: any) => {
        this.vendors = config.vendors;
        this.customers = config.customers;
        alert('Vendors and Customers loaded successfully!');
      },
      (error) => {
        console.error('Error loading entities:', error);
        alert('Failed to load Vendors and Customers.');
      }
    );
  }

  // Load the full configuration, including parameters, vendors, and customers, from the Node.js backend
  loadFullConfiguration(): void {
    this.backendService.loadFullConfiguration().subscribe(
      (config: any) => {
        this.totalTickets = config.totalTickets;
        this.ticketReleaseRate = config.ticketReleaseRate;
        this.customerRetrievalRate = config.customerRetrievalRate;
        this.maxTicketCapacity = config.maxTicketCapacity;
        this.vendors = config.vendors;
        this.customers = config.customers;

        alert('Configuration loaded successfully!');
      },
      (error) => {
        console.error('Error loading configuration:', error);
        alert('Failed to load configuration.');
      }
    );
  }
}
