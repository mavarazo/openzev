import { Component, OnInit } from '@angular/core';
import { NavItem } from './core/components/sidebar/sidebar.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  items: NavItem[];
  settingsItems: NavItem[];

  ngOnInit(): void {
    this.items = [
      {
        label: 'Dashboard',
        icon: 'bi bi-clipboard-data',
        link: '/dashboard',
      },
      {
        label: 'Invoices',
        icon: 'bi bi-briefcase',
        link: '/invoices',
      },
      {
        label: 'Owners',
        icon: 'bi bi-people',
        link: '/owners',
      },
      {
        label: 'Products',
        icon: 'bi bi-boxes',
        link: '/products',
      },
      {
        label: 'Units',
        icon: 'bi bi-houses',
        link: '/units',
      },
    ];

    this.settingsItems = [
      {
        label: 'Basics',
        icon: 'bi bi-houses',
        link: '/settings',
      },
      {
        label: 'Bank Accounts',
        icon: 'bi bi-houses',
        link: '/bank-accounts',
      },
      {
        label: 'Representatives',
        icon: 'bi bi-houses',
        link: '/representatives',
      },
    ];
  }
}
