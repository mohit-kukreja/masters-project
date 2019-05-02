import { Injectable, Output, EventEmitter } from "@angular/core";
import {
  HttpHeaders,
  HttpClient,
  HttpErrorResponse,
  HttpResponse
} from "@angular/common/http";

import { Observable, throwError } from "rxjs";
import { catchError, retry } from "rxjs/operators";
@Injectable({
  providedIn: "root"
})
export class BuildTabService {
  isOpen = true;
  @Output() change: EventEmitter<boolean> = new EventEmitter();
  @Output() undo: EventEmitter<boolean> = new EventEmitter();

  resData: any;
  constructor(private http: HttpClient) {}

  uploadFile(payload) {
    return this.http.post("http://localhost:8000/uploadinput", payload, {
      responseType: "text"
    });
  }

  exportToCSV() {
    let httpHeaders = new HttpHeaders({
      "Content-Type": "multipart/form-data"
    });
    let options = {
      headers: httpHeaders
    };
    return this.http.post("http://localhost:8000/CSVsave", options, {
      observe: "response"
    });
  }
  uploadTestFile(payload) {
    return this.http.post("http://localhost:8000/uploadtest", payload, {
      responseType: "text"
    });
  }

  setResult(res) {
    console.log(res);
    this.resData = res;
    if (res) {
      this.isOpen = !this.isOpen;
      console.log("setResult \t" + this.isOpen.valueOf());
      this.change.emit(this.isOpen);
    }
  }

  reBuildTab() {
    this.isOpen = true;
    console.log("reBuildTab \t" + this.isOpen.valueOf());

    this.undo.emit(this.isOpen);
  }

  getResult() {
    // console.log(this.resData.valueOf());
    return this.resData;
  }
}
