import {
  inject,
  Injectable
} from '@angular/core';

import {
  HttpClient,
  HttpParams
} from '@angular/common/http';

import {
  Observable
} from 'rxjs';

import {
  NationalityResponse,
  Person,
  PersonRequest
} from '../models/person.model';

import {
  AuthService
} from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class PersonService {

  private readonly http = inject(HttpClient);

  private readonly authService = inject(AuthService);

  private readonly baseUrl = 'http://localhost:8080';

  register(
    person: PersonRequest
  ): Observable<Person> {

    return this.http.post<Person>(
      `${this.baseUrl}/registrarName`,
      person
    );
  }

  list(
    orderBy: string = 'id'
  ): Observable<Person[]> {

    const params = new HttpParams()
      .set('ordenarPor', orderBy);

    return this.http.get<Person[]>(
      `${this.baseUrl}/list`,
      { params }
    );
  }

  findById(
    id: number
  ): Observable<Person> {

    return this.http.get<Person>(
      `${this.baseUrl}/list/${id}`
    );
  }

  findNationality(
    personId: number
  ): Observable<NationalityResponse> {

    return this.http.get<NationalityResponse>(
      `${this.baseUrl}/findNacionalityByPerson/${personId}`
    );
  }

  delete(
    personId: number
  ): Observable<void> {

    const headers =
      this.authService.getAuthorizationHeaders();

    return this.http.delete<void>(
      `${this.baseUrl}/list/${personId}`,
      { headers }
    );
  }
}