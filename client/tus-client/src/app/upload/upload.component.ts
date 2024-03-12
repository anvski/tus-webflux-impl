import {AfterViewInit, Component, OnInit, ViewChild} from "@angular/core";
import {Uppy} from "@uppy/core";
import Tus from "@uppy/tus";
import {DashboardOptions} from "@uppy/dashboard";
import {fromPromise} from "rxjs/internal/observable/innerFrom";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {DashboardComponent} from "@uppy/angular";
import {LocalStorageService} from "../service/local-storage.service";
import {BehaviorSubject, debounceTime, filter, firstValueFrom, fromEvent, of, switchMap, tap} from "rxjs";
import {HttpStatusCode} from "@angular/common/http";
import {UploadService} from "../service/upload.service";
import {FileRequestRequest} from "../model/file-request.request";
import {UserAuthenticationService} from "../service/user-authentication.service";
import {StorageKeyEnum} from "../model/enum/storage.key.enum";


@Component({
  selector: 'app-upload',
  templateUrl: 'upload.component.html',
  styleUrls: ['upload.component.css']
})
export class UploadComponent implements OnInit, AfterViewInit {

  @ViewChild('dashboardRef') dashboardRef!: DashboardComponent;

  uppy: Uppy = new Uppy({
    allowMultipleUploadBatches: false,
    restrictions: {
      maxNumberOfFiles: 1,
      allowedFileTypes: [".mp4", ".png", ".jpg", ".jpeg"]
    }
  });
  dashboardOptions!: DashboardOptions;
  uploadForm!: FormGroup;
  canSendRequestSubject = new BehaviorSubject<boolean>(true);

  constructor(private formBuilder: FormBuilder,
              private localStorageService: LocalStorageService,
              private uploadService: UploadService,
              private authService: UserAuthenticationService) {
    this.generateFormGroup();
  }

  ngOnInit(): void {
    this.uppy
      .use(Tus, {
        endpoint: 'http://localhost:8080/upload', chunkSize: 1000000,
        storeFingerprintForResuming: true,
        onBeforeRequest: (request, file) => {
          return firstValueFrom<void>(this.canSendRequestSubject.pipe(
            filter(val => val),
            filter(() => !!file.type),
            tap(() => {
              request.setHeader("Authorization", this.authService.getAuthenticationHeader());
              request.setHeader("Mime-Type", file.type!);
              request.setHeader("Tus-Resumable", "1.0.0");
            }),
            switchMap(() => of(void 0))
          ));
        },
        onAfterResponse: (request, response) => {
          if (response.getStatus() == HttpStatusCode.Created) {
            this.canSendRequestSubject.next(false);
            const fileUuid = response.getHeader("Location").split("upload/")[1];
            const requestBody = this.formToRequest(fileUuid);
            this.uploadService.storeRequest(requestBody).subscribe({
              next: _ => {
                this.canSendRequestSubject.next(true);
              },
              error: _ => {
                this.uppy.cancelAll({reason: "unmount"});
              }
            });
          }
        }
      });

    this.dashboardOptions = {
      proudlyDisplayPoweredByUppy: false,
      height: 400,
      showProgressDetails: true,
      hideUploadButton: true,
      note: "Supported types : [.mp4, .png, .jpg/.jpeg]",
      singleFileFullScreen: true
    };

    fromEvent(this.uppy, 'file-added').subscribe({
      next: (value: any) => {
        this.uploadForm.get("uploadControl")?.setValue(value);
      }
    });

    fromEvent(this.uppy, 'file-removed').subscribe({
      next: (_: any) => {
        this.uploadForm.get("uploadControl")?.setValue(null);
      }
    })

    this.attemptLoadFormFromStorage();
    this.patchFormInStorageOnChange();
  }

  ngAfterViewInit(): void {
    fromEvent(this.dashboardRef.el.nativeElement, 'click').subscribe({
      next: _ => {
        this.uploadForm.controls["uploadControl"].markAsTouched({onlySelf: true});
      }
    });
  }

  private generateFormGroup() {
    this.uploadForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(10)]],
      description: ['', [Validators.required, Validators.minLength(15)]],
      uploadControl: [null, [Validators.required]]
    });
  }

  attemptLoadFormFromStorage() {
    const formDataString = this.localStorageService.get(StorageKeyEnum.UPLOAD_FORM);
    if (!formDataString) return;
    const formData: { [key: string]: any } = JSON.parse(formDataString);
    Object.keys(formData).filter(key => formData[key] != null && formData[key].length > 0 && this.uploadForm.contains(key))
      .forEach(key => {
        this.uploadForm.controls[key].patchValue(formData[key]);
        this.uploadForm.controls[key].markAsTouched();
      });
  }

  onSubmit() {
    if (this.uploadForm.invalid) return;
    fromPromise(this.uppy.upload()).subscribe();
  }

  get isUploadFormInvalid() {
    return this.uploadForm.controls["uploadControl"].invalid
      && this.uploadForm.controls["uploadControl"].touched
  }

  private patchFormInStorageOnChange() {
    this.uploadForm.valueChanges.pipe(
      debounceTime(500)
    ).subscribe({
      next: _ => {
        this.localStorageService.set(StorageKeyEnum.UPLOAD_FORM, JSON.stringify(this.uploadForm.getRawValue()));
      }
    })
  }

  private formToRequest(fileUuid: string): FileRequestRequest {
    return {
      fileUuid: fileUuid,
      fileRequestData: {
        title: this.uploadForm.controls["title"].value,
        description: this.uploadForm.controls["description"].value
      }
    }
  }

}
