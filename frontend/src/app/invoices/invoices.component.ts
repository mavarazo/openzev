import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { InvoiceDto, InvoiceService } from '../../generated-source/api';

@Component({
  selector: 'app-invoices',
  templateUrl: './invoices.component.html',
  styleUrls: ['./invoices.component.scss'],
})
export class InvoicesComponent implements OnInit {
  invoices$: Observable<InvoiceDto[]>;

  constructor(private invoiceService: InvoiceService) {}

  ngOnInit(): void {
    this.invoices$ = this.invoiceService.getInvoices();
  }
}
