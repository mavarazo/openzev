import { Component, OnInit } from '@angular/core';
import { UserDto, UserService } from '../../../generated-source/api';
import { ActivatedRoute, Router } from '@angular/router';
import { first, Observable } from 'rxjs';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
export class UserComponent implements OnInit {
  id: string | null;
  user$: Observable<UserDto>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');

    if (this.id) {
      this.user$ = this.userService.getUser(this.id);
    }
  }

  delete() {
    if (this.id) {
      this.userService
        .deleteUser(this.id)
        .pipe(first())
        .subscribe({
          error: console.error,
          complete: () => {
            this.router.navigate(['/users']);
          },
        });
    }
  }
}
