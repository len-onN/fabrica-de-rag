import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-bootstrap',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="w-full max-w-md bg-white dark:bg-slate-800 rounded-xl shadow-xl p-8 border border-slate-100 dark:border-slate-700">
      <div class="text-center mb-8">
        <h1 class="text-2xl font-bold text-slate-900 dark:text-white">Fábrica de RAG</h1>
        <p class="text-slate-500 dark:text-slate-400 mt-2">Configuração inicial do sistema</p>
      </div>

      <form (ngSubmit)="onSubmit()" #bootstrapForm="ngForm" class="space-y-6">
        
        <!-- User Info -->
        <div class="space-y-4">
          <h2 class="text-sm font-semibold text-slate-900 dark:text-white uppercase tracking-wider">Conta do Administrador</h2>
          <div>
            <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Nome de Exibição</label>
            <input type="text" name="displayName" [(ngModel)]="formData.displayName" required class="w-full px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-shadow">
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">E-mail</label>
            <input type="email" name="email" [(ngModel)]="formData.email" required class="w-full px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-shadow">
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Senha</label>
            <input type="password" name="password" [(ngModel)]="formData.password" required class="w-full px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-shadow">
          </div>
        </div>

        <hr class="border-slate-200 dark:border-slate-700">

        <!-- Workspace Info -->
        <div class="space-y-4">
          <h2 class="text-sm font-semibold text-slate-900 dark:text-white uppercase tracking-wider">Workspace Principal</h2>
          <div>
            <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Nome da Organização</label>
            <input type="text" name="workspaceName" [(ngModel)]="formData.workspaceName" required class="w-full px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-shadow">
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Propósito</label>
            <select name="workspacePurpose" [(ngModel)]="formData.workspacePurpose" required class="w-full px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-shadow">
              <option value="production">Produção / Empresa</option>
              <option value="evaluation">Avaliação / Testes</option>
              <option value="academic">Acadêmico / Pesquisa</option>
            </select>
          </div>
        </div>

        <div *ngIf="error" class="p-3 bg-red-50 dark:bg-red-900/30 text-red-600 dark:text-red-400 text-sm rounded-lg">
          {{ error }}
        </div>

        <button type="submit" [disabled]="loading || bootstrapForm.invalid" class="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors disabled:opacity-50 flex justify-center items-center">
          <span *ngIf="loading" class="mr-2 h-4 w-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
          Iniciar Sistema
        </button>
      </form>
    </div>
  `
})
export class BootstrapComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  formData = {
    displayName: '',
    email: '',
    password: '',
    workspaceName: '',
    workspacePurpose: 'evaluation',
    analytics: {
      enabled: false,
      retentionDays: 30
    }
  };

  loading = false;
  error = '';

  onSubmit() {
    this.loading = true;
    this.error = '';
    
    this.authService.bootstrap(this.formData).subscribe({
      next: () => {
        // After bootstrap, auto login implies session is created.
        // Fetch 'me' and navigate to home
        this.authService.me().subscribe({
          next: () => this.router.navigate(['/']),
          error: () => this.router.navigate(['/'])
        });
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.detail || 'Erro ao inicializar o sistema. Verifique os logs.';
      }
    });
  }
}
