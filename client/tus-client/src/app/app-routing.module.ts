import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {UploadComponent} from "./upload/upload.component";
import {LoginComponent} from "./user_auth/login/login.component";
import {RegisterComponent} from "./user_auth/register/register.component";
import {UnauthorizedGuard} from "./guards/unauthorized.guard";
import {AuthGuard} from "./guards/auth.guard";

const routes: Routes = [
  {path: '', component: UploadComponent, canActivate: [AuthGuard]},
  {path: 'login', component: LoginComponent, canActivate: [UnauthorizedGuard]},
  {path: 'register', component: RegisterComponent, canActivate: [UnauthorizedGuard]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
