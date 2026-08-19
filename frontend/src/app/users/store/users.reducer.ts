import { createReducer, on } from '@ngrx/store';
import { UsersActions } from './users.actions';
import { initialUsersState } from './users.state';

export const usersReducer = createReducer(
  initialUsersState,
  on(UsersActions.loadUsers, (state) => ({ ...state, loading: true, error: null })),
  on(UsersActions.loadUsersSuccess, (state, { users }) => ({
    ...state,
    users,
    loading: false
  })),
  on(UsersActions.loadUsersFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(UsersActions.createUser, UsersActions.updateUser, (state) => ({
    ...state,
    saving: true,
    error: null
  })),
  on(UsersActions.createUserSuccess, (state, { user }) => ({
    ...state,
    users: [...state.users, user],
    saving: false
  })),
  on(UsersActions.updateUserSuccess, (state, { user }) => ({
    ...state,
    users: state.users.map((existing) => existing.id === user.id ? user : existing),
    saving: false
  })),
  on(UsersActions.createUserFailure, UsersActions.updateUserFailure, (state, { error }) => ({
    ...state,
    saving: false,
    error
  }))
);

