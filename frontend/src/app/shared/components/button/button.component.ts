import {
    ChangeDetectionStrategy,
    Component,
    input,
    output,
} from '@angular/core'

@Component({
    selector: 'app-button',
    imports: [],
    templateUrl: './button.component.html',
    styleUrl: './button.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ButtonComponent {
    readonly type = input<string>('button')
    readonly label = input.required<string>()
    readonly clicked = output()

    onClick() {
        this.clicked.emit()
    }
}
