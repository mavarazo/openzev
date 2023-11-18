import { Component } from '@angular/core';
import { NavItem } from './core/components/sidebar/sidebar.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  items: NavItem[];

  ngOnInit(): void {
    this.items = [
      {
        label: 'Dashboard',
        icon: 'bi bi-clipboard-data',
        link: '/dashboard',
      },
      {
        label: 'Accountings',
        icon: 'bi bi-briefcase',
        link: '/accountings',
      },
      {
        label: 'Agreements',
        icon: 'bi bi-patch-check',
        link: '/agreements',
      },
      {
        label: 'Owners',
        icon: 'bi bi-people',
        link: '/owners',
      },
      {
        label: 'Properties',
        icon: 'bi bi-houses',
        link: '/properties',
      },
    ];
  }
}
