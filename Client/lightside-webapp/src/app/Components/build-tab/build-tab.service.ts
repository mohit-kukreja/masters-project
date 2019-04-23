import { Injectable } from "@angular/core";
import {
  HttpHeaders,
  HttpClient,
  HttpErrorResponse,
  HttpResponse
} from "@angular/common/http";
import "rxjs/add/operator/catch";
import { Observable, throwError } from "rxjs";
import { catchError, retry } from "rxjs/operators";
@Injectable({
  providedIn: "root"
})
export class BuildTabService {
  resData: any;
  constructor(private http: HttpClient) {}

  uploadFile(payload) {
    return this.http.post("http://localhost:8000/uploadinput", payload, {
      responseType: "text"
    });
  }
}
