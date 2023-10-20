import { Component, Input, OnInit } from '@angular/core';
import { AccountingDto } from '../../../generated-source/api';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-accounting-list',
  templateUrl: './accounting-list.component.html',
  styleUrls: ['./accounting-list.component.scss'],
})
export class AccountingListComponent implements OnInit {
  @Input() accountings: AccountingDto[];

  actions: MenuItem[];

  constructor() {}

  ngOnInit(): void {
    this.actions = [{ icon: 'pi pi-plus', routerLink: '/accountings/add' }];
  }
}
