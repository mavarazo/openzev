import { ChangeDetectionStrategy, Component, inject } from '@angular/core'
import { MeterPointsStore } from '../../state/meter-points.store'
import { RouterLink } from '@angular/router'

@Component({
    selector: 'app-overview',
    standalone: true,
    imports: [RouterLink],
    providers: [MeterPointsStore],
    templateUrl: './overview.component.html',
    styleUrl: './overview.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverviewComponent {
    private readonly store = inject(MeterPointsStore)

    meterPoints = this.store.meterPoints
}
