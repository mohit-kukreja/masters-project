import { Component, OnInit } from "@angular/core";
import { BuildTabService } from "../build-tab/build-tab.service";
import { FormControl, FormGroup } from "@angular/forms";

@Component({
  selector: "predict-tab",
  templateUrl: "./predict-tab.component.html",
  styleUrls: ["./predict-tab.component.scss"]
})
export class PredictTabComponent {
  inputfile: File;
  SelectedFile = null;
  selectedOption: any;
  loading = false;
  uploading = "uploading";
  result: any;
  barResult = 0;
  constructor(private buildTabService: BuildTabService) {}

  onFileSelected(event) {
    this.SelectedFile = event.target.files[0];
    console.log(this.SelectedFile);
  }

  toggleLoadingAnimation() {
    this.loading = true;
  }

  setValue(newValue) {
    this.barResult = Math.min(Math.max(newValue, 0), 100);
  }

  get status() {
    if (this.barResult <= 25) {
      return "danger";
    } else if (this.barResult <= 50) {
      return "warning";
    } else if (this.barResult <= 75) {
      return "info";
    } else {
      return "success";
    }
  }

  onSubmit() {
    const payload = new FormData();
    payload.append("inputfile", this.SelectedFile);

    this.buildTabService.uploadTestFile(payload).subscribe(article => {
      // this.result = article + "\t Please check Console";
      this.loading = false;
      this.result = this.buildTabService.getResult();
      // console.log("predict tab: \t" + this.result);
      this.barResult = this.result * 100;
      console.log("predict tab: \t" + this.barResult);
    });

    this.buildTabService.exportToCSV().subscribe(article => {
      console.log("save csv" + article.headers.get("file Uploaded"));
    });
  }
}
