import { Component, OnInit, OnDestroy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription, interval } from 'rxjs';
import { switchMap, startWith } from 'rxjs/operators';
import { IngestRunService, IngestRunDetailResponse, IngestRunLogResponse } from '../../../core/http/ingest-run.service';

@Component({
  selector: 'app-ingest-run-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ingest-run-detail.component.html',
  styleUrls: ['./ingest-run-detail.component.css']
})
export class IngestRunDetailComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private ingestRunService = inject(IngestRunService);

  run = signal<IngestRunDetailResponse | null>(null);
  logs = signal<IngestRunLogResponse[]>([]);
  error = signal<string | null>(null);
  
  workspaceId: string = '';
  runId: string = '';
  
  private pollSubscription?: Subscription;

  ngOnInit() {
    this.workspaceId = this.route.parent?.snapshot.paramMap.get('id') || 'local'; // Ou pegar de outro local dependendo da hierarquia real
    // Mas no app.routes está sob Shell, o ShellComponent talvez injete workspaceId, mas vamos pegar a partir do route ou do localStorage/authService.
    // Vamos assumir 'wsp_local' para fins de MVP ou pegar via auth.
    // Como a rota é /ingest-runs/:id vamos pegar o id do parametro.
    this.route.paramMap.subscribe(params => {
      this.runId = params.get('id') || '';
      // Workspace ID provisório até refatoração fina (MVP)
      this.workspaceId = localStorage.getItem('workspaceId') || 'wsp_fixture_alpha'; 
      this.startPolling();
    });
  }

  ngOnDestroy() {
    if (this.pollSubscription) {
      this.pollSubscription.unsubscribe();
    }
  }

  private startPolling() {
    this.pollSubscription = interval(5000).pipe(
      startWith(0),
      switchMap(() => this.ingestRunService.getRunDetails(this.workspaceId, this.runId))
    ).subscribe({
      next: (data) => {
        this.run.set(data);
        this.loadLogs();
      },
      error: (err) => {
        this.error.set('Failed to load run details.');
      }
    });
  }

  private loadLogs() {
    this.ingestRunService.getRunLogs(this.workspaceId, this.runId).subscribe({
      next: (page) => this.logs.set(page.content),
      error: () => console.error('Failed to load logs')
    });
  }

  cancelRun() {
    this.ingestRunService.cancelRun(this.workspaceId, this.runId).subscribe({
      next: () => this.forceRefresh(),
      error: () => alert('Failed to cancel run')
    });
  }

  retryRun() {
    this.ingestRunService.retryRun(this.workspaceId, this.runId).subscribe({
      next: (newRun) => {
        this.router.navigate(['/ingest-runs', newRun.publicId]);
      },
      error: () => alert('Failed to retry run')
    });
  }

  private forceRefresh() {
    this.ingestRunService.getRunDetails(this.workspaceId, this.runId).subscribe(data => {
      this.run.set(data);
    });
  }

  canCancel(): boolean {
    const status = this.run()?.status;
    return status === 'QUEUED' || status === 'RUNNING' || status === 'WAITING_FOR_REVIEW';
  }

  canRetry(): boolean {
    return this.run()?.status === 'FAILED';
  }

  getStatusBadgeClass(status: string | undefined): string {
    switch (status) {
      case 'QUEUED': return 'badge-secondary';
      case 'RUNNING': return 'badge-primary';
      case 'WAITING_FOR_REVIEW': return 'badge-warning';
      case 'COMPLETED': return 'badge-success';
      case 'FAILED': return 'badge-danger';
      case 'CANCELLED': return 'badge-dark';
      default: return 'badge-secondary';
    }
  }
}
