import { Component, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject, Observable, Subject, takeUntil } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { ErrorDto } from '../../../generated-source/api';

@Component({
  template: '',
})
export abstract class AbstractAddEditComponent<T, MODIFIABLE>
  implements OnInit, OnDestroy
{
  protected destroy$ = new Subject<void>();
  protected error$ = new BehaviorSubject<ErrorDto | undefined>(undefined);

  form: FormGroup;
  isSubmitted: boolean = false;

  get error(): Observable<ErrorDto | undefined> {
    return this.error$;
  }

  abstract get isEdit(): boolean;

  abstract initForm(): FormGroup;

  abstract fetchEntity(): Observable<T>;

  ngOnInit(): void {
    this.form = this.initForm();

    if (this.isEdit) {
      this.fetchEntity()
        .pipe(takeUntil(this.destroy$))
        .subscribe((item: any) => this.form.patchValue(item));
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  submit() {
    this.isSubmitted = true;

    if (this.form.valid) {
      const item = {
        ...this.form.value,
      } as MODIFIABLE;

      if (this.isEdit) {
        this.edit(item);
      } else {
        this.add(item);
      }
    }
  }

  abstract createEntity(entity: MODIFIABLE): Observable<any>;

  abstract onSuccessfullCreate(result: any): void;

  private add(entity: MODIFIABLE) {
    this.createEntity(entity)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.reset();
          this.onSuccessfullCreate(result);
        },
        error: (error) => {
          console.error(error);
          this.emitError(error);
        },
      });
  }

  abstract changeEntity(entity: MODIFIABLE): Observable<any>;

  abstract onSuccessfullChange(result: any): void;

  private edit(entity: MODIFIABLE) {
    this.changeEntity(entity)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          this.reset();
          this.onSuccessfullChange(result);
        },
        error: (error) => {
          console.error(error);
          this.emitError(error);
        },
      });
  }

  reset() {
    this.isSubmitted = false;
    this.form.reset();
  }

  private emitError(error: ErrorDto) {
    if (error && error.code) {
      this.error$.next(error);
    }
  }
}
