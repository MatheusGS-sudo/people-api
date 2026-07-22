import {
  computed,
  inject,
  Injectable,
  signal
} from '@angular/core';

import {
  HttpClient,
  HttpHeaders
} from '@angular/common/http';

import {
  Observable,
  tap
} from 'rxjs';

interface AuthResponse {
  username: string;
  message: string;
}

interface Credentials {
  username: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly http = inject(HttpClient);

  private readonly baseUrl = 'http://localhost:8080';

  private credentials: Credentials | null = null;

  readonly currentUsername = signal<string | null>(null);

  readonly isAuthenticated = computed(
    () => this.currentUsername() !== null
  );

  login(
    username: string,
    password: string
  ): Observable<AuthResponse> {

    const headers = this.createHeaders(
      username,
      password
    );

    return this.http
      .get<AuthResponse>(
        `${this.baseUrl}/auth/check`,
        { headers }
      )
      .pipe(
        tap(response => {

          this.credentials = {
            username,
            password
          };

          this.currentUsername.set(
            response.username
          );
        })
      );
  }

  logout(): void {
    this.credentials = null;
    this.currentUsername.set(null);
  }

  getAuthorizationHeaders(): HttpHeaders {

    if (!this.credentials) {
      return new HttpHeaders();
    }

    return this.createHeaders(
      this.credentials.username,
      this.credentials.password
    );
  }

  private createHeaders(
    username: string,
    password: string
  ): HttpHeaders {

    const encodedCredentials = btoa(
      `${username}:${password}`
    );

    return new HttpHeaders({
      Authorization: `Basic ${encodedCredentials}`
    });
  }
}