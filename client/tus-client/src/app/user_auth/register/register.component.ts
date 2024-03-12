import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthenticationService} from "../../service/authentication.service";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['../auth-forms.css'],
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private authenticationService: AuthenticationService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.buildFormGroup();
  }

  onSubmit(_: Event) {
    if (this.registerForm.invalid) return;
    const username = this.registerForm.controls["username"].value;
    const password = this.registerForm.controls["password"].value;
    const email = this.registerForm.controls["email"].value;
    this.authenticationService.register(username, password, email).subscribe({
      next: _ => {
        this.router.navigate(['/login']).then();
      }
    });
  }


  buildFormGroup() {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(5)]],
      password: ['', [Validators.required, Validators.minLength(5)]],
      email: ['', [Validators.required, Validators.email]]
    })
  }
}
