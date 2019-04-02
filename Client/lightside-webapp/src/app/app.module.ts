import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";
import { HttpClientModule } from "@angular/common/http";
import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { BuildTabComponent } from "./Components/build-tab/build-tab.component";
import { APP_BASE_HREF } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ThemeModule } from "./Components/@theme/theme.module";
import { BuildTabService } from "./Components/build-tab/build-tab.service";
import { AllTabsComponent } from "./Components/all-tabs/all-tabs.component";
import { PredictComponent } from "./Components/predict/predict.component";
import { PredictTabComponent } from "./Components/predict-tab/predict-tab.component";

@NgModule({
  declarations: [
    AppComponent,
    BuildTabComponent,
    AllTabsComponent,
    PredictComponent,
    PredictTabComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    NgbModule.forRoot(),
    ThemeModule.forRoot()
  ],
  providers: [{ provide: APP_BASE_HREF, useValue: "/" }, BuildTabService],
  bootstrap: [AppComponent]
})
export class AppModule {}
