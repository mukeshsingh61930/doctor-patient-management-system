import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { CommonService } from '../service/common.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup | any;

  constructor(private fb: FormBuilder,
    private authService: AuthService,
    private commonService: CommonService,
    private _router: Router
  ) {

  }
  ngOnInit(): void {
    this.initForm();
  }

  initForm() {
    this.loginForm = this.fb.group({
      userId: ['', Validators.required],
      password: ['', Validators.required]
    })
  }

  login() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      Swal.fire({
        title: 'Required fields',
        text: 'Please enter User Id and Password.',
        icon: 'warning',
        confirmButtonText: 'OK'
      });
      return;
    }

    this.authService.login(this.loginForm.value).subscribe(
      (res: any) => {
        localStorage.clear();
        localStorage.setItem('isLoggedIn', 'true');
        localStorage.setItem('token', res.token);
        localStorage.setItem('tokenType', res.tokenType || 'Bearer');
        localStorage.setItem('userId', res.userId);
        localStorage.setItem('role', res.role);
        localStorage.setItem('name', res.name || '');
        localStorage.setItem('status', res.status || '');
        this._router.navigate(['/dashboard']);
      },
      (err: any) => {
        Swal.fire({
          title: 'Error!',
          text: this.commonService.getErrorMessage(err, 'Unable to login. Please try again.'),
          icon: 'error',
          confirmButtonText: 'OK'
        });
      }
    );
  }



}
