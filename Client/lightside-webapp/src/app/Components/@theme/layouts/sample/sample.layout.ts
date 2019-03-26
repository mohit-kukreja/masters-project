import { Component, OnDestroy } from "@angular/core";
import { delay, withLatestFrom, takeWhile } from "rxjs/operators";
import {
  NbMediaBreakpoint,
  NbMediaBreakpointsService,
  NbMenuItem,
  NbMenuService,
  NbSidebarService,
  NbThemeService
} from "@nebular/theme";

// TODO: move layouts into the framework
@Component({
  selector: "ngx-sample-layout",
  styleUrls: ["./sample.layout.scss"],
  template: `
    <nb-layout windowMode>
      <nb-layout-header fixed> Tabs </nb-layout-header>
      <nb-layout-column class="main-content">
        <ng-content select="router-outlet"></ng-content>
      </nb-layout-column>
    </nb-layout>
  `
})
export class SampleLayoutComponent implements OnDestroy {
  layout: any = {};
  sidebar: any = {};
  currentTheme: string;

  constructor(
    protected menuService: NbMenuService,
    protected themeService: NbThemeService,
    protected bpService: NbMediaBreakpointsService,
    protected sidebarService: NbSidebarService
  ) {}

  ngOnDestroy() {}
}
