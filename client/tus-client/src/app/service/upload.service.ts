import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {FileRequestRequest} from "../model/file-request.request";

@Injectable({
  providedIn: "root"
})
export class UploadService {
  baseUrl = 'http://localhost:8080/upload'

  constructor(private http: HttpClient) {
  }

  storeRequest(body: FileRequestRequest) {
    return this.http.post(`${this.baseUrl}/store-request`, body);
  }

}
