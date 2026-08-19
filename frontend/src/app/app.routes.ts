import { Routes } from '@angular/router';
import { HomeComponent } from './users/pages/home/home.component';
import { UserFormComponent } from './users/pages/user-form/user-form.component';
import { UserListComponent } from './users/pages/user-list/user-list.component';

export const routes: Routes = [
  { path: '', component: HomeComponent, title: 'Home | User Management' },
  { path: 'users', component: UserListComponent, title: 'Users | User Management' },
  { path: 'users/new', component: UserFormComponent, title: 'Create User | User Management' },
  { path: 'users/:id/edit', component: UserFormComponent, title: 'Edit User | User Management' },
  { path: '**', redirectTo: '' }
];

