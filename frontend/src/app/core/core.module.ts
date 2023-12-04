import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { BreadcrumbModule } from 'xng-breadcrumb';
import { HeaderComponent } from './components/header/header.component';
import {
  NgbDropdown,
  NgbDropdownItem,
  NgbDropdownMenu,
  NgbDropdownToggle,
  NgbNav,
  NgbNavLinkBase,
} from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  declarations: [SidebarComponent, HeaderComponent],
  exports: [SidebarComponent, HeaderComponent],
  imports: [
    CommonModule,
    RouterModule,
    BreadcrumbModule,
    NgbDropdown,
    NgbDropdownMenu,
    NgbDropdownToggle,
    NgbDropdownItem,
    NgbNav,
    NgbNavLinkBase,
  ],
})
export class CoreModule {}
