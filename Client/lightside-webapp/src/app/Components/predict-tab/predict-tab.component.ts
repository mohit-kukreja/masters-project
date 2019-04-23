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
  constructor(private buildTabService: BuildTabService) {}

  onFileSelected(event) {
    this.SelectedFile = event.target.files[0];
    console.log(this.SelectedFile);
  }

  toggleLoadingAnimation() {
    this.loading = true;
  }

  onSubmit() {
    const payload = new FormData();
    payload.append("inputfile", this.SelectedFile);

    this.buildTabService.uploadFile(payload).subscribe(article => {
      this.uploading = article;
      setTimeout(() => (this.loading = false), 300);
    });
  }
}
