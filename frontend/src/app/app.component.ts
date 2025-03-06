import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from './core/sidebar/sidebar.component';
import { NavItem } from './core/sidebar/nav-item.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  items: NavItem[] = [
    {
      label: 'Meter Points',
      icon: 'bi bi-clipboard-data',
      link: '/meterPoint',
    },
  ];
}
