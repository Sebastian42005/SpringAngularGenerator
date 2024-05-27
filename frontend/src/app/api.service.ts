import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {RestGenerateDto} from "./dto/RestGenerateDto";

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private httpClient: HttpClient) { }

  generateRest(name: string): Observable<RestGenerateDto[]> {
    return this.get<RestGenerateDto[]>('/backend/generate/' + name);
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

export const baseUrl = 'http://localhost:8080';
