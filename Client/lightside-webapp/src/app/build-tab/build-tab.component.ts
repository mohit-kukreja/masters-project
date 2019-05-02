import { Component, Input, HostListener, OnInit } from "@angular/core";
import { BuildTabService } from "./build-tab.service";
import { FormControl, FormGroup } from "@angular/forms";
import { AllTabsComponent } from "../all-tabs/all-tabs.component";

@Component({
  selector: "app-build-tab",
  templateUrl: "./build-tab.component.html",
  styleUrls: ["./build-tab.component.scss"]
})
export class BuildTabComponent implements OnInit {
  inputfile: File;
  SelectedFile = null;
  selectedOption: any;
  loading = false;
  public show: boolean = false;
  result: any;
  barResult = 0;
  uploading = "uploading";
  constructor(private buildTabService: BuildTabService) {
    this.buildTabService.reBuildTab();
  }

  @Input() tabRoute: AllTabsComponent;

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
    console.log(this.selectedOption);
    const payload = new FormData();
    payload.append("inputfile", this.SelectedFile);
    payload.append("algo", this.selectedOption);
    this.show = true;

    this.buildTabService.uploadFile(payload).subscribe(accuracy => {
      this.result = parseFloat(accuracy);
      this.barResult = this.result * 100;
      this.buildTabService.setResult(accuracy);
      this.loading = false;
    });
  }

  ngOnInit() {}
}
