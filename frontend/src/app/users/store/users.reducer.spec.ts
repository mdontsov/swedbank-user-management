import { User } from '../models/user.model';
import { UsersActions } from './users.actions';
import { usersReducer } from './users.reducer';
import { initialUsersState } from './users.state';

describe('usersReducer', () => {
  const ada: User = { id: 1, firstName: 'Ada', lastName: 'Lovelace', email: 'ada@example.com' };

  it('stores users after a successful load', () => {
    const loadingState = usersReducer(initialUsersState, UsersActions.loadUsers());
    const state = usersReducer(loadingState, UsersActions.loadUsersSuccess({ users: [ada] }));

    expect(state.users).toEqual([ada]);
    expect(state.loading).toBe(false);
    expect(state.error).toBeNull();
  });

  it('replaces only the updated user', () => {
    const grace: User = { id: 2, firstName: 'Grace', lastName: 'Hopper', email: 'grace@example.com' };
    const updatedAda = { ...ada, email: 'ada.lovelace@example.com' };
    const initial = { ...initialUsersState, users: [ada, grace], saving: true };

    const state = usersReducer(initial, UsersActions.updateUserSuccess({ user: updatedAda }));

    expect(state.users).toEqual([updatedAda, grace]);
    expect(state.saving).toBe(false);
  });

  it('removes only the deleted user', () => {
    const grace: User = { id: 2, firstName: 'Grace', lastName: 'Hopper', email: 'grace@example.com' };
    const initial = { ...initialUsersState, users: [ada, grace], saving: true };

    const state = usersReducer(initial, UsersActions.deleteUserSuccess({ id: ada.id }));

    expect(state.users).toEqual([grace]);
    expect(state.saving).toBe(false);
  });
});
