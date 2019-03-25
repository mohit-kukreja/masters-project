import { Injectable } from "@angular/core";
import { HttpHeaders, HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: "root"
})
export class BuildTabService {
  resData: any;
  constructor(private http: HttpClient) {}

  uploadFile(payload) {
    let headers = new HttpHeaders({
      enctype: "multipart/form-data",
      "Content-Type": "application/x-www-form-urlencoded"
    });
    let options = { headers: headers };
    console.log("called");
    this.http.post("http://localhost:8000/uploadinput", payload, options);
  }
}
