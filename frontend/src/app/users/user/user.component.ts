import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UserDto, UserService } from '../../../generated-source/api';
import { Router } from '@angular/router';
import { Observable, Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
export class UserComponent implements OnInit, OnDestroy {
  @Input() userId: string;

  private destroy$ = new Subject<void>();

  user$: Observable<UserDto>;

  constructor(private router: Router, private userService: UserService) {}

  ngOnInit(): void {
    this.user$ = this.userService.getUser(this.userId);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  delete() {
    if (this.userId) {
      this.userService
        .deleteUser(this.userId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          error: console.error,
          complete: () => {
            this.router.navigate(['/users']);
          },
        });
    }
  }
}
