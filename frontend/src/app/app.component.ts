import { Component } from '@angular/core';
import { NbMenuItem } from '@nebular/theme';

const MENU_ITEMS: NbMenuItem[] = [
  {
    title: 'Dashboard',
    icon: 'pie-chart-outline',
    link: '/dashboard',
    home: true,
    hidden: true,
  },
  {
    title: 'Units',
    icon: 'home-outline',
    link: '/units',
  },
  {
    title: 'Users',
    icon: 'people-outline',
    link: '/users',
  },
  {
    title: 'Agreements',
    icon: 'award-outline',
    link: '/agreements',
  },
  {
    title: 'Accountings',
    icon: 'briefcase-outline',
    link: '/accountings',
  },
];

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  title = 'openzev';

  menu = MENU_ITEMS;
}
