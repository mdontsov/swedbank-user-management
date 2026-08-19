import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  template: `
    <section class="card border-0 shadow-sm mx-auto page-card">
      <div class="card-body p-4 p-md-5 text-center">
        <h1 class="display-6 fw-semibold mb-3">Manage users</h1>
        <p class="text-body-secondary mb-4">Create a new user or view and edit registered users.</p>
        <div class="row g-3 justify-content-center">
          <div class="col-12 col-sm-6">
            <a class="btn btn-primary btn-lg w-100" routerLink="/users/new">Create User</a>
          </div>
          <div class="col-12 col-sm-6">
            <a class="btn btn-outline-primary btn-lg w-100" routerLink="/users">Show User List</a>
          </div>
        </div>
      </div>
    </section>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HomeComponent {}

