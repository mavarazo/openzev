import {
    ChangeDetectionStrategy,
    Component,
    effect,
    inject,
    input,
} from '@angular/core'
import { VendorInvoicesStore } from '../../store/vendor-invoices.store'
import {
    FormControl,
    FormGroup,
    ReactiveFormsModule,
    Validators,
} from '@angular/forms'
import { VendorInvoice } from '../../../../../generated-source/api'
import { ButtonComponent } from '../../../../shared/components/button/button.component'

@Component({
    selector: 'app-add-edit',
    imports: [ButtonComponent, ReactiveFormsModule],
    providers: [VendorInvoicesStore],
    templateUrl: './add-edit.component.html',
    styleUrl: './add-edit.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddEditComponent {
    readonly vendorInvoiceId = input<string>()
    private readonly store = inject(VendorInvoicesStore)

    private readonly vendorInvoice = this.store.vendorInvoice

    private formSubmitAttempt: boolean = false
    isEditMode: boolean = false

    form = new FormGroup({
        date: new FormControl<Date | undefined>(undefined, Validators.required),
        periodFrom: new FormControl<Date | undefined>(undefined),
        periodUpto: new FormControl<Date | undefined>(undefined),
        dueDate: new FormControl<Date | undefined>(
            undefined,
            Validators.required
        ),
    })

    constructor() {
        effect(() => {
            const id = this.vendorInvoiceId()
            if (id) {
                this.isEditMode = true
                this.store.get(id)
            }
        })

        effect(() => {
            const vendorInvoice = this.vendorInvoice()
            if (vendorInvoice != undefined) {
                this.form.patchValue(vendorInvoice)
            }
        })
    }

    submit(): void {
        this.formSubmitAttempt = true

        if (this.form.invalid) {
            return
        }

        const id = this.vendorInvoiceId()
        const vendorInvoice: VendorInvoice = Object.assign(this.form.value)
        console.log(JSON.stringify(vendorInvoice))
        if (this.isEditMode && id) {
            this.store.change({ id, vendorInvoice })
        } else {
            this.store.create(vendorInvoice)
        }
    }
}
