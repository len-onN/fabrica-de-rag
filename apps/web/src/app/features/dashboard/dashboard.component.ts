import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/auth/auth.service';
import { WorkspaceService, DashboardSummaryResponse } from '../../core/http/workspace.service';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { lucideLayers, lucideFileText, lucideActivity, lucidePlus } from '@ng-icons/lucide';
import { filter, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NgIconComponent],
  providers: [provideIcons({ lucideLayers, lucideFileText, lucideActivity, lucidePlus })],
  template: `
    <div class="dashboard-container">
      <header class="dashboard-header">
        <h1>Dashboard</h1>
        <p class="subtitle">Visão geral do seu workspace ativo</p>
      </header>

      @if (loading()) {
        <div class="loading-state">
          Carregando dados do workspace...
        </div>
      } @else if (summary()) {
        <div class="metrics-grid">
          <div class="metric-card">
            <div class="metric-icon"><ng-icon name="lucideLayers" size="24"></ng-icon></div>
            <div class="metric-content">
              <span class="metric-label">Coleções</span>
              <span class="metric-value">{{ summary()?.collectionCount }}</span>
            </div>
          </div>
          
          <div class="metric-card">
            <div class="metric-icon"><ng-icon name="lucideFileText" size="24"></ng-icon></div>
            <div class="metric-content">
              <span class="metric-label">Documentos</span>
              <span class="metric-value">{{ summary()?.documentCount }}</span>
            </div>
          </div>
          
          <div class="metric-card">
            <div class="metric-icon"><ng-icon name="lucideActivity" size="24"></ng-icon></div>
            <div class="metric-content">
              <span class="metric-label">Processamentos</span>
              <span class="metric-value">{{ summary()?.activeRunsCount }}</span>
            </div>
          </div>
        </div>

        @if (summary()?.collectionCount === 0) {
          <div class="empty-state">
            <div class="empty-icon">
              <ng-icon name="lucideLayers" size="48"></ng-icon>
            </div>
            <h2>Seu workspace está vazio</h2>
            <p>Crie sua primeira coleção para começar a ingerir documentos e realizar processamentos.</p>
            <button class="primary-action">
              <ng-icon name="lucidePlus"></ng-icon>
              Nova Coleção
            </button>
          </div>
        }
      }
    </div>
  `,
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  private auth = inject(AuthService);
  private workspaceService = inject(WorkspaceService);
  
  loading = signal(true);
  summary = signal<DashboardSummaryResponse | null>(null);

  ngOnInit() {
    this.auth.authState$.pipe(
      filter(state => !!state && !!state.activeWorkspaceId),
      switchMap(state => this.workspaceService.getDashboardSummary(state!.activeWorkspaceId))
    ).subscribe({
      next: (data) => {
        this.summary.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load dashboard summary', err);
        this.loading.set(false);
      }
    });
  }
}
