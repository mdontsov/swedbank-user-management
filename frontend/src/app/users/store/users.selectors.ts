import { createFeatureSelector, createSelector } from '@ngrx/store';
import { UsersState, usersFeatureKey } from './users.state';

export const selectUsersState = createFeatureSelector<UsersState>(usersFeatureKey);
export const selectAllUsers = createSelector(selectUsersState, (state) => state.users);
export const selectUsersLoading = createSelector(selectUsersState, (state) => state.loading);
export const selectUsersSaving = createSelector(selectUsersState, (state) => state.saving);
export const selectUsersError = createSelector(selectUsersState, (state) => state.error);
export const selectUserById = (id: number) =>
  createSelector(selectAllUsers, (users) => users.find((user) => user.id === id));

