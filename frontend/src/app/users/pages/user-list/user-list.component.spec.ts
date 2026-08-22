import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { UserListComponent } from './user-list.component';
import { initialUsersState } from '../../store/users.state';

describe('UserListComponent', () => {
  let fixture: ComponentFixture<UserListComponent>;
  let store: MockStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserListComponent],
      providers: [
        provideRouter([]),
        provideMockStore({
          initialState: {
            users: {
              ...initialUsersState,
              users: [{ id: 1, firstName: 'Ada', lastName: 'Lovelace', email: 'ada@example.com' }]
            }
          }
        })
      ]
    }).compileComponents();

    store = TestBed.inject(MockStore);
    fixture = TestBed.createComponent(UserListComponent);
    fixture.detectChanges();
    store.refreshState();
    fixture.detectChanges();
  });

  it('wraps the user table for responsive overflow', () => {
    expect(fixture.nativeElement.querySelector('.table-responsive table')).not.toBeNull();
  });
});
