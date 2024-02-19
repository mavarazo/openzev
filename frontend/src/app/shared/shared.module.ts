import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivePillComponent } from './components/active-pill/active-pill.component';

@NgModule({
  declarations: [ActivePillComponent],
  exports: [ActivePillComponent],
  imports: [CommonModule],
})
export class SharedModule {}
