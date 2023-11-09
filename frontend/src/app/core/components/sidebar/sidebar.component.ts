import { Component, Input } from '@angular/core';

export interface NavItem {
  label: string;
  link: string;
  icon?: string;
}

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent {
  @Input() items: NavItem[];
}
