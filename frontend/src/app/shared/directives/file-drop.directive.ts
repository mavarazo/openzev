import {
  Directive,
  EventEmitter,
  HostBinding,
  HostListener,
  Output,
} from '@angular/core';

@Directive({
  selector: '[appFileDrop]',
})
export class FileDropDirective {
  @HostBinding('class.fileover') fileOver: boolean;
  @Output() fileDropped = new EventEmitter<any>();

  @HostListener('dragover', ['$event']) onDragOver($event: any) {
    $event.preventDefault();
    $event.stopPropagation();
    this.fileOver = true;
  }

  @HostListener('dragleave', ['$event'])
  public onDragLeave($event: any) {
    $event.preventDefault();
    $event.stopPropagation();
    this.fileOver = false;
  }

  @HostListener('drop', ['$event'])
  public ondrop($event: any) {
    $event.preventDefault();
    $event.stopPropagation();
    this.fileOver = false;
    const files = $event.dataTransfer.files;
    if (files.length > 0) {
      this.fileDropped.emit(files);
    }
  }
}
