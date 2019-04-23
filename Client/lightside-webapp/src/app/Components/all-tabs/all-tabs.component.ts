import { Component, OnInit } from "@angular/core";

@Component({
  selector: "app-all-tabs",
  templateUrl: "./all-tabs.component.html",
  styleUrls: ["./all-tabs.component.scss"]
})
export class AllTabsComponent implements OnInit {
  tabs: any[] = [
    {
      title: "Build",
      route: "/BuildTab"
    },
    {
      title: "Predict",
      route: "/PredictTab"
    }
  ];
  constructor() {}

  ngOnInit() {}
}
