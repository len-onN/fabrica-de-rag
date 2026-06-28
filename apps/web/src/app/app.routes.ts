import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { bootstrapGuard } from './core/guards/bootstrap.guard';

export const routes: Routes = [
  {
    path: 'auth',
    loadComponent: () => import('./features/auth/auth-layout/auth-layout.component').then(m => m.AuthLayoutComponent),
    children: [
      { path: 'login', loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent) },
      { path: 'bootstrap', loadComponent: () => import('./features/auth/bootstrap/bootstrap.component').then(m => m.BootstrapComponent), canActivate: [bootstrapGuard] },
      { path: '', redirectTo: 'login', pathMatch: 'full' }
    ]
  },
  {
    path: '',
    loadComponent: () => import('./features/shell/shell.component').then(m => m.ShellComponent),
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'settings', loadComponent: () => import('./features/settings/settings.component').then(m => m.SettingsComponent) },
      { path: 'collections', loadComponent: () => import('./features/collections/collection-list/collection-list.component').then(m => m.CollectionListComponent) },
      { path: 'collections/new', loadComponent: () => import('./features/collections/collection-create/collection-create.component').then(m => m.CollectionCreateComponent) },
      { path: 'collections/:id', loadComponent: () => import('./features/collections/collection-detail/collection-detail.component').then(m => m.CollectionDetailComponent) },
      { path: 'collections/:id/documents/:docId', loadComponent: () => import('./features/documents/document-detail/document-detail.component').then(m => m.DocumentDetailComponent) },
      { path: 'ingest-runs/:id', loadComponent: () => import('./features/ingest-runs/ingest-run-detail/ingest-run-detail.component').then(m => m.IngestRunDetailComponent) },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },
  { path: '**', redirectTo: '' }
];
