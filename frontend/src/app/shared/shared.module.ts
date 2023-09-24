import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FileDropDirective } from './directives/file-drop.directive';

@NgModule({
  declarations: [FileDropDirective],
  exports: [FileDropDirective],
  imports: [CommonModule],
})
export class SharedModule {}
