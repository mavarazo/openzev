import {
    ChangeDetectionStrategy,
    Component,
    effect,
    inject,
    input,
} from '@angular/core'
import { VendorInvoicesStore } from '../../store/vendor-invoices.store'
import { DatePipe } from '@angular/common'

@Component({
    selector: 'app-detail',
    imports: [DatePipe],
    providers: [VendorInvoicesStore],
    templateUrl: './detail.component.html',
    styleUrl: './detail.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailComponent {
    readonly vendorInvoiceId = input<string>()
    private readonly store = inject(VendorInvoicesStore)

    readonly vendorInvoice = this.store.vendorInvoice

    constructor() {
        effect(() => {
            const id = this.vendorInvoiceId()
            if (id) {
                this.store.get(id)
            }
        })
    }
}
