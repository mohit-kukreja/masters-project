import { Component, OnInit } from "@angular/core";
import { BuildTabService } from "./build-tab.service";

@Component({
  selector: "app-build-tab",
  templateUrl: "./build-tab.component.html",
  styleUrls: ["./build-tab.component.scss"]
})
export class BuildTabComponent implements OnInit {
  // inputfile: File;
  // SelectedFile = null;
  constructor(private buildTabService: BuildTabService) {}

  ngOnInit() {}

  // onFileSelected(event) {
  //   this.SelectedFile = event.target.files[0];
  //   console.log(this.SelectedFile);
  // }

  // onSubmit() {
  //   const payload = new FormData();
  //   payload.append("inputfile", this.SelectedFile);
  //   this.buildTabService.uploadFile(payload);
  // }
}
