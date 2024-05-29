import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {FileDto} from "./dto/FileDto";
import {FileListDto} from "./dto/FileListDto";
import {FrontendFileListDto} from "./dto/FrontendFileListDto";

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private httpClient: HttpClient) { }

  generateTsFiles(files: File[]): Observable<FileDto[]> {
    const formData = new FormData();
    files.forEach(file => {
      formData.append('files', file);
    });
    return this.post<FileDto[]>('/frontend/ts-file', formData);
  }

  generateRest(name: string): Observable<FileDto> {
    return this.get<FileDto>('/backend/generate/' + name);
  }

  generateService(files: File[]): Observable<FileListDto[]> {
    const formData = new FormData();
    files.forEach(file => {
      formData.append('files', file);
    });
    return this.post<FileListDto[]>('/backend/generate', formData);
  }

  generateAdminPage(files: File[]): Observable<FrontendFileListDto[]> {
    const formData = new FormData();
    files.forEach(file => {
      formData.append('files', file);
    });
    return this.post<FrontendFileListDto[]>('/frontend/admin-page', formData);
  }

  getApiCalls(files: File[]): Observable<FileDto> {
    const formData = new FormData();
    files.forEach(file => {
      formData.append('files', file);
    });
    return this.post<FileDto>('/frontend/api-calls', formData);
  }

  private get<T>(url: string): Observable<T> {
    return this.httpClient.get<T>(baseUrl + url);
  }

  private post<T>(url: string, body: any): Observable<T> {
    return this.httpClient.post<T>(baseUrl + url, body);
  }

  private delete<T>(url: string): Observable<T> {
    return this.httpClient.delete<T>(baseUrl + url);
  }

  private put<T>(url: string, body: any): Observable<T> {
    return this.httpClient.put<T>(baseUrl + url, body);
  }
}

export const baseUrl = 'http://localhost:8081';
