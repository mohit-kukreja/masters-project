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
// import { ThemeModule } from './Components/@theme/theme.module';
import { BuildTabService } from "./Components/build-tab/build-tab.service";

@NgModule({
  declarations: [AppComponent, BuildTabComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    NgbModule.forRoot()
  ],
  providers: [BuildTabService],
  bootstrap: [AppComponent]
})
export class AppModule {}
