import {
  ChangeDetectionStrategy,
  Component,
  effect,
  ElementRef,
  inject,
  OnInit,
  ViewChild,
} from '@angular/core';
import { MeterPointsStore } from '../../state/meter-points.store';
import { RouterLink } from '@angular/router';
import { Toast } from 'bootstrap';
import { error } from '@angular/compiler-cli/src/transformers/util';

@Component({
  selector: 'app-overview',
  standalone: true,
  imports: [RouterLink],
  providers: [MeterPointsStore],
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverviewComponent implements OnInit {
  private readonly store = inject(MeterPointsStore);

  @ViewChild('errorToast', { static: true })
  toastEl!: ElementRef<HTMLDivElement>;
  toast: Toast | null = null;

  meterPoints = this.store.meterPoints;

  errorMessage: String | null = null;

  _onError = effect(() => {
    const error = this.store.error();
    if (error && this.toast) {
      this.errorMessage = error.message;
      this.toast.show();
    }
  });

  ngOnInit(): void {
    if (this.toastEl) {
      this.toast = new Toast(this.toastEl.nativeElement, {});
    }
    this.store.loadAll();
  }

  protected readonly error = error;
}
