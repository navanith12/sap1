import { Component, OnInit } from '@angular/core';
import { Router } from "@angular/router";
import { environment } from 'src/environments/environment';
import { DataLakesService } from 'src/app/services/data-lakes.service';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  userName: string;
  password: string;
  hide = true;
  constructor(private router: Router, private apis: DataLakesService) { }

  ngOnInit() {
  }

  login() {
    let params = {
      "username": this.userName.toLocaleLowerCase(),
      "password": this.password
    }
    console.log("In login");
    this.apis.postApi(environment.login, params).subscribe(res => {
      console.log("login result: ",res);
      if(res.username) {
        localStorage.setItem('userName', res.username);
        localStorage.setItem('userRole',res.authorities[0].authority);
        localStorage.setItem('userFullName', res.firstname+' '+res.lastname)
        this.router.navigateByUrl('/datalake/dashboard');
        // this.loader = false;
      }
    });
    // localStorage.setItem('userName', 'Chandrasekhar');
    // localStorage.setItem('userRole','BUSINESS');
    // this.router.navigateByUrl('/datalake/dashboard');
    

  }

}
