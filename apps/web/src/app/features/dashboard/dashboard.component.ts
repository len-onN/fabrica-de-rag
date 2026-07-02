import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { AuthService } from '../../core/auth/auth.service';
import { WorkspaceService, DashboardSummaryResponse, DashboardFilters } from '../../core/http/workspace.service';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { lucideLayers, lucideFileText, lucideActivity, lucidePlus, lucideAlertTriangle, lucideSearch, lucideMessageSquare, lucideSettings } from '@ng-icons/lucide';
import { filter, switchMap, debounceTime } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgIconComponent],
  providers: [provideIcons({ lucideLayers, lucideFileText, lucideActivity, lucidePlus, lucideAlertTriangle, lucideSearch, lucideMessageSquare, lucideSettings })],
  template: `
    <div class="dashboard-container">
      <header class="dashboard-header">
        <h1>Dashboard</h1>
        <p class="subtitle">Visão geral do seu workspace ativo</p>
      </header>

      <div class="filters-panel">
        <form [formGroup]="filterForm" class="filter-form">
          <div class="filter-group">
            <label for="preset">Intervalo</label>
            <select id="preset" formControlName="preset">
              <option value="24h">Últimas 24h</option>
              <option value="7d">Últimos 7 dias</option>
              <option value="30d">Últimos 30 dias</option>
            </select>
          </div>
          
          <div class="filter-group origin-filters">
            <label>Origens</label>
            <div class="checkbox-group">
              <label><input type="checkbox" formControlName="originUi"> UI</label>
              <label><input type="checkbox" formControlName="originApi"> API</label>
              <label><input type="checkbox" formControlName="originWorker"> Worker</label>
              <label><input type="checkbox" formControlName="originMcp"> MCP</label>
            </div>
          </div>
        </form>
      </div>

      @if (loading()) {
        <div class="loading-state">
          Carregando dados do workspace...
        </div>
      } @else if (summary()) {
        
        @if (summary()?.empty) {
          <div class="empty-state">
            <div class="empty-icon">
              <ng-icon name="lucideLayers" size="48"></ng-icon>
            </div>
            <h2>Nenhum evento registrado</h2>
            <p>Seu workspace ainda não possui atividades no período selecionado ou está totalmente vazio.</p>
          </div>
        } @else {
          
          <section class="dashboard-section">
            <h2 class="section-title">Ingestão</h2>
            <div class="metrics-grid">
              <div class="metric-card">
                <div class="metric-icon"><ng-icon name="lucideActivity" size="24"></ng-icon></div>
                <div class="metric-content">
                  <span class="metric-label">Runs Iniciadas</span>
                  <span class="metric-value">{{ summary()?.ingestion?.runsStarted }}</span>
                </div>
              </div>
              <div class="metric-card success-card">
                <div class="metric-icon"><ng-icon name="lucideLayers" size="24"></ng-icon></div>
                <div class="metric-content">
                  <span class="metric-label">Completas</span>
                  <span class="metric-value">{{ summary()?.ingestion?.runsCompleted }}</span>
                </div>
              </div>
              <div class="metric-card danger-card">
                <div class="metric-icon"><ng-icon name="lucideAlertTriangle" size="24"></ng-icon></div>
                <div class="metric-content">
                  <span class="metric-label">Falhas</span>
                  <span class="metric-value">{{ summary()?.ingestion?.runsFailed }}</span>
                </div>
              </div>
              <div class="metric-card info-card">
                <div class="metric-content">
                  <span class="metric-label">Tempo Mediano (ms)</span>
                  <span class="metric-value">{{ summary()?.ingestion?.medianDurationMs }}</span>
                </div>
              </div>
            </div>
          </section>

          <section class="dashboard-section">
            <h2 class="section-title">RAG (Busca e Resposta)</h2>
            <div class="metrics-grid">
              <div class="metric-card">
                <div class="metric-icon"><ng-icon name="lucideSearch" size="24"></ng-icon></div>
                <div class="metric-content">
                  <span class="metric-label">Consultas</span>
                  <span class="metric-value">{{ summary()?.retrieval?.queriesCount }}</span>
                </div>
              </div>
              <div class="metric-card warning-card">
                <div class="metric-content">
                  <span class="metric-label">Baixa Confiança</span>
                  <span class="metric-value">{{ summary()?.retrieval?.lowConfidenceCount }}</span>
                </div>
              </div>
              <div class="metric-card info-card">
                <div class="metric-icon"><ng-icon name="lucideMessageSquare" size="24"></ng-icon></div>
                <div class="metric-content">
                  <span class="metric-label">Respostas Geradas</span>
                  <span class="metric-value">{{ summary()?.answers?.generatedCount }}</span>
                </div>
              </div>
              <div class="metric-card warning-card">
                <div class="metric-content">
                  <span class="metric-label">Evidência Insuficiente</span>
                  <span class="metric-value">{{ summary()?.answers?.insufficientEvidenceCount }}</span>
                </div>
              </div>
            </div>
          </section>

          <section class="dashboard-section split-section">
            <div class="half-section">
              <h2 class="section-title">API e MCP</h2>
              <div class="metrics-grid compact-grid">
                <div class="metric-card">
                  <div class="metric-content">
                    <span class="metric-label">Chamadas API</span>
                    <span class="metric-value">{{ summary()?.apiAndMcp?.apiCallsCount }}</span>
                  </div>
                </div>
                <div class="metric-card">
                  <div class="metric-content">
                    <span class="metric-label">Chamadas MCP</span>
                    <span class="metric-value">{{ summary()?.apiAndMcp?.mcpToolCallsCount }}</span>
                  </div>
                </div>
                <div class="metric-card danger-card">
                  <div class="metric-content">
                    <span class="metric-label">Limites Atingidos</span>
                    <span class="metric-value">{{ summary()?.apiAndMcp?.limitHitCount }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="half-section">
              <h2 class="section-title">Top Falhas</h2>
              @if (summary()?.topFailures?.length === 0) {
                <div class="empty-table-state">Nenhuma falha registrada.</div>
              } @else {
                <table class="failures-table">
                  <thead>
                    <tr>
                      <th>Etapa</th>
                      <th>Erro</th>
                      <th>Contagem</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (failure of summary()?.topFailures; track failure.errorCode) {
                      <tr>
                        <td>{{ failure.stageName }}</td>
                        <td class="error-code">{{ failure.errorCode }}</td>
                        <td>{{ failure.count }}</td>
                      </tr>
                    }
                  </tbody>
                </table>
              }
            </div>
          </section>

        }
      }
    </div>
  `,
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  private auth = inject(AuthService);
  private workspaceService = inject(WorkspaceService);
  private fb = inject(FormBuilder);
  
  loading = signal(true);
  summary = signal<DashboardSummaryResponse | null>(null);
  workspaceId: string | null = null;
  private filterChangeSubject = new Subject<DashboardFilters>();

  filterForm = this.fb.group({
    preset: ['7d'],
    originUi: [true],
    originApi: [true],
    originWorker: [true],
    originMcp: [true]
  });

  ngOnInit() {
    // 1. First subscribe to filter changes so we don't miss the initial emission
    this.filterChangeSubject.pipe(
      debounceTime(300),
      switchMap(filters => this.workspaceService.getDashboardSummary(this.workspaceId!, filters))
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

    // 2. Then subscribe to authState$, which may emit synchronously (BehaviorSubject)
    this.auth.authState$.pipe(
      filter(state => !!state && !!state.activeWorkspaceId)
    ).subscribe(state => {
      this.workspaceId = state!.activeWorkspaceId;
      this.loadDashboard();
    });

    this.filterForm.valueChanges.subscribe(val => {
      if (this.workspaceId) {
        const origins: string[] = [];
        if (val.originUi) origins.push('ui');
        if (val.originApi) origins.push('api');
        if (val.originWorker) origins.push('worker');
        if (val.originMcp) origins.push('mcp');

        this.loading.set(true);
        this.filterChangeSubject.next({
          preset: val.preset || '7d',
          origins
        });
      }
    });
  }

  private loadDashboard() {
    if (!this.workspaceId) return;
    this.loading.set(true);
    
    const val = this.filterForm.value;
    const origins: string[] = [];
    if (val.originUi) origins.push('ui');
    if (val.originApi) origins.push('api');
    if (val.originWorker) origins.push('worker');
    if (val.originMcp) origins.push('mcp');

    this.filterChangeSubject.next({
      preset: val.preset || '7d',
      origins
    });
  }
}
