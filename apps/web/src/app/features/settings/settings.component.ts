import { Component, inject, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';
import { AuthService } from '../../core/auth/auth.service';
import { WorkspaceService, WorkspaceSettingsResponse } from '../../core/http/workspace.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent {
  private auth = inject(AuthService);
  private workspaceService = inject(WorkspaceService);
  
  authState = toSignal(this.auth.authState$);

  activeTab = signal<'general' | 'ingestion' | 'analytics' | 'access' | 'api'>('general');
  settingsData = signal<WorkspaceSettingsResponse | null>(null);
  
  generalForm = { name: '', purpose: '', slug: '' };
  ingestionForm = { profile: 'balanced', ocr: 'auto', language: 'por', chunkTarget: 500, overlap: 50, contextPolicy: 'conservative' };
  analyticsForm = { historyMode: 'local_history', retention: 30, exportEnabled: false };

  currentRole = signal<string>('Desconhecido');
  apiUrl = 'http://localhost:8080/api/v1';

  constructor() {
    effect(() => {
      const state = this.authState();
      if (state && state.activeWorkspaceId) {
        this.loadSettings(state.activeWorkspaceId);
        
        const activeWorkspace = state.workspaces.find(w => w.id === state.activeWorkspaceId);
        if (activeWorkspace) {
          this.currentRole.set(activeWorkspace.role);
        }
      }
    });
  }

  loadSettings(workspaceId: string) {
    this.workspaceService.getWorkspaceSettings(workspaceId).subscribe({
      next: (data) => {
        this.settingsData.set(data);
        
        this.generalForm = {
          name: data.name || '',
          purpose: data.purpose || '',
          slug: data.slug || ''
        };

        const setts = data.settings || {};
        this.ingestionForm = {
          profile: setts.profile || 'balanced',
          ocr: setts.ocr || 'auto',
          language: setts.language || 'por',
          chunkTarget: setts.chunkTarget || 500,
          overlap: setts.overlap || 50,
          contextPolicy: setts.contextPolicy || 'conservative'
        };

        this.analyticsForm = {
          historyMode: setts.historyMode || 'local_history',
          retention: setts.retention || 30,
          exportEnabled: !!setts.exportEnabled
        };
      },
      error: (err) => console.error('Erro ao carregar settings', err)
    });
  }

  saveGeneral() {
    const state = this.authState();
    if (!state?.activeWorkspaceId) return;

    this.workspaceService.updateWorkspaceSettings(state.activeWorkspaceId, {
      name: this.generalForm.name,
      purpose: this.generalForm.purpose,
      slug: this.generalForm.slug
    }).subscribe({
      next: () => alert('Geral salvo com sucesso!'),
      error: () => alert('Erro ao salvar Geral')
    });
  }

  saveSettingsBlock() {
    const state = this.authState();
    if (!state?.activeWorkspaceId) return;

    const currentSettings = this.settingsData()?.settings || {};
    
    const updatedSettings = {
      ...currentSettings,
      profile: this.ingestionForm.profile,
      ocr: this.ingestionForm.ocr,
      language: this.ingestionForm.language,
      chunkTarget: this.ingestionForm.chunkTarget,
      overlap: this.ingestionForm.overlap,
      contextPolicy: this.ingestionForm.contextPolicy,
      
      historyMode: this.analyticsForm.historyMode,
      retention: this.analyticsForm.retention,
      exportEnabled: this.analyticsForm.exportEnabled
    };

    this.workspaceService.updateWorkspaceSettings(state.activeWorkspaceId, {
      settings: updatedSettings
    }).subscribe({
      next: () => alert('Configurações salvas com sucesso!'),
      error: () => alert('Erro ao salvar Configurações')
    });
  }
}
