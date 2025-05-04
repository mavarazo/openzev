import {
    ChangeDetectionStrategy,
    Component,
    effect,
    inject,
    input,
} from '@angular/core'
import { UnitsStore } from '../../state/units.store'
import {
    FormControl,
    FormGroup,
    ReactiveFormsModule,
    Validators,
} from '@angular/forms'
import { Unit } from '../../../../../generated-source/api'
import { ButtonComponent } from '../../../../shared/components/button/button.component'

@Component({
    selector: 'app-add-edit',
    standalone: true,
    imports: [ReactiveFormsModule, ButtonComponent],
    providers: [UnitsStore],
    templateUrl: './add-edit.component.html',
    styleUrl: './add-edit.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddEditComponent {
    readonly unitId = input<string>()
    private readonly store = inject(UnitsStore)
    readonly unit = this.store.unit

    private formSubmitAttempt: boolean = false
    isEditMode: boolean = false

    form = new FormGroup({
        number: new FormControl<string | undefined>(
            undefined,
            Validators.required
        ),
        firstName: new FormControl<string | undefined>(undefined),
        lastName: new FormControl<string | undefined>(
            undefined,
            Validators.required
        ),
    })

    constructor() {
        effect(() => {
            const id = this.unitId()
            if (id) {
                this.isEditMode = true
                this.store.getUnit(id)
            }
        })

        effect(() => {
            const unit = this.unit()
            if (unit != undefined) {
                this.form.patchValue(unit)
            }
        })
    }

    submit(): void {
        this.formSubmitAttempt = true

        if (this.form.invalid) {
            return
        }

        const id = this.unitId()
        const unit: Unit = Object.assign(this.form.value)
        if (this.isEditMode && id) {
            this.store.changeUnit({ id, unit })
        } else {
            this.store.createUnit(unit)
        }
    }
}
