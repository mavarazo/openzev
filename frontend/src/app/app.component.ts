import { Component } from '@angular/core';
import { MenuItem, PrimeNGConfig } from 'primeng/api';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  items: MenuItem[];

  constructor(private primengConfig: PrimeNGConfig) {}

  ngOnInit(): void {
    this.primengConfig.ripple = true;

    this.items = [
      {
        label: 'Dashboard',
        icon: 'pi pi-sliders-v',
        routerLink: '/dashboard',
      },
      {
        label: 'Accountings',
        icon: 'pi pi-briefcase',
        routerLink: '/accountings',
      },
      {
        label: 'Agreements',
        icon: 'pi pi-verified',
        routerLink: '/agreements',
      },
      {
        label: 'Units',
        icon: 'pi pi-home',
        routerLink: '/units',
      },
      {
        label: 'Users',
        icon: 'pi pi-users',
        routerLink: '/users',
      },
    ];
  }
}
