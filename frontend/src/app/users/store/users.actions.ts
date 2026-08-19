import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { User, UserInput } from '../models/user.model';

export const UsersActions = createActionGroup({
  source: 'Users',
  events: {
    'Load Users': emptyProps(),
    'Load Users Success': props<{ users: User[] }>(),
    'Load Users Failure': props<{ error: string }>(),
    'Create User': props<{ user: UserInput }>(),
    'Create User Success': props<{ user: User }>(),
    'Create User Failure': props<{ error: string }>(),
    'Update User': props<{ id: number; user: UserInput }>(),
    'Update User Success': props<{ user: User }>(),
    'Update User Failure': props<{ error: string }>()
  }
});

