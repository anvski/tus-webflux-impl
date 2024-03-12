import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {UploadComponent} from "./upload/upload.component";
import {MatButtonModule} from "@angular/material/button";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatInputModule} from "@angular/material/input";
import {MatCardModule} from "@angular/material/card";
import {ReactiveFormsModule} from "@angular/forms";
import {UppyAngularDashboardModule} from "@uppy/angular";
import {CommonModule} from "@angular/common";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {RegisterComponent} from "./user_auth/register/register.component";
import {LoginComponent} from "./user_auth/login/login.component";
import {UniversalAppInterceptor} from "./service/universal-app-interceptor.service";
import {FormControlErrorStateMatcher} from "./form-utils/form-control-error-state-matcher";
import {ErrorStateMatcher} from "@angular/material/core";

@NgModule({
  declarations: [
    AppComponent,
    UploadComponent,
    RegisterComponent,
    LoginComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    CommonModule,
    UppyAngularDashboardModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    MatCardModule,
    HttpClientModule,
    MatInputModule,
    MatCheckboxModule,
    MatButtonModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: UniversalAppInterceptor,
      multi: true,
    },
    {
      provide: ErrorStateMatcher, useClass: FormControlErrorStateMatcher
    },],
  bootstrap: [AppComponent]
})
export class AppModule {
}
