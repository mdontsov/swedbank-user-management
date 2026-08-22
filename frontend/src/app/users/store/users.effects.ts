import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap, tap } from 'rxjs';
import { UserApiService } from '../../core/api/user-api.service';
import { UsersActions } from './users.actions';

@Injectable()
export class UsersEffects {
  private readonly actions$ = inject(Actions);
  private readonly api = inject(UserApiService);
  private readonly router = inject(Router);

  readonly loadUsers$ = createEffect(() => this.actions$.pipe(
    ofType(UsersActions.loadUsers),
    switchMap(() => this.api.getAll().pipe(
      map((users) => UsersActions.loadUsersSuccess({ users })),
      catchError((error: unknown) => of(UsersActions.loadUsersFailure({ error: this.message(error) })))
    ))
  ));

  readonly createUser$ = createEffect(() => this.actions$.pipe(
    ofType(UsersActions.createUser),
    switchMap(({ user }) => this.api.create(user).pipe(
      map((created) => UsersActions.createUserSuccess({ user: created })),
      catchError((error: unknown) => of(UsersActions.createUserFailure({ error: this.message(error) })))
    ))
  ));

  readonly updateUser$ = createEffect(() => this.actions$.pipe(
    ofType(UsersActions.updateUser),
    switchMap(({ id, user }) => this.api.update(id, user).pipe(
      map((updated) => UsersActions.updateUserSuccess({ user: updated })),
      catchError((error: unknown) => of(UsersActions.updateUserFailure({ error: this.message(error) })))
    ))
  ));

  readonly deleteUser$ = createEffect(() => this.actions$.pipe(
    ofType(UsersActions.deleteUser),
    switchMap(({ id }) => this.api.delete(id).pipe(
      map(() => UsersActions.deleteUserSuccess({ id })),
      catchError((error: unknown) => of(UsersActions.deleteUserFailure({ error: this.message(error) })))
    ))
  ));

  readonly returnToList$ = createEffect(() => this.actions$.pipe(
    ofType(UsersActions.createUserSuccess, UsersActions.updateUserSuccess, UsersActions.deleteUserSuccess),
    tap(() => void this.router.navigate(['/users']))
  ), { dispatch: false });

  private message(error: unknown): string {
    if (typeof error === 'object' && error !== null && 'error' in error) {
      const response = (error as { error?: { error?: string } }).error;
      if (response?.error) {
        return response.error;
      }
    }
    return 'Unable to complete the request. Please try again.';
  }
}
