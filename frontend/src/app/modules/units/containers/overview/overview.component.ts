import { ChangeDetectionStrategy, Component, inject } from '@angular/core'
import { UnitsStore } from '../../state/units.store'
import { faPencil, faTrash } from '@fortawesome/free-solid-svg-icons'
import { FaIconComponent } from '@fortawesome/angular-fontawesome'
import { Unit } from '../../../../../generated-source/api'
import { ActivatedRoute, Router, RouterLink } from '@angular/router'
import { ButtonComponent } from '../../../../shared/components/button/button.component'

@Component({
    selector: 'app-overview',
    standalone: true,
    imports: [FaIconComponent, RouterLink, ButtonComponent],
    providers: [UnitsStore],
    templateUrl: './overview.component.html',
    styleUrl: './overview.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverviewComponent {
    private readonly store = inject(UnitsStore)
    private readonly router = inject(Router)
    private readonly activeRoute = inject(ActivatedRoute)

    protected readonly faPencil = faPencil
    protected readonly faTrash = faTrash

    units = this.store.units

    addClicked() {
        this.router.navigate(['add'], { relativeTo: this.activeRoute }).then()
    }

    remove(unit: Unit) {
        this.store.removeUnit(unit)
    }
}
