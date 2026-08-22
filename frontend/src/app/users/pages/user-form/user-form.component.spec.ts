import { ComponentFixture, TestBed } from '@angular/core/testing';
import { convertToParamMap, provideRouter } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { UserFormComponent } from './user-form.component';
import { UsersActions } from '../../store/users.actions';
import { initialUsersState } from '../../store/users.state';

describe('UserFormComponent', () => {
  let fixture: ComponentFixture<UserFormComponent>;
  let component: UserFormComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserFormComponent],
      providers: [
        provideRouter([]),
        provideMockStore({ initialState: { users: initialUsersState } }),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({}) } }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UserFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('rejects empty fields and malformed email', () => {
    expect(component.form.invalid).toBe(true);

    component.form.setValue({ firstName: 'Ada', lastName: 'Lovelace', email: 'invalid' });

    expect(component.form.controls.email.hasError('email')).toBe(true);
    expect(component.form.invalid).toBe(true);
  });

  it('accepts a complete user and renders accessible validation feedback', () => {
    component.submit();
    fixture.detectChanges();

    const feedback = fixture.nativeElement.querySelectorAll('[role="alert"].invalid-feedback');
    const firstName = fixture.nativeElement.querySelector('#firstName');
    expect(feedback.length).toBe(3);
    expect(firstName.getAttribute('aria-describedby')).toBe('firstName-error');

    component.form.setValue({ firstName: 'Ada', lastName: 'Lovelace', email: 'ada@example.com' });
    expect(component.form.valid).toBe(true);
  });
});

describe('UserFormComponent in edit mode', () => {
  let fixture: ComponentFixture<UserFormComponent>;
  let store: MockStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserFormComponent],
      providers: [
        provideRouter([]),
        provideMockStore({
          initialState: {
            users: {
              ...initialUsersState,
              users: [{ id: 1, firstName: 'Ada', lastName: 'Lovelace', email: 'ada@example.com' }]
            }
          }
        }),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '1' }) } }
        }
      ]
    }).compileComponents();

    store = TestBed.inject(MockStore);
    fixture = TestBed.createComponent(UserFormComponent);
    fixture.detectChanges();
  });

  it('shows deletion below the fields and dispatches it after confirmation', () => {
    const deleteButton: HTMLButtonElement = fixture.nativeElement.querySelector('.btn-outline-danger');
    const formFields = fixture.nativeElement.querySelector('.row.g-3');
    expect(deleteButton).not.toBeNull();
    expect(formFields.compareDocumentPosition(deleteButton) & Node.DOCUMENT_POSITION_FOLLOWING).toBeTruthy();

    vi.spyOn(globalThis, 'confirm').mockReturnValue(true);
    const dispatch = vi.spyOn(store, 'dispatch');
    deleteButton.click();

    expect(dispatch).toHaveBeenCalledWith(UsersActions.deleteUser({ id: 1 }));
  });
});
