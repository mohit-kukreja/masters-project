import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";

import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { APP_BASE_HREF } from "@angular/common";

import { HttpClientModule } from "@angular/common/http";
import {
  NbThemeModule,
  NbLayoutModule,
  NbTabsetModule,
  NbActionsModule,
  NbCardModule,
  NbMenuModule,
  NbRouteTabsetModule,
  NbSearchModule,
  NbUserModule,
  NbCheckboxModule,
  NbPopoverModule,
  NbContextMenuModule,
  NbProgressBarModule,
  NbCalendarModule,
  NbCalendarRangeModule,
  NbStepperModule,
  NbButtonModule,
  NbInputModule,
  NbAccordionModule,
  NbDatepickerModule,
  NbDialogModule,
  NbWindowModule,
  NbListModule,
  NbToastrModule,
  NbAlertModule,
  NbSpinnerModule,
  NbRadioModule,
  NbSelectModule,
  NbChatModule,
  NbTooltipModule
} from "@nebular/theme";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";

import { NbSidebarModule, NbSidebarService } from "@nebular/theme";
import { AllTabsComponent } from "./all-tabs/all-tabs.component";
import { BuildTabComponent } from "./build-tab/build-tab.component";
import { PredictTabComponent } from "./predict-tab/predict-tab.component";
import { FormsModule } from "@angular/forms";
import { ReactiveFormsModule } from "@angular/forms";

import { BuildTabService } from "./build-tab/build-tab.service";

@NgModule({
  declarations: [
    AppComponent,
    AllTabsComponent,
    BuildTabComponent,
    PredictTabComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NbTabsetModule,
    BrowserAnimationsModule,
    NbLayoutModule,
    NbSidebarModule,
    HttpClientModule,
    NbThemeModule.forRoot({ name: "corporate" }),
    NbLayoutModule,
    NbActionsModule,
    NbCardModule,
    NbLayoutModule,
    NbMenuModule,
    NbRouteTabsetModule,
    NbSearchModule,
    NbSidebarModule,
    NbTabsetModule,
    NbThemeModule,
    NbUserModule,
    NbCheckboxModule,
    NbPopoverModule,
    NbContextMenuModule,
    NbProgressBarModule,
    NbCalendarModule,
    NbCalendarRangeModule,
    NbStepperModule,
    NbButtonModule,
    NbInputModule,
    NbAccordionModule,
    NbDatepickerModule,
    NbDialogModule,
    NbWindowModule,
    NbListModule,
    NbToastrModule,
    NbAlertModule,
    NbSpinnerModule,
    NbRadioModule,
    NbSelectModule,
    NbChatModule,
    NbTooltipModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule
  ],
  providers: [
    { provide: APP_BASE_HREF, useValue: "/" },
    NbSidebarService,
    BuildTabService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
