import { ChangeDetectionStrategy, Component, inject } from '@angular/core'
import { UnitsStore } from '../../state/units.store'
import { RouterLink } from '@angular/router'
import { faPencil, faTrash } from '@fortawesome/free-solid-svg-icons'
import { FaIconComponent } from '@fortawesome/angular-fontawesome'
import { Unit } from '../../../../../generated-source/api'

@Component({
    selector: 'app-overview',
    standalone: true,
    imports: [RouterLink, FaIconComponent],
    providers: [UnitsStore],
    templateUrl: './overview.component.html',
    styleUrl: './overview.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverviewComponent {
    private readonly store = inject(UnitsStore)

    protected readonly faPencil = faPencil
    protected readonly faTrash = faTrash

    units = this.store.units

    remove(unit: Unit) {
        this.store.deleteUnit(unit)
    }
}
