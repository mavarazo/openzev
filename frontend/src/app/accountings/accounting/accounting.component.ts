import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import {
  defaultIfEmpty,
  filter,
  forkJoin,
  map,
  Observable,
  of,
  share,
  Subject,
  switchMap,
  takeUntil,
} from 'rxjs';

import { Router } from '@angular/router';
import { saveAs } from 'file-saver';
import { HttpEventType } from '@angular/common/http';
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

export interface CustomInvoiceDto extends InvoiceDto {
  unit?: UnitDto;
}

@Component({
  selector: 'app-accounting',
  templateUrl: './accounting.component.html',
  styleUrls: ['./accounting.component.scss'],
})
export class AccountingComponent implements OnInit, OnDestroy {
  @Input() accountingId: string;

  private destroy$ = new Subject<void>();

  accounting$: Observable<AccountingDto>;
  agreement$: Observable<AgreementDto>;
  documents$: Observable<DocumentDto[]>;
  invoices$: Observable<CustomInvoiceDto[]>;

  isFileUploading = false;
  fileUploadProgress: number = 0;

  constructor(
    private router: Router,
    private accountingService: AccountingService,
    private agreementService: AgreementService,
    private documentService: DocumentService,
    private invoiceService: InvoiceService,
    private unitService: UnitService
  ) {}

  ngOnInit(): void {
    this.accounting$ = this.accountingService
      .getAccounting(this.accountingId)
      .pipe(share(), takeUntil(this.destroy$));

    this.agreement$ = this.accounting$.pipe(
      filter((accounting) => !!accounting.agreementId),
      switchMap((accounting) =>
        this.agreementService.getAgreement(accounting.agreementId!)
      ),
      takeUntil(this.destroy$)
    );

    this.documents$ = this.documentService.getAccountingDocuments(
      this.accountingId
    );

    this.invoices$ = this.invoiceService.getInvoices(this.accountingId).pipe(
      switchMap((invoices: InvoiceDto[]) =>
        forkJoin(
          invoices.map((invoice: InvoiceDto) =>
            this.loadUnitForInvoice(invoice)
          )
        ).pipe(defaultIfEmpty([]))
      ),
      takeUntil(this.destroy$)
    );
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadUnitForInvoice(
    invoice: InvoiceDto
  ): Observable<CustomInvoiceDto> {
    console.log('something');
    if (invoice.unitId) {
      return this.unitService.getUnit(invoice.unitId).pipe(
        map((unit) => ({ ...invoice, unit: unit } as CustomInvoiceDto)),
        takeUntil(this.destroy$)
      );
    }
    return of({ ...invoice } as CustomInvoiceDto);
  }

  delete() {
    this.accountingService
      .deleteAccounting(this.accountingId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        error: console.error,
        complete: () => {
          this.router.navigate(['/accountings']);
        },
      });
  }

  fileBrowseHandler($event: any): void {
    this.fileUpload($event.target.files);
  }

  private fileUpload(fileList: FileList): void {
    if (fileList.length === 1) {
      const file = fileList.item(0)!;
      this.accountingService
        .createAccountingDocument(this.accountingId, file, 'events', true)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (event) => {
            if (event.type === HttpEventType.UploadProgress) {
              this.isFileUploading = true;
              this.fileUploadProgress = Math.round(
                (100 * event.loaded) / event.total!
              );
              console.log(this.fileUploadProgress);
            }
          },
          complete: () => {
            this.isFileUploading = false;
            this.documents$ = this.accountingService.getAccountingDocuments(
              this.accountingId!
            );
          },
        });
    }
  }

  openOrDownloadDocument(document: DocumentDto) {
    if (document.id) {
      if ('application/pdf' === document.mimeType) {
        this.documentService
          .getDocument(document.id)
          .pipe(takeUntil(this.destroy$))
          .subscribe((file: Blob) => {
            const pdfBlob = new Blob([file], { type: document.mimeType });
            const fileURL = URL.createObjectURL(pdfBlob);
            window.open(fileURL, '_blank');
          });
      } else {
        this.downloadDocument(document);
      }
    }
  }

  downloadDocument(document: DocumentDto) {
    if (document.id) {
      this.documentService
        .getDocument(document.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe((file: Blob) => {
          saveAs(file, document.filename ?? document.name);
        });
    }
  }

  deleteDocument(document: DocumentDto) {
    if (document.id) {
      this.documentService
        .deleteDocument(document.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => {
          if (this.accountingId) {
            this.documents$ = this.accountingService.getAccountingDocuments(
              this.accountingId
            );
          }
        });
    }
  }
}
