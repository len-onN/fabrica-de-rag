import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, catchError, of } from 'rxjs';

export interface BootstrapRequest {
  email?: string;
  password?: string;
  displayName?: string;
  workspaceName?: string;
  workspacePurpose?: string;
  analytics?: {
    enabled: boolean;
    retentionDays: number;
  };
}

export interface AuthMeResponse {
  user: { id: string, displayName: string, email: string };
  workspaces: Array<{ id: string, name: string, role: string, capabilities: string[] }>;
  activeWorkspaceId: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = '/api/v1';
  
  private authState = new BehaviorSubject<AuthMeResponse | null>(null);
  authState$ = this.authState.asObservable();

  bootstrap(request: BootstrapRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/bootstrap`, request);
  }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/login`, credentials);
  }

  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/logout`, {}).pipe(
      tap(() => this.authState.next(null))
    );
  }

  me(): Observable<AuthMeResponse | null> {
    return this.http.get<AuthMeResponse>(`${this.apiUrl}/me`).pipe(
      tap(res => this.authState.next(res)),
      catchError(() => {
        this.authState.next(null);
        return of(null);
      })
    );
  }
}
