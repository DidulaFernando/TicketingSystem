import { Component, OnInit, OnDestroy, OnChanges, Input } from '@angular/core';
import { Subscription, interval, BehaviorSubject } from 'rxjs';
import { Color, ScaleType } from '@swimlane/ngx-charts';
import { curveLinear } from 'd3-shape';
import { BackendService } from '../../services/backend.service';

@Component({
  selector: 'app-live-update',
  templateUrl: './live-update.component.html',
  styleUrls: ['./live-update.component.css'],
})
export class LiveUpdateComponent implements OnInit, OnDestroy, OnChanges {
  @Input() hasStarted!: boolean;

  // State tracking for ticket stats
  totalTicketsPurchased: number = 0;
  totalTicketsReleased: number = 0;

  // Chart settings
  chartDimensions: [number, number] = [400, 365];
  curve = curveLinear;

  timeSeriesChartData: any[] = [
    { name: 'Total Tickets Purchased', series: [] },
    { name: 'Total Tickets Released', series: [] },
  ];

  colorScheme: Color = {
    name: 'Custom Scheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#5AA454', '#A10A28', '#C7B42C', '#AAAAAA'],
  };

  private dataSubscription!: Subscription; // Handles the polling interval
  private liveUpdates$: BehaviorSubject<any> = new BehaviorSubject<any>(null); // Observes data updates

  constructor(private backendService: BackendService) {}

  ngOnInit() {
    if (this.hasStarted) {
      this.startLiveUpdates();
    }
  }

  ngOnChanges() {
    // Check if component's status changes (e.g., hasStarted toggles)
    if (this.hasStarted) {
      // Check system readiness before starting updates
      this.backendService.isSystemReady().subscribe(
        (isReady: boolean) => {
          if (isReady) {
            console.log('System is ready. Starting live updates...');
            this.resetState(); // Reset before starting
            this.startLiveUpdates();
          } else {
            console.warn('System is not ready. Live updates will not start.');
          }
        },
        (error) => {
          console.error('Error checking system readiness:', error.message || error);
          // Fallback: Assume the system is ready if the endpoint is unavailable
          this.resetState();
          this.startLiveUpdates();
        }
      );
    } else {
      console.log('Stopping live updates...');
      this.stopLiveUpdates();
      this.resetState(); // Clear the state on stop
    }
  }

  /**
   * Start fetching live updates from the backend
   */
  startLiveUpdates() {
    if (!this.dataSubscription || this.dataSubscription.closed) {
      console.log('Starting live ticket status updates...');

      // Poll `/status` every 1000ms (1 second)
      this.dataSubscription = interval(1000).subscribe(() => {
        this.fetchLiveUpdates();
      });

      // Subscribe to BehaviorSubject to listen for updates
      this.liveUpdates$.subscribe(
        (data) => {
          if (data) {
            console.log('Incoming data:', data); // Debugging: Logs incoming data
            this.updateState(data);
          }
        },
        (error) => console.error('Error in live updates observable:', error)
      );
    }
  }

  /**
   * Stop fetching updates and clean up subscriptions
   */
  stopLiveUpdates() {
    if (this.dataSubscription && !this.dataSubscription.closed) {
      this.dataSubscription.unsubscribe(); // Stop the interval
    }
    this.liveUpdates$.next(null); // Clear the observable data
  }

  /**
   * Fetch live updates from the backend API
   */
  private fetchLiveUpdates() {
    this.backendService.getTicketStatus().subscribe(
      (status) => {
        console.log('Backend response:', status); // Debugging: Logs backend response
        if (status) {
          this.liveUpdates$.next(status); // Push the status to the subscription pipeline
        } else {
          console.warn('Warning: Empty or invalid status response.');
        }
      },
      (error) => {
        console.error('Error fetching status from backend:', error); // Debugging: Logs error
      }
    );
  }

  /**
   * Update the component state (tickets and chart) based on new data
   */
  private updateState(status: { totalTicketsPurchased: number; newTicketsReleased: number }) {
    // Ensure new data is valid
    if (
      status &&
      typeof status.totalTicketsPurchased === 'number' &&
      typeof status.newTicketsReleased === 'number'
    ) {
      console.log('Updating state with:', status); // Debugging: Logs valid state update
      this.totalTicketsPurchased = status.totalTicketsPurchased;
      this.totalTicketsReleased = status.newTicketsReleased;

      const currentTime = new Date().toLocaleTimeString(); // Format time for chart data

      // Update data series for the chart and trim to the last 20 data points
      this.timeSeriesChartData = [
        {
          name: 'Total Tickets Purchased',
          series: [
            ...this.timeSeriesChartData[0].series,
            { name: currentTime, value: this.totalTicketsPurchased },
          ].slice(-20), // Keep last 20 points
        },
        {
          name: 'Total Tickets Released',
          series: [
            ...this.timeSeriesChartData[1].series,
            { name: currentTime, value: this.totalTicketsReleased },
          ].slice(-20), // Keep last 20 points
        },
      ];
    } else {
      console.warn('Invalid data received in updateState:', status); // Debugging: Logs invalid state data
    }
  }

  /**
   * Reset the component state
   */
  private resetState() {
    console.log('Resetting state...');
    this.totalTicketsPurchased = 0;
    this.totalTicketsReleased = 0;
    this.timeSeriesChartData = [
      { name: 'Total Tickets Purchased', series: [] },
      { name: 'Total Tickets Released', series: [] },
    ];
  }

  ngOnDestroy() {
    console.log('LiveUpdateComponent destroyed. Cleaning up...');
    this.stopLiveUpdates(); // Clean up interval and subscriptions
  }
}
