import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { filter, take } from 'rxjs';
import { UsersActions } from '../../store/users.actions';
import { selectUserById, selectUsersError, selectUsersSaving } from '../../store/users.selectors';

type UserField = 'firstName' | 'lastName' | 'email';

@Component({
  selector: 'app-user-form',
  imports: [AsyncPipe, ReactiveFormsModule, RouterLink],
  templateUrl: './user-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserFormComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly store = inject(Store);
  private readonly destroyRef = inject(DestroyRef);

  readonly userId = this.readUserId();
  readonly isEdit = this.userId !== null;
  readonly saving$ = this.store.select(selectUsersSaving);
  readonly error$ = this.store.select(selectUsersError);

  readonly form = this.formBuilder.nonNullable.group({
    firstName: ['', [Validators.required, Validators.maxLength(100)]],
    lastName: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(320)]]
  });

  ngOnInit(): void {
    if (this.userId === null) {
      return;
    }

    this.store.select(selectUserById(this.userId)).pipe(
      filter((user) => user !== undefined),
      take(1),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe((user) => this.form.setValue({
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email
    }));

    this.store.dispatch(UsersActions.loadUsers());
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const user = this.form.getRawValue();
    if (this.userId === null) {
      this.store.dispatch(UsersActions.createUser({ user }));
    } else {
      this.store.dispatch(UsersActions.updateUser({ id: this.userId, user }));
    }
  }

  remove(): void {
    if (this.userId === null) {
      return;
    }

    const { firstName, lastName } = this.form.getRawValue();
    const name = `${firstName} ${lastName}`.trim() || 'this user';
    if (globalThis.confirm(`Delete ${name}? This action cannot be undone.`)) {
      this.store.dispatch(UsersActions.deleteUser({ id: this.userId }));
    }
  }

  invalid(field: UserField): boolean {
    const control = this.form.controls[field];
    return control.invalid && control.touched;
  }

  private readUserId(): number | null {
    const rawId = this.route.snapshot.paramMap.get('id');
    if (rawId === null) {
      return null;
    }
    const id = Number(rawId);
    return Number.isSafeInteger(id) && id > 0 ? id : null;
  }
}
