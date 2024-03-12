import {Injectable, OnInit} from '@angular/core';
import {LocalStorageService} from './local-storage.service';
import {CookieService} from 'ngx-cookie-service';
import {StorageKeyEnum} from "../model/enum/storage.key.enum";
import {jwtDecode} from "jwt-decode";

@Injectable({
  providedIn: 'root',
})
export class UserAuthenticationService implements OnInit {
  private authenticated = false;

  constructor(
    private storage: LocalStorageService,
    private cookieService: CookieService
  ) {
    const token = this.retrieveActiveStorage().get(StorageKeyEnum.AUTH_TOKEN)
    this.authenticated = token != null && token.length > 0;
  }

  ngOnInit(): void {
  }

  saveLoggedInUser(token: string) {
    this.retrieveActiveStorage().set(StorageKeyEnum.AUTH_TOKEN, token);
    this.authenticated = true;
  }

  removeAuthenticationTokens() {
    if (navigator.cookieEnabled) {
      this.cookieService.delete(StorageKeyEnum.AUTH_TOKEN);
    }
    this.storage.remove(StorageKeyEnum.AUTH_TOKEN);
    this.authenticated = false;
  }

  isAuthenticatedAndValid(): boolean {
    if (this.authenticated) return this.checkTokenValidity()
    else return false;
  }

  getAuthenticationHeader() {
    if (navigator.cookieEnabled) {
      return `Bearer ${this.cookieService.get(StorageKeyEnum.AUTH_TOKEN)}`;
    } else return `Bearer ${this.storage.get(StorageKeyEnum.AUTH_TOKEN)}`;
  }

  private retrieveActiveStorage() {
    if (navigator.cookieEnabled) return this.cookieService
    else return this.storage;
  }

  private checkTokenValidity(): boolean {
    const token = this.retrieveActiveStorage().get(StorageKeyEnum.AUTH_TOKEN);
    if (token === null) return false;
    try {
      const decodedToken = jwtDecode(token);
      const expirationTime = decodedToken.exp;
      const currentTime = (Math.floor(Date.now() / 1000)); // to UNIX timestamp
      if (expirationTime === undefined || expirationTime < currentTime) {
        this.removeAuthenticationTokens();
      } else return true;
    } catch (e) {
      this.removeAuthenticationTokens();
    }
    return false;
  }
}
