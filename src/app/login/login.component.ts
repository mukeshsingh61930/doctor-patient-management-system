import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup | any;

  constructor(private fb: FormBuilder,
    private authService: AuthService,
    private _router: Router
  ) {

  }
  ngOnInit(): void {
    this.initForm();
  }

  initForm() {
    this.loginForm = this.fb.group({
      userId: [''],
      password: ['']
    })
  }

  login() {
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
          text: 'Invalid credentials. Please try again.',
          icon: 'error',
          confirmButtonText: 'OK'
        });
      }
    );
  }



}
