import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { AllTabsComponent } from "./Components/all-tabs/all-tabs.component";
import { BuildTabComponent } from "./Components/build-tab/build-tab.component";
import { PredictTabComponent } from "./Components/predict-tab/predict-tab.component";

const routes: Routes = [
  {
    path: "",
    component: AllTabsComponent,
    children: [
      {
        path: "BuildTab",
        component: BuildTabComponent
      },
      {
        path: "PredictTab",
        component: PredictTabComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
