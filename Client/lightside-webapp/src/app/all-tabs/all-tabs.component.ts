import { Component, OnInit } from "@angular/core";
import { BuildTabService } from "../build-tab/build-tab.service";

@Component({
  selector: "app-all-tabs",
  templateUrl: "./all-tabs.component.html",
  styleUrls: ["./all-tabs.component.scss"]
})
export class AllTabsComponent implements OnInit {
  isOpen = true;

  tabs: any[] = [
    {
      title: "Build",
      route: "/BuildTab",
      responsive: true
    }
  ];
  constructor(private buildTabService: BuildTabService) {}

  enableTab() {
    this.tabs.push({
      title: "Predict",
      route: "/PredictTab",
      responsive: true,
      disabled: this.isOpen
    });
  }
  disableTab() {
    if (this.tabs.length > 1) {
      this.tabs.pop();
    }
    console.log("tabs remainging" + this.tabs.length);
  }

  ngOnInit() {
    this.buildTabService.change.subscribe(isOpen => {
      this.isOpen = false;
      console.log("alltab change \t" + this.isOpen.valueOf());
      this.enableTab();
    });
    this.buildTabService.undo.subscribe(isOpen => {
      this.isOpen = isOpen;
      console.log("alltab undo \t" + this.isOpen.valueOf());
      this.disableTab();
    });
  }
}
