import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthenticationService} from "../../service/authentication.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['../auth-forms.css'],
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;

  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private formBuilder: FormBuilder
  ) {
  }

  ngOnInit(): void {
    this.buildFormGroup();
  }

  buildFormGroup() {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(5)]],
      password: ['', [Validators.required, Validators.minLength(5)]]
    })
  }

  onSubmit(_: Event) {
    if (this.loginForm.invalid) return;
    this.authService
      .login(
        this.loginForm.controls["username"].value,
        this.loginForm.controls["password"].value
      )
      .subscribe( {
        next: _ => {
          this.router.navigate(['']).then();
        }
      });
  }
}
