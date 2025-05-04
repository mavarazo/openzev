import { ChangeDetectionStrategy, Component, inject } from '@angular/core'
import { MeterPointsStore } from '../../state/meter-points.store'
import { faPencil, faTrash } from '@fortawesome/free-solid-svg-icons'
import { MeterPoint } from '../../../../../generated-source/api'
import { FaIconComponent } from '@fortawesome/angular-fontawesome'
import { ActivatedRoute, Router, RouterLink } from '@angular/router'
import { ButtonComponent } from '../../../../shared/components/button/button.component'

@Component({
    selector: 'app-overview',
    standalone: true,
    imports: [FaIconComponent, RouterLink, ButtonComponent],
    providers: [MeterPointsStore],
    templateUrl: './overview.component.html',
    styleUrl: './overview.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverviewComponent {
    private readonly store = inject(MeterPointsStore)
    private readonly router = inject(Router)
    private readonly activeRoute = inject(ActivatedRoute)

    meterPoints = this.store.meterPoints
    readonly faPencil = faPencil
    readonly faTrash = faTrash

    addClicked() {
        this.router.navigate(['add'], { relativeTo: this.activeRoute }).then()
    }

    remove(meterPoint: MeterPoint) {
        this.store.removeMeterPoint(meterPoint)
    }
}
