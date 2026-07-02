import { Component, inject, signal, computed } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { lucideMenu, lucideLayoutDashboard, lucideSettings, lucideLayers } from '@ng-icons/lucide';
import { AuthService } from '../../core/auth/auth.service';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, NgIconComponent],
  providers: [provideIcons({ lucideMenu, lucideLayoutDashboard, lucideSettings, lucideLayers })],
  template: `
    <div class="shell-container" [class.sidebar-collapsed]="!isSidebarExpanded()">
      <aside class="sidebar">
        <div class="sidebar-header">
          @if (isSidebarExpanded()) {
            <div class="logo">Violet Lab</div>
          } @else {
            <div class="logo-collapsed">VL</div>
          }
        </div>
        
        @if (isSidebarExpanded()) {
          <div class="workspace-selector">
            <span class="workspace-label">WORKSPACE</span>
            <div class="workspace-name">{{ activeWorkspaceName() }}</div>
          </div>
        }

        <nav class="sidebar-nav">
          <a routerLink="/dashboard" routerLinkActive="active" class="nav-item">
            <ng-icon name="lucideLayoutDashboard"></ng-icon>
            @if (isSidebarExpanded()) {
              <span>Dashboard</span>
            }
          </a>
          <a routerLink="/collections" routerLinkActive="active" class="nav-item">
            <ng-icon name="lucideLayers"></ng-icon>
            @if (isSidebarExpanded()) {
              <span>Coleções</span>
            }
          </a>
          <a routerLink="/settings" routerLinkActive="active" class="nav-item">
            <ng-icon name="lucideSettings"></ng-icon>
            @if (isSidebarExpanded()) {
              <span>Configurações</span>
            }
          </a>
        </nav>
      </aside>
      
      <main class="main-content">
        <header class="top-header">
          <button class="icon-button" (click)="toggleSidebar()" aria-label="Toggle Sidebar">
            <ng-icon name="lucideMenu" size="20"></ng-icon>
          </button>
        </header>
        <div class="page-content">
          <router-outlet></router-outlet>
        </div>
      </main>
    </div>
  `,
  styleUrls: ['./shell.component.css']
})
export class ShellComponent {
  private auth = inject(AuthService);
  
  isSidebarExpanded = signal(true);
  authState = toSignal(this.auth.authState$);

  activeWorkspaceName = computed(() => {
    const state = this.authState();
    if (!state || !state.activeWorkspaceId) return 'Carregando...';
    const workspace = state.workspaces.find(w => w.id === state.activeWorkspaceId);
    return workspace ? workspace.name : 'Desconhecido';
  });

  toggleSidebar() {
    this.isSidebarExpanded.update(v => !v);
  }
}
