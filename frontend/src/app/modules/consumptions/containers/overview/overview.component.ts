import { ChangeDetectionStrategy, Component, inject } from '@angular/core'
import { ConsumptionsStore } from '../../state/consumptions.store'
import { faPencil, faTrash } from '@fortawesome/free-solid-svg-icons'
import { FaIconComponent } from '@fortawesome/angular-fontawesome'
import { Consumption } from '../../../../../generated-source/api'
import { ActivatedRoute, Router, RouterLink } from '@angular/router'
import { DatePipe } from '@angular/common'
import { ButtonComponent } from '../../../../shared/components/button/button.component'

@Component({
    selector: 'app-overview',
    standalone: true,
    imports: [FaIconComponent, RouterLink, DatePipe, ButtonComponent],
    providers: [ConsumptionsStore],
    templateUrl: './overview.component.html',
    styleUrl: './overview.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverviewComponent {
    private readonly store = inject(ConsumptionsStore)
    private readonly router = inject(Router)
    private readonly activeRoute = inject(ActivatedRoute)

    protected readonly faPencil = faPencil
    protected readonly faTrash = faTrash

    readonly consumptions = this.store.consumptions

    addClicked() {
        this.router.navigate(['add'], { relativeTo: this.activeRoute }).then()
    }

    remove(consumption: Consumption) {
        this.store.remove(consumption)
    }
}
