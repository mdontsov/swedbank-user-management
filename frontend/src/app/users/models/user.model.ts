export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
}

export type UserInput = Omit<User, 'id'>;

