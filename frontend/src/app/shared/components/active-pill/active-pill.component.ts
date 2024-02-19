import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-active-pill',
  templateUrl: './active-pill.component.html',
  styleUrls: ['./active-pill.component.scss'],
})
export class ActivePillComponent {
  @Input() active: boolean;
}
