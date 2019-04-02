import { Injectable } from "@angular/core";
import { HttpHeaders, HttpClient } from "@angular/common/http";
@Injectable({
  providedIn: "root"
})
export class BuildTabService {
  resData: any;
  constructor(private http: HttpClient) {}

  uploadFile(payload) {
    console.log("called");
    return this.http.post("http://localhost:8000/uploadinput", payload);
  }
}
