import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="w-full max-w-sm bg-white dark:bg-slate-800 rounded-xl shadow-xl p-8 border border-slate-100 dark:border-slate-700">
      <div class="text-center mb-8">
        <h1 class="text-2xl font-bold text-slate-900 dark:text-white">Fábrica de RAG</h1>
        <p class="text-slate-500 dark:text-slate-400 mt-2">Faça login para continuar</p>
      </div>

      <form (ngSubmit)="onSubmit()" #loginForm="ngForm" class="space-y-5">
        <div>
          <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">E-mail</label>
          <input type="email" name="email" [(ngModel)]="credentials.email" required class="w-full px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-shadow">
        </div>
        
        <div>
          <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">Senha</label>
          <input type="password" name="password" [(ngModel)]="credentials.password" required class="w-full px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-700 text-slate-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-shadow">
        </div>

        <div *ngIf="error" class="p-3 bg-red-50 dark:bg-red-900/30 text-red-600 dark:text-red-400 text-sm rounded-lg">
          {{ error }}
        </div>

        <button type="submit" [disabled]="loading || loginForm.invalid" class="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors disabled:opacity-50 flex justify-center items-center">
          <span *ngIf="loading" class="mr-2 h-4 w-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
          Entrar
        </button>
      </form>
    </div>
  `
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  credentials = {
    email: '',
    password: ''
  };

  loading = false;
  error = '';

  onSubmit() {
    this.loading = true;
    this.error = '';
    
    this.authService.login(this.credentials).subscribe({
      next: () => {
        this.authService.me().subscribe({
          next: () => this.router.navigate(['/']),
          error: () => this.router.navigate(['/'])
        });
      },
      error: (err) => {
        this.loading = false;
        this.error = 'E-mail ou senha inválidos.';
      }
    });
  }
}
