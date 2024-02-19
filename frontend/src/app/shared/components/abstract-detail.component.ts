import {
  Component,
  OnDestroy,
  OnInit,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { Observable, share, Subject, takeUntil } from 'rxjs';

@Component({
  template: '',
})
export abstract class AbstractDetailComponent<T> implements OnInit, OnDestroy {
  @ViewChild('content') content: TemplateRef<any>;

  protected destroy$ = new Subject<void>();

  entity$: Observable<T>;

  abstract fetchEntity(): Observable<T>;

  ngOnInit(): void {
    this.entity$ = this.fetchEntity().pipe(share(), takeUntil(this.destroy$));
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  abstract deleteEntity(): Observable<any>;

  abstract onSuccessfullDelete(): void;

  delete(): void {
    this.deleteEntity()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        error: console.error,
        complete: () => {
          this.onSuccessfullDelete();
        },
      });
  }
}
