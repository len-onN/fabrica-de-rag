import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-bootstrap',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDividerModule
  ],
  templateUrl: './bootstrap.component.html',
  styleUrls: ['./bootstrap.component.css']
})
export class BootstrapComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  bootstrapForm!: FormGroup;
  loading = false;
  error = '';
  hidePassword = true;
  passwordStrength = 0;

  ngOnInit() {
    this.bootstrapForm = this.fb.group({
      displayName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      workspaceName: ['', Validators.required],
      workspacePurpose: ['evaluation', Validators.required]
    });

    this.bootstrapForm.get('password')?.valueChanges.subscribe(val => {
      this.passwordStrength = this.calculatePasswordStrength(val || '');
    });
  }

  calculatePasswordStrength(password: string): number {
    if (!password) return 0;
    let score = 0;
    if (password.length >= 8) score++;
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) score++;
    if (/[0-9]/.test(password)) score++;
    if (/[^a-zA-Z0-9]/.test(password)) score++;
    return score;
  }

  getMeterClass(index: number): string {
    if (index >= this.passwordStrength) return '';
    if (this.passwordStrength === 1) return 'active-weak';
    if (this.passwordStrength === 2) return 'active-fair';
    if (this.passwordStrength === 3) return 'active-good';
    return 'active-strong';
  }

  getMeterTextClass(): string {
    if (this.passwordStrength === 1) return 'text-weak';
    if (this.passwordStrength === 2) return 'text-fair';
    if (this.passwordStrength === 3) return 'text-good';
    if (this.passwordStrength === 4) return 'text-strong';
    return '';
  }

  getMeterLabel(): string {
    if (this.passwordStrength === 0) return '';
    if (this.passwordStrength === 1) return 'Muito Fraca';
    if (this.passwordStrength === 2) return 'Razoável';
    if (this.passwordStrength === 3) return 'Boa';
    return 'Forte';
  }

  onSubmit() {
    if (this.bootstrapForm.invalid || this.passwordStrength < 4) {
      this.bootstrapForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = '';
    
    // Preparar o payload com o default analytics object, que ainda nao ta na UI
    const payload = {
      ...this.bootstrapForm.value,
      analytics: { enabled: false, retentionDays: 30 }
    };

    this.authService.bootstrap(payload).subscribe({
      next: () => {
        this.authService.me().subscribe({
          next: () => this.router.navigate(['/']),
          error: () => this.router.navigate(['/'])
        });
      },
      error: (err) => {
        this.loading = false;
        if (err.error?.fieldErrors) {
           this.error = 'Campos preenchidos incorretamente.';
           err.error.fieldErrors.forEach((fe: any) => {
             const control = this.bootstrapForm.get(fe.field);
             if (control) {
               control.setErrors({ serverError: fe.message });
             }
           });
        } else if (err.error?.detail === 'bootstrap_already_completed') {
           this.error = 'O sistema já foi inicializado! Essa página serve apenas para a criação do primeiro Administrador. Volte para a tela de Login.';
        } else {
           this.error = err.error?.detail || 'Erro ao inicializar o sistema. Verifique os logs.';
        }
      }
    });
  }
}
