import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { AllTabsComponent } from "./all-tabs/all-tabs.component";
import { BuildTabComponent } from "./build-tab/build-tab.component";
import { PredictTabComponent } from "./predict-tab/predict-tab.component";

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
