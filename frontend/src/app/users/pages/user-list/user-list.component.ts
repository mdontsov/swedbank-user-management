import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { UsersActions } from '../../store/users.actions';
import { selectAllUsers, selectUsersError, selectUsersLoading } from '../../store/users.selectors';

@Component({
  selector: 'app-user-list',
  imports: [AsyncPipe, RouterLink],
  templateUrl: './user-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserListComponent implements OnInit {
  private readonly store = inject(Store);
  readonly users$ = this.store.select(selectAllUsers);
  readonly loading$ = this.store.select(selectUsersLoading);
  readonly error$ = this.store.select(selectUsersError);

  ngOnInit(): void {
    this.store.dispatch(UsersActions.loadUsers());
  }

  retry(): void {
    this.store.dispatch(UsersActions.loadUsers());
  }
}

