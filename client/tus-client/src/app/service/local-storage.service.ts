import {Injectable} from '@angular/core';
import {StorageKeyEnum} from "../model/enum/storage.key.enum";

@Injectable({
  providedIn: 'root',
})
export class LocalStorageService {

  set(key: StorageKeyEnum, value: string) {
    localStorage.setItem(key, value);
  }

  get(key: StorageKeyEnum): string | null {
    return localStorage.getItem(key);
  }

  remove(key: StorageKeyEnum) {
    return localStorage.removeItem(key);
  }
}
