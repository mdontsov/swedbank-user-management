import { User } from '../models/user.model';

export const usersFeatureKey = 'users';

export interface UsersState {
  users: User[];
  loading: boolean;
  saving: boolean;
  error: string | null;
}

export const initialUsersState: UsersState = {
  users: [],
  loading: false,
  saving: false,
  error: null
};

