import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterLink, RouterOutlet],
  template: `
    <header class="navbar navbar-dark bg-primary shadow-sm">
      <div class="container">
        <a class="navbar-brand fw-semibold" routerLink="/">User Management</a>
      </div>
    </header>
    <main class="container py-4 py-md-5">
      <router-outlet />
    </main>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {}

