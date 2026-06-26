import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { lucideMenu } from '@ng-icons/lucide';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, NgIconComponent],
  providers: [provideIcons({ lucideMenu })],
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
        <nav class="sidebar-nav">
          <!-- Navigation items will go here -->
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
  isSidebarExpanded = signal(true);

  toggleSidebar() {
    this.isSidebarExpanded.update(v => !v);
  }
}
