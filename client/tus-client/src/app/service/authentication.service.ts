import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {TokenResponse} from "../model/response/token.response";
import {Observable, tap} from "rxjs";
import {UserAuthenticationService} from "./user-authentication.service";

@Injectable({
  providedIn: "root"
})
export class AuthenticationService {

  private baseUrl = 'http://localhost:8080/auth';

  constructor(private _http: HttpClient,
              private userAuthenticationService: UserAuthenticationService) {
  }

  login(username: string, password: string): Observable<TokenResponse> {
    const requestBody = {
      username: username,
      password: password
    }
    return this._http.post<TokenResponse>(`${this.baseUrl}/login`, requestBody).pipe(
      tap((response) => this.userAuthenticationService.saveLoggedInUser(response.access_token))
    );
  }

  register(username: string, password: string, email: string) {
    const requestBody = {
      username: username,
      password: password,
      email: email
    }

    return this._http.post<Boolean>(`${this.baseUrl}/register`, requestBody);
  }

}
