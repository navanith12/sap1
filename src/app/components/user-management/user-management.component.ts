import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { MatPaginator, MatTableDataSource, MatDialog, MatSnackBar } from '@angular/material';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { environment } from 'src/environments/environment';
import { DataLakesService } from 'src/app/services/data-lakes.service';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  @ViewChild('UserPageinator', { static: true }) userPageinator: MatPaginator;

  tableHeaders: string[];
  userTableData:  MatTableDataSource<any>;

  userForm: FormGroup;
  modelTitle : string;
  tableLoader: Boolean = true;
  selectedUserData: any;
  hide = true;
  searchKey : string = '';
  userRole: string = '';

  constructor(private dialog: MatDialog, private formBuilder: FormBuilder, private apis: DataLakesService, private snackBar: MatSnackBar ){
    this.userForm = this.formBuilder.group({
      firstname: ['',[Validators.required]],
      lastname: ['',[Validators.required]],
      username: ['',[Validators.required]],
      email: ['',[Validators.required, Validators.email]],
      password: ['',[Validators.required]],
      role: ['',[Validators.required]],
      alerts: [''],
    })

  }

  ngOnInit() {
    this.userRole = localStorage.getItem('userRole')
    if(this.userRole=="ADMIN") {
      this.tableHeaders = [ '#', 'id', 'firstname', 'lastname', 'email', 'username', 'userrole', 'actions']
    }
    else {
      this.tableHeaders = [ '#', 'id', 'firstname', 'lastname', 'email', 'username', 'userrole', ]
    }
    this.tableHeaders
    this.getUserList();
  }

  userModel(templateRef: TemplateRef<any>, title) {
    this.modelTitle = title;
    this.userForm.reset();
    this.userForm.enable();
    
    if(title=='Edit' || title=='Reset Password') {
      console.log("Selected Element: ",this.selectedUserData);
      this.userForm.patchValue({
        firstname: this.selectedUserData.firstname,
        lastname: this.selectedUserData.lastname,
        username: this.selectedUserData.username,
        email: this.selectedUserData.email,
        password: this.selectedUserData.password,
        alerts: this.selectedUserData.alerts,
        role: this.selectedUserData.roles[0].name,
      });

      if(title=='Edit') {
        this.userForm.controls.email.disable();
        this.userForm.controls.password.disable();
        this.userForm.controls.username.disable();
      }
      else {
        this.userForm.disable();
        this.userForm.controls.password.enable();
        this.userForm.controls.alerts.enable();
      }
    }
    
   
    this.dialog.open(templateRef, { width: '600px' });
  }

  onSearchClear() {
    this.searchKey = "";
    this.applyFilter();
  }

  applyFilter() {
    this.userTableData.filter = this.searchKey.trim().toLowerCase();
  }

  getUserList() {
    this.tableLoader = true;
    this.apis.getApi(environment.getUserList+'userinfo').subscribe(res => {
      this.userTableData = new MatTableDataSource(res);
      this.userTableData.paginator = this.userPageinator;
      console.log("getUserList result: ", this.userTableData);
      this.tableLoader = false;
    });
  }

  addUpdateUser() {
    let params = this.userForm.getRawValue();
    params.username = params.username.toLowerCase();
    params.role = [params.role];
    params.alerts = params.alerts==null ? false : true;
    console.log("user Params: ",params);
    this.apis.postApi(environment.userSignUpIn+'signup',params).subscribe(res => {
      console.log("addUpdateUser result: ", res);
      this.getUserList();
    });
  }

  deleteUser() {
    // /api/test/delete/user/{id}
    this.apis.deleteApi(environment.getUserList+'delete/user/'+this.selectedUserData.id).subscribe(res => {
      console.log("deleteUser result: ", res);
      this.getUserList();
    });
  }

}
