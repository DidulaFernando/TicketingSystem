import { Component, OnInit, OnDestroy, AfterViewInit, ViewChild, ElementRef, ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-log-viewer',
  templateUrl: './log-viewer.component.html',
  styleUrls: ['./log-viewer.component.css'],
})
export class LogViewerComponent implements OnInit, OnDestroy, AfterViewInit {
  logs: string[] = [];
  private eventSource: EventSource | undefined;

  @ViewChild('logViewer') logViewer!: ElementRef;

  constructor(private changeDetector: ChangeDetectorRef) {}

  ngOnInit() {
    this.startLogStreaming();
  }

  ngAfterViewInit() {
    this.scrollToBottom();
  }

  ngOnDestroy() {
    this.stopLogStreaming();
  }

  private startLogStreaming() {
    if (!this.eventSource) {
      this.eventSource = new EventSource('http://localhost:8080/api/example/logs/stream');
      this.eventSource.onmessage = (event) => {
        const logMessage = event.data;
        // Append logs immutably
        this.logs = [...this.logs, logMessage];

        this.changeDetector.detectChanges(); // Ensure immediate UI refresh
        this.scrollToBottom(); // Keep the log scrolled to the bottom
      };

      this.eventSource.onerror = () => {
        console.error('Error in log stream, reconnecting...');
        this.stopLogStreaming();
        setTimeout(() => this.startLogStreaming(), 3000); // Retry connection
      };
    }
  }

  private stopLogStreaming() {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = undefined;
    }
  }

  private scrollToBottom() {
    if (this.logViewer) {
      const element = this.logViewer.nativeElement;
      element.scrollTop = element.scrollHeight;
    }
  }

  trackByIndex(index: number): number {
    return index;
  }
}
