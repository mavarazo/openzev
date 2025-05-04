import {
    ChangeDetectionStrategy,
    Component,
    effect,
    inject,
    input,
    OnInit,
} from '@angular/core'
import { MeterPointsStore } from '../../state/meter-points.store'
import {
    FormControl,
    FormGroup,
    ReactiveFormsModule,
    Validators,
} from '@angular/forms'
import { MeterPoint } from '../../../../../generated-source/api'
import { ButtonComponent } from '../../../../shared/components/button/button.component'

@Component({
    selector: 'app-add-edit',
    standalone: true,
    imports: [ReactiveFormsModule, ButtonComponent],
    providers: [MeterPointsStore],
    templateUrl: './add-edit.component.html',
    styleUrl: './add-edit.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddEditComponent implements OnInit {
    readonly meterPointId = input<string>()
    private readonly store = inject(MeterPointsStore)
    readonly meterPoint = this.store.meterPoint

    readonly units = this.store.units

    private formSubmitAttempt: boolean = false
    isEditMode: boolean = false

    form = new FormGroup({
        number: new FormControl<string | undefined>(
            undefined,
            Validators.required
        ),
        unitId: new FormControl<string | undefined>(
            undefined,
            Validators.required
        ),
    })

    constructor() {
        effect(() => {
            const id = this.meterPointId()
            if (id) {
                this.isEditMode = true
                this.store.getMeterPoint(id)
            }
        })

        effect(() => {
            const meterPoint = this.meterPoint()
            if (meterPoint != undefined) {
                this.form.patchValue(meterPoint)
                if (meterPoint.unit) {
                    this.form.controls.unitId.setValue(meterPoint.unit.id)
                }
            }
        })
    }

    ngOnInit(): void {
        this.store.loadUnits()
    }

    submit(): void {
        this.formSubmitAttempt = true

        if (this.form.invalid) {
            return
        }

        const id = this.meterPointId()
        const meterPoint: MeterPoint = Object.assign(this.form.value)
        if (this.isEditMode && id) {
            this.store.changeMeterPoint({ id, meterPoint })
        } else {
            this.store.createMeterPoint(meterPoint)
        }
    }
}
