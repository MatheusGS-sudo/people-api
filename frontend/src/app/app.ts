import { Component, computed, inject, OnInit, signal } from '@angular/core';

import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { HttpErrorResponse } from '@angular/common/http';

import { finalize } from 'rxjs';

import { ApiError, NationalityResponse, Person } from './models/person.model';

import { AuthService } from './services/auth.service';

import { PersonService } from './services/person.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  private readonly formBuilder = inject(FormBuilder);

  private readonly personService = inject(PersonService);

  readonly authService = inject(AuthService);

  readonly people = signal<Person[]>([]);

  readonly searchTerm = signal('');

  readonly selectedNationality = signal<NationalityResponse | null>(null);

  readonly pendingDeletePerson = signal<Person | null>(null);

  readonly showLoginModal = signal(false);

  readonly showDeleteModal = signal(false);

  readonly isLoadingList = signal(false);

  readonly isSubmitting = signal(false);

  readonly isLoggingIn = signal(false);

  readonly activePersonId = signal<number | null>(null);

  readonly successMessage = signal('');

  readonly errorMessage = signal('');

  readonly filteredPeople = computed(() => {
    const search = this.searchTerm().trim().toLowerCase();

    if (!search) {
      return this.people();
    }

    return this.people().filter((person) => {
      const fullText = [person.id, person.documento, person.nome, person.sobrenome, person.email]
        .join(' ')
        .toLowerCase();

      return fullText.includes(search);
    });
  });

  readonly personForm = this.formBuilder.nonNullable.group({
    documento: ['', [Validators.required, Validators.pattern(/^[0-9]{11}$/)]],

    nome: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],

    sobrenome: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],

    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
  });

  readonly loginForm = this.formBuilder.nonNullable.group({
    username: ['admin', [Validators.required]],

    password: ['', [Validators.required]],
  });

  ngOnInit(): void {
    this.loadPeople();
  }

  registerPerson(): void {
    this.clearMessages();

    if (this.personForm.invalid) {
      this.personForm.markAllAsTouched();

      this.errorMessage.set('Preencha corretamente os campos do cadastro.');

      return;
    }

    this.isSubmitting.set(true);

    this.personService
      .register(this.personForm.getRawValue())
      .pipe(
        finalize(() => {
          this.isSubmitting.set(false);
        }),
      )
      .subscribe({
        next: (person) => {
          this.people.update((current) => [...current, person]);

          this.personForm.reset();

          this.successMessage.set(`${person.nome} foi cadastrado com sucesso.`);
        },

        error: (error) => {
          this.handleError(error);
        },
      });
  }

  loadPeople(): void {
    this.isLoadingList.set(true);

    this.personService
      .list('id')
      .pipe(
        finalize(() => {
          this.isLoadingList.set(false);
        }),
      )
      .subscribe({
        next: (people) => {
          this.people.set(people);
        },

        error: (error) => {
          this.handleError(error);
        },
      });
  }

  findNationality(person: Person): void {
    this.clearMessages();

    this.activePersonId.set(person.id);

    this.personService
      .findNationality(person.id)
      .pipe(
        finalize(() => {
          this.activePersonId.set(null);
        }),
      )
      .subscribe({
        next: (nationality) => {
          this.selectedNationality.set(nationality);
        },

        error: (error) => {
          this.handleError(error);
        },
      });
  }

  requestDelete(person: Person): void {
    this.clearMessages();

    this.pendingDeletePerson.set(person);

    if (!this.authService.isAuthenticated()) {
      this.showLoginModal.set(true);

      return;
    }

    this.showDeleteModal.set(true);
  }

  login(): void {
    this.clearMessages();

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();

      this.errorMessage.set('Informe o usuário e a senha.');

      return;
    }

    this.isLoggingIn.set(true);

    const credentials = this.loginForm.getRawValue();

    this.authService
      .login(credentials.username, credentials.password)
      .pipe(
        finalize(() => {
          this.isLoggingIn.set(false);
        }),
      )
      .subscribe({
        next: (response) => {
          this.showLoginModal.set(false);

          this.loginForm.controls.password.reset();

          this.successMessage.set(`Usuário ${response.username} autenticado.`);

          if (this.pendingDeletePerson()) {
            this.showDeleteModal.set(true);
          }
        },

        error: (error) => {
          this.handleError(error);
        },
      });
  }

  logout(): void {
    this.authService.logout();

    this.showDeleteModal.set(false);

    this.pendingDeletePerson.set(null);

    this.successMessage.set('Sessão encerrada.');
  }

  openLogin(): void {
    this.clearMessages();

    this.pendingDeletePerson.set(null);

    this.showLoginModal.set(true);
  }

  closeLogin(): void {
    this.showLoginModal.set(false);

    this.pendingDeletePerson.set(null);

    this.loginForm.controls.password.reset();
  }

  updateSearch(event: Event): void {
    const input = event.target as HTMLInputElement;

    this.searchTerm.set(input.value);
  }

  scrollTo(sectionId: string): void {
    document.getElementById(sectionId)?.scrollIntoView({
      behavior: 'smooth',
      block: 'start',
    });
  }

  formatProbability(probability: number | null | undefined): string {
    if (probability === null || probability === undefined) {
      return 'Não informada';
    }

    return `${(probability * 100).toFixed(2)}%`;
  }
  cancelDelete(): void {
    this.showDeleteModal.set(false);

    this.pendingDeletePerson.set(null);
  }

  confirmDelete(): void {
    const person = this.pendingDeletePerson();

    if (!person) {
      return;
    }

    this.clearMessages();

    this.activePersonId.set(person.id);

    this.personService
      .delete(person.id)
      .pipe(
        finalize(() => {
          this.activePersonId.set(null);
        }),
      )
      .subscribe({
        next: () => {
          this.people.update((current) => current.filter((item) => item.id !== person.id));

          if (this.selectedNationality()?.pessoaId === person.id) {
            this.selectedNationality.set(null);
          }

          this.showDeleteModal.set(false);

          this.pendingDeletePerson.set(null);

          this.successMessage.set(`${person.nome} ${person.sobrenome} foi excluído com sucesso.`);
        },

        error: (error) => {
          this.handleError(error);
        },
      });
  }
  private clearMessages(): void {
    this.successMessage.set('');
    this.errorMessage.set('');
  }

  private handleError(error: unknown): void {
    if (!(error instanceof HttpErrorResponse)) {
      this.errorMessage.set('Ocorreu um erro inesperado.');

      return;
    }

    if (error.status === 0) {
      this.errorMessage.set(
        'O navegador não conseguiu acessar o backend. Verifique a URL da API e o CORS.',
      );

      return;
    }

    if (error.status === 401) {
      this.errorMessage.set('Usuário ou senha inválidos.');

      return;
    }

    const apiError = error.error as ApiError | undefined;

    const validationMessages = apiError?.validationErrors
      ? Object.values(apiError.validationErrors)
      : [];

    if (validationMessages.length > 0) {
      this.errorMessage.set(validationMessages.join(' '));

      return;
    }

    this.errorMessage.set(apiError?.message ?? `Erro HTTP ${error.status}.`);
  }
}
