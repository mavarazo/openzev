import { ChangeDetectionStrategy, Component, inject } from '@angular/core'
import { ButtonComponent } from '../../../../shared/components/button/button.component'
import { VendorInvoice } from '../../../../../generated-source/api'
import { VendorInvoicesStore } from '../../store/vendor-invoices.store'
import { ActivatedRoute, Router, RouterLink } from '@angular/router'
import { DatePipe } from '@angular/common'
import { FaIconComponent } from '@fortawesome/angular-fontawesome'
import { faPencil, faTrash } from '@fortawesome/free-solid-svg-icons'

@Component({
    selector: 'app-overview',
    imports: [ButtonComponent, DatePipe, FaIconComponent, RouterLink],
    providers: [VendorInvoicesStore],
    templateUrl: './overview.component.html',
    styleUrl: './overview.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverviewComponent {
    private readonly store = inject(VendorInvoicesStore)
    private readonly router = inject(Router)
    private readonly activeRoute = inject(ActivatedRoute)

    readonly vendorInvoices = this.store.vendorInvoices

    protected readonly faTrash = faTrash
    protected readonly faPencil = faPencil

    addClicked() {
        this.router.navigate(['add'], { relativeTo: this.activeRoute }).then()
    }

    remove(vendorInvoice: VendorInvoice) {
        this.store.remove(vendorInvoice)
    }
}
