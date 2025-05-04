import {
    ChangeDetectionStrategy,
    Component,
    effect,
    inject,
    input,
    OnInit,
} from '@angular/core'
import { ConsumptionsStore } from '../../state/consumptions.store'
import {
    FormControl,
    FormGroup,
    ReactiveFormsModule,
    Validators,
} from '@angular/forms'
import { Consumption } from '../../../../../generated-source/api'
import { ButtonComponent } from '../../../../shared/components/button/button.component'
import { DatePipe } from '@angular/common'

@Component({
    selector: 'app-add-edit',
    standalone: true,
    imports: [ReactiveFormsModule, ButtonComponent, DatePipe],
    providers: [ConsumptionsStore],
    templateUrl: './add-edit.component.html',
    styleUrl: './add-edit.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddEditComponent implements OnInit {
    readonly consumptionId = input<string>()
    private readonly store = inject(ConsumptionsStore)
    readonly consumption = this.store.consumption

    readonly meterPoints = this.store.meterPoints
    readonly consumptions = this.store.consumptionsByMeterPoint

    private formSubmitAttempt: boolean = false
    isEditMode: boolean = false

    form = new FormGroup({
        meterPointId: new FormControl<string | undefined>(
            undefined,
            Validators.required
        ),
        previousConsumptionId: new FormControl<string | undefined>(undefined),
        date: new FormControl<Date | undefined>(undefined, Validators.required),
        total: new FormControl<number | undefined>(
            undefined,
            Validators.required
        ),
    })

    constructor() {
        effect(() => {
            const id = this.consumptionId()
            if (id) {
                this.isEditMode = true
                this.store.get(id)
            }
        })

        effect(() => {
            const consumption = this.consumption()
            if (consumption != undefined) {
                this.form.patchValue(consumption)
                if (consumption.meterPoint?.id != undefined) {
                    this.store.loadPreviousConsumptions(
                        consumption.meterPoint.id
                    )
                    this.form.controls.meterPointId.setValue(
                        consumption.meterPoint.id
                    )
                }
                if (consumption.previousConsumption?.id != undefined) {
                    this.form.controls.previousConsumptionId.setValue(
                        consumption.previousConsumption.id
                    )
                }
            }
        })
    }

    ngOnInit(): void {
        this.store.loadMeterPoints()
    }

    onMeterPointIdChange(meterPointId: string) {
        if (meterPointId) {
            this.store.loadPreviousConsumptions(meterPointId)
        }
    }

    submit(): void {
        this.formSubmitAttempt = true

        if (this.form.invalid) {
            return
        }

        const id = this.consumptionId()
        const consumption: Consumption = Object.assign(this.form.value)
        if (this.isEditMode && id) {
            this.store.change({ id, consumption })
        } else {
            this.store.create(consumption)
        }
    }
}
