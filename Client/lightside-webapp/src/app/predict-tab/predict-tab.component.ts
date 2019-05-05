import { Component, OnInit } from "@angular/core";
import { BuildTabService } from "../build-tab/build-tab.service";
import { FormControl, FormGroup } from "@angular/forms";
import { Router } from "@angular/router";

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
  public show: boolean = false;

  barResult = 0;
  constructor(
    private buildTabService: BuildTabService,
    private router: Router
  ) {}

  onFileSelected(event) {
    this.SelectedFile = event.target.files[0];
    console.log(this.SelectedFile);
  }

  toggleLoadingAnimation() {
    this.loading = true;
  }

  saveCSV() {
    console.log("save clicked");
    this.buildTabService.exportToCSV().subscribe(article => {
      console.log("save csv" + article.headers.get("file Uploaded"));
    });
  }

  setValue() {
    this.router.navigate(["/BuildTab"]);
  }

  onSubmit() {
    const payload = new FormData();
    payload.append("inputfile", this.SelectedFile);

    this.buildTabService.uploadTestFile(payload).subscribe(article => {
      // this.result = article + "\t Please check Console";
      this.loading = false;
      this.show = !this.show;
    });
  }
}
