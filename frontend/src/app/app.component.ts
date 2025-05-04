import { Component } from '@angular/core'
import { RouterOutlet } from '@angular/router'
import { SidebarComponent } from './core/sidebar/sidebar.component'
import { NavItem } from './core/sidebar/nav-item.model'

@Component({
    selector: 'app-root',
    imports: [RouterOutlet, SidebarComponent],
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss',
})
export class AppComponent {
    items: NavItem[] = [
        {
            label: 'Consumptions',
            link: '/consumptions',
        },
        {
            label: 'Meter Points',
            link: '/meter-points',
        },
        {
            label: 'Units',
            link: '/units',
        },
        {
            label: 'Vendor Invoices',
            link: '/vendor-invoices',
        },
    ]
}
