import { ChangeDetectionStrategy, Component, OnInit } from "@angular/core";
import { BuildTabService } from "./build-tab.service";
import { FormControl, FormGroup } from "@angular/forms";

@Component({
  selector: "app-build-tab",
  templateUrl: "./build-tab.component.html",
  styleUrls: ["./build-tab.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BuildTabComponent {
  inputfile: File;
  SelectedFile = null;
  selectedOption:any;
  constructor(private buildTabService: BuildTabService) {
    
  }



  onFileSelected(event) {
    this.SelectedFile = event.target.files[0];
    console.log(this.SelectedFile);
  }

  onSubmit() {
    const payload = new FormData();
    payload.append("inputfile", this.SelectedFile);
    this.buildTabService.uploadFile(payload).subscribe(article => {
      console.log(article);
    });
  }
}
