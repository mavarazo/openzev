import {
    ChangeDetectionStrategy,
    Component,
    effect,
    inject,
    input,
} from '@angular/core'
import { UnitsStore } from '../../state/units.store'

@Component({
    selector: 'app-detail',
    imports: [],
    providers: [UnitsStore],
    templateUrl: './detail.component.html',
    styleUrl: './detail.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailComponent {
    readonly unitId = input.required<string>()
    private readonly store = inject(UnitsStore)
    readonly unit = this.store.unit

    constructor() {
        effect(() => {
            this.store.getUnit(this.unitId())
        })
    }
}
