import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'Ticketing System';

  // State to track if the system has started
  hasStarted: boolean = false;

  // Array to store logs
  logs: string[] = [];

  // Method to toggle system state
  toggleSystemState(): void {
    this.hasStarted = !this.hasStarted;
    const log = this.hasStarted ? 'System started successfully!' : 'System stopped successfully!';
    this.addLog(log);
  }

  // Method to add logs
  addLog(log: string): void {
    this.logs.push(log);

    // Ensure the log viewer starts at the bottom
    setTimeout(() => {
      const logViewer = document.querySelector('.log-viewer ul');
      if (logViewer) {
        logViewer.scrollTop = logViewer.scrollHeight;
      }
    }, 0);
  }
}
