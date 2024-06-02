import {AfterViewInit, Component, OnInit} from '@angular/core';
import {ApiService} from "./api.service";
import {FileDto} from "./dto/FileDto";
import {FileListDto} from "./dto/FileListDto";
import {FrontendFileListDto} from "./dto/FrontendFileListDto";
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  name: string = '';
  data: FileDto[];
  frontendData: FileDto[];
  backendServices: FileListDto[];
  frontendAdminPage: FrontendFileListDto[]
  apiCalls: FileDto;
  files: File[] = [];
  backendFiles: File[] = [];
  controllerFiles: File[] = [];
  requestsData: FileDto[];

  constructor(private apiService: ApiService) {}

  generateRest() {
    this.apiService.generateRest(this.name).subscribe(data => {
      this.data = [data];
    })
  }

  generateTsFiles() {
    this.apiService.generateTsFiles(this.files).subscribe(data => {
      this.frontendData = data;
    })
  }

  generateController() {
    this.apiService.generateService(this.backendFiles).subscribe(data => {
      this.backendServices = data;
    })
  }

  generateAdminPage() {
    this.apiService.generateAdminPage(this.files).subscribe(data => {
      this.frontendAdminPage = data;
    })
    this.getApiCalls();
  }

  getCreateComponentCommand(name: string) {
    return `ng g c admin/${name.toLowerCase()}/Admin${name}`
  }

  getCreateComponentCreateCommand(name: string) {
    return `ng g c admin/${name.toLowerCase()}/AdminCreate${name}`
  }

  getCreateComponentDetailCommand(name: string) {
    return `ng g c admin/${name.toLowerCase()}/AdminDetail${name}`
  }

  getApiCalls() {
    this.apiService.getApiCalls(this.files).subscribe(apiCalls => {
      console.log("Api", apiCalls)
      this.apiCalls = apiCalls;
    })
  }

  getImports() {
    return `
    MatInputModule,
    MatTableModule,
    MatSortModule,
    MatIconModule,
    MatButtonModule,
    MatPaginatorModule,
    FormsModule,
    MatSelectModule`
  }

  getRouting() {
    const fileList: string[] = [];
    this.files.forEach(file => {
      fileList.push(`  {path: 'admin/${file.name.replace('.java', '').toLowerCase()}', component: Admin${file.name.replace('.java', '')}Component}`)
      fileList.push(`  {path: 'admin/${file.name.replace('.java', '').toLowerCase()}/:id', component: AdminDetail${file.name.replace('.java', '')}Component}`)
    })
    return fileList.join(",\n");
  }

  getImportsAndApiCalls() {
    const list: FileDto[] = [
      {
        title: 'Imports',
        content: this.getImports()
      },
      this.apiCalls,
      {
        title: 'Routing',
        content: this.getRouting(),
      }
    ];
    return list;
  }

  generateRequests() {
    this.apiService.generateRequests(this.controllerFiles).subscribe(data => {
      this.requestsData = data;
    })
  }
}
