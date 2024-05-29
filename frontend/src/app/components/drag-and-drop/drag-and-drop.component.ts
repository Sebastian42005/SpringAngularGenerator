import {Component, EventEmitter, Output} from '@angular/core';

@Component({
  selector: 'app-drag-and-drop',
  templateUrl: './drag-and-drop.component.html',
  styleUrl: './drag-and-drop.component.scss'
})
export class DragAndDropComponent {
  files: File[] = []
  @Output() filesChanged = new EventEmitter<File[]>();

  onFileDropped(event: DragEvent) {
    event.preventDefault();
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.files.push(...Array.from(files));
      this.filesChanged.emit(this.files);
    }
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
  }


  onFileSelected($event: any) {
    if ($event?.target?.files) {
      const files: File[] = Array.from($event.target.files);
      this.files.push(...files);
      this.filesChanged.emit(this.files);
    }
  }

  deleteFile(file: File) {
    this.files = this.files.filter(f => f != file)
    this.filesChanged.emit(this.files);
  }
}
