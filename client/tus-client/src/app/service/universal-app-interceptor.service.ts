import {Injectable} from '@angular/core';
import {HttpHandler, HttpInterceptor, HttpRequest, HttpResponse, HttpStatusCode,} from '@angular/common/http';
import {UserAuthenticationService} from "./user-authentication.service";
import {tap} from "rxjs";
import {Router} from "@angular/router";

@Injectable()
export class UniversalAppInterceptor implements HttpInterceptor {
  constructor(
    private userAuthenticationService: UserAuthenticationService,
    private router: Router
  ) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const accessTokenHeader = this.userAuthenticationService.getAuthenticationHeader();
    req = req.clone({
      url: req.url,
      setHeaders: {
        Authorization: accessTokenHeader,
      },
    });
    return next.handle(req).pipe(
      tap(httpEvent => {
        if(httpEvent instanceof HttpResponse && httpEvent.status == HttpStatusCode.Unauthorized) {
          this.userAuthenticationService.removeAuthenticationTokens();
          this.router.navigate(['/login']);
        }
      })
    );

  }
}
