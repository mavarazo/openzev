import { Component, Input, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { first } from 'rxjs';
import { BreadcrumbService } from 'xng-breadcrumb';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  items: MenuItem[];
  home: MenuItem | undefined;

  @Input() actions: MenuItem[];

  constructor(private breadcrumbService: BreadcrumbService) {}

  ngOnInit(): void {
    this.home = { icon: 'pi pi-home', routerLink: '/' };
    this.breadcrumbService.breadcrumbs$.pipe(first()).subscribe(
      (breadcrumbs) =>
        (this.items = breadcrumbs.map(
          (b) =>
            ({
              label: b.label,
              routerLink: b.routeLink,
              disabled: b.disable,
            } as MenuItem)
        ))
    );
  }
}
