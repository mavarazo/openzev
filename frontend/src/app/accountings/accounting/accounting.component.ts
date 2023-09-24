import { Component, OnInit } from '@angular/core';
import {
  catchError,
  defaultIfEmpty,
  EMPTY,
  first,
  forkJoin,
  last,
  map,
  Observable,
  of,
  switchMap,
  tap,
} from 'rxjs';
import {
  AccountingDto,
  AccountingService,
  AgreementDto,
  AgreementService,
  DocumentDto,
  DocumentService,
  InvoiceDto,
  InvoiceService,
  UnitDto,
  UnitService,
} from '../../../generated-source/api';
import { ActivatedRoute, Router } from '@angular/router';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpEventType,
} from '@angular/common/http';
import { saveAs } from 'file-saver';

export interface CustomInvoiceDto extends InvoiceDto {
  unit?: UnitDto;
}

@Component({
  selector: 'app-accounting',
  templateUrl: './accounting.component.html',
  styleUrls: ['./accounting.component.scss'],
})
export class AccountingComponent implements OnInit {
  id: string | null;
  accounting$: Observable<AccountingDto>;
  agreement$: Observable<AgreementDto>;
  documents$: Observable<DocumentDto[]>;
  invoices$: Observable<CustomInvoiceDto[]>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private accountingService: AccountingService,
    private agreementService: AgreementService,
    private documentService: DocumentService,
    private invoiceService: InvoiceService,
    private unitService: UnitService
  ) {}

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');

    if (this.id) {
      this.accounting$ = this.accountingService.getAccounting(this.id);
      this.agreement$ = this.accounting$.pipe(
        switchMap((a: AccountingDto) => {
          if (a.agreementId) {
            return this.agreementService.getAgreement(a.agreementId);
          }
          return EMPTY;
        })
      );

      this.documents$ = this.accountingService.getAccountingDocuments(this.id);

      this.invoices$ = this.invoiceService
        .getInvoices(this.id)
        .pipe(
          switchMap((invoices: InvoiceDto[]) =>
            forkJoin(
              invoices.map((invoice: InvoiceDto) =>
                this.loadUnitForInvoice(invoice)
              )
            ).pipe(defaultIfEmpty([]))
          )
        );
    }
  }

  private loadUnitForInvoice(
    invoice: InvoiceDto
  ): Observable<CustomInvoiceDto> {
    if (invoice.unitId) {
      return this.unitService
        .getUnit(invoice.unitId)
        .pipe(map((unit) => ({ ...invoice, unit: unit } as CustomInvoiceDto)));
    }
    return of({ ...invoice } as CustomInvoiceDto);
  }

  delete() {
    if (this.id) {
      this.accountingService
        .deleteAccounting(this.id)
        .pipe(first())
        .subscribe({
          error: console.error,
          complete: () => {
            this.router.navigate(['/accountings']);
          },
        });
    }
  }

  fileBrowseHandler($event: any): void {
    this.fileUpload($event.target.files);
  }

  private fileUpload(fileList: FileList): void {
    if (this.id && fileList.length === 1) {
      const file = fileList.item(0)!;
      this.accountingService
        .createAccountingDocument(this.id, file, 'events', true)
        .pipe(
          map((event) => this.getEventMessage(event, file)),
          tap((message) => this.showProgress(message)),
          last(), // return last (completed) message to caller
          catchError(this.handleError(file))
        )
        .subscribe(() => {
          if (this.id) {
            this.documents$ = this.accountingService.getAccountingDocuments(
              this.id
            );
          }
        });
    }
  }

  private getEventMessage(event: HttpEvent<any>, file: File) {
    console.log(event);

    switch (event.type) {
      case HttpEventType.Sent:
        return `Uploading file "${file.name}" of size ${file.size}.`;

      case HttpEventType.UploadProgress:
        // Compute and show the % done:
        const percentDone = event.total
          ? Math.round((100 * event.loaded) / event.total)
          : 0;
        return `File "${file.name}" is ${percentDone}% uploaded.`;

      case HttpEventType.Response:
        return `File "${file.name}" was completely uploaded!`;

      default:
        return `File "${file.name}" surprising upload event: ${event.type}.`;
    }
  }

  private handleError(file: File) {
    const userMessage = `${file.name} upload failed.`;

    return (error: HttpErrorResponse) => {
      const message =
        error.error instanceof Error
          ? error.error.message
          : `server returned code ${error.status} with body "${error.error}"`;
      console.error(message); // log to console instead
      return of(userMessage);
    };
  }

  private showProgress(message: string) {
    console.log(message);
  }

  downloadDocument(document: DocumentDto) {
    if (document.id) {
      this.documentService
        .getDocument(document.id)
        .pipe(first())
        .subscribe((file: Blob) => {
          saveAs(file, document.filename ?? document.name);
        });
    }
  }

  deleteDocument(document: DocumentDto) {
    if (document.id) {
      this.documentService
        .deleteDocument(document.id)
        .pipe(first())
        .subscribe(() => {
          if (this.id) {
            this.documents$ = this.accountingService.getAccountingDocuments(
              this.id
            );
          }
        });
    }
  }
}
