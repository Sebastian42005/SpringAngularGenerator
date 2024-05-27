import {AfterViewInit, Component} from '@angular/core';
import {ApiService} from "./api.service";
import {RestGenerateDto} from "./dto/RestGenerateDto";
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  name: string = '';
  file: File | null = null;
  data: RestGenerateDto[];

  constructor(private apiService: ApiService) {}

  generateRest() {
    this.apiService.generateRest(this.name).subscribe(data => {
      this.data = data
    })
  }


  onFileDropped(event: DragEvent) {
    event.preventDefault();
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.file = files[0];
    }
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
  }

  uploadFile() {
    if (this.file) {
      const formData = new FormData();
      formData.append('file', this.file);

    }
  }
}
