import {Component, Input} from '@angular/core';
import {FileDto} from "../../dto/FileDto";

@Component({
  selector: 'app-display-data',
  templateUrl: './display-data.component.html',
  styleUrl: './display-data.component.scss'
})
export class DisplayDataComponent {
  @Input() data: FileDto[] = []
  @Input() rowTemplate = "1fr 1fr";
}
