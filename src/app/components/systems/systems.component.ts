import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatPaginator, MatTableDataSource, MatDialog, MatSnackBar } from '@angular/material';
import { environment } from 'src/environments/environment';
import { DataLakesService } from 'src/app/services/data-lakes.service';

@Component({
  selector: 'app-systems',
  templateUrl: './systems.component.html',
  styleUrls: ['./systems.component.css']
})
export class SystemsComponent implements OnInit {
  @ViewChild('SourcePageinator', { static: true }) sourcePageinator: MatPaginator;
  @ViewChild('DbPageinator', { static: true }) dbPageinator: MatPaginator;
  @ViewChild('FtpPageinator', { static: true }) ftpPageinator: MatPaginator;

  sourceHeaders: string[] = ['#', 'sourcetype', 'applicationServer', 'port', 'client', 'description', 'instances', 'systemId', 'createdAt', 'actions'];
  sourceTableData: MatTableDataSource<any>;
  dbHeaders: string[] = ['#', 'connectionName', 'username', 'createdAt', 'actions'];
  dbTableData: MatTableDataSource<any>;
  ftpHeaders: string[] = ['#', 'storageName', 'containerName', 'accessKey', 'createdAt', 'actions'];
  ftpTableData: MatTableDataSource<any>;
  // lfHeaders: string[] = ['#', 'applicationServer', 'description', 'createdAt',  'actions'];
  // lfTableData:  MatTableDataSource<any>;


  searchKey: string;
  minDate: Date = new Date();
  maxDate: Date;
  fromDate: Date = null;
  toDate: Date = null;

  systemForm: FormGroup;
  tableType: string = 'source';
  selectedSystemData: any;
  modelTitle: string = '';

  tableFalg: Boolean = false;
  systemTypeDisable: Boolean = false;
  hide: Boolean = true;
  loader: Boolean = true;
  userRole: string = '';

  sourcetypeList: Array<any> = [];
  databaseTypeList: Array<any> = ['MYSQL', 'ORACLE', 'POSTGRESQL'];

  constructor(private dialog: MatDialog, private formBuilder: FormBuilder, private apis: DataLakesService, private snackBar: MatSnackBar) {
    this.systemForm = this.formBuilder.group({
      
      systemType: ['', [Validators.required]],
      // createdBy: [],
      // Source
      sourcetype: ['', [Validators.required]],
      appServer: ['', [Validators.required]],
      portNumber: ['', [Validators.required]],
      client: ['', [Validators.required]],
      instance: ['', [Validators.required]],
      loginId: ['', [Validators.required]],
      password: ['', [Validators.required]],
      systemId: ['', [Validators.required]],
      description: ['', [Validators.required]],
      // DB
      connectionName: ['', [Validators.required]],
      dbtype: ['', [Validators.required]],
      hostAddress: ['', [Validators.required]],
      dbPort: ['', [Validators.required]],
      dbName: ['', [Validators.required]],
      username: ['', [Validators.required]],
      dbPassword: ['', [Validators.required]],
      // FTP
      storageName: ['', [Validators.required]],
      containerName: ['', [Validators.required]],
      accessKey: ['', [Validators.required]]
    })
  }

  ngOnInit() {
    this.tableType = 'source';
    this.userRole = localStorage.getItem('userRole');
    this.sourcetypeList = ['ECC', ' S/4 HANA', 'BW', 'ORACLE'];
    this.changeSystemTable();
  }

  systemModel(templateRef: TemplateRef<any>, title) {
    this.systemForm.reset();
    this.systemForm.enable();
    this.modelTitle = title;
    this.hide = true;
    this.systemForm.controls['systemType'].setValue(this.tableType);
    if (title == 'Edit' || title == 'View') {
      console.log("System: "+this.systemForm.value.systemType);
      console.log("Element: ", this.selectedSystemData);
      this.systemForm.patchValue({
        
        systemType: this.tableType,
        // createdBy: this.selectedSystemData.createdBy,
        // Source
        sourcetype: this.selectedSystemData.sourcetype,
        appServer: this.selectedSystemData.applicationServer,
        portNumber: this.selectedSystemData.port,
        client: this.selectedSystemData.client,
        instance: this.selectedSystemData.instances,
        loginId: this.selectedSystemData.login,
        password: this.selectedSystemData.password,
        systemId: this.selectedSystemData.systemId,
        description: this.selectedSystemData.description,
        // DB
        connectionName: this.selectedSystemData.connectionName,
        dbtype: this.selectedSystemData.dbtype,
        hostAddress: this.selectedSystemData.hostAddress,
        dbPort: this.selectedSystemData.portnumber,
        dbName: this.selectedSystemData.dbname,
        username: this.selectedSystemData.username,
        dbPassword: this.selectedSystemData.password,
        // FTP
        storageName: this.selectedSystemData.storageName,
        containerName: this.selectedSystemData.containerName,
        accessKey: this.selectedSystemData.accessKey
      });
      console.log("System Before 1 Call: "+this.systemForm.value.systemType);
      
      this.systemTypeDisable = true;
      this.systemForm.controls['appServer'].disable();
    }

    if (title == 'View') {
      this.systemForm.disable();
    }
    else if(title == 'Edit'){
      // this.systemForm.controls['systemType'].disabled;
    }
    this.changeSystemType();
    this.dialog.open(templateRef, { width: '600px' });
    console.log("System Before 2 Call: "+this.systemForm.value.systemType);

  }

  onSearchClear() {
    this.searchKey = "";
    this.applyFilter();
  }

  applyFilter() {
    this.sourceTableData.filter = this.searchKey.trim().toLowerCase();
  }

  searchSystems() {
    let params = {
      jobName: this.searchKey,
      fromDate: this.fromDate,
      toDate: this.toDate
    }
    console.log("searchSystems params: ", params);
  }

  changeSystemTable() {
    console.log("In changeSystemTable: ",this.tableType);
    this.loader = true;
    if (this.tableType == 'source') {
      console.log("In Souce Table")
      this.getSourceTableData();
    }
    else if (this.tableType == 'db') {
      this.getDbTableData();
    }
    else if (this.tableType == 'ftp') {
      this.getFtpTableData();
    }
    else if (this.tableType == 'lf') {
      // this.getLfTableData();
    }
  }

  getSourceTableData() {
    this.apis.getApi(environment.getSourceSystemList).subscribe(res => {
      if(res.Status=='OK'){
        this.sourceTableData = new MatTableDataSource(res.Data);
        this.sourceTableData.paginator = this.sourcePageinator;
        this.tableFalg = true;
        console.log("getSourceTableData result: ", this.sourceTableData);
        this.loader = false;
      }else {
        this.snackBar.open("Filed to load Systems.", '', { duration:  5000, verticalPosition: "top" });
      }
      
    });
  }

  getDbTableData() {
    this.apis.getApi(environment.getDbList).subscribe(res => {
      if(res.Status=='OK'){
        this.dbTableData = new MatTableDataSource(res.Data);
        this.dbTableData.paginator = this.dbPageinator;
        console.log("getdbTableData result: ", this.dbTableData);
        this.loader = false;
      }else {
        this.snackBar.open("Filed to load Systems.", '', { duration:  5000, verticalPosition: "top" });
      }
    });
  }

  // getLfTableData() {
  //   this.apis.getApi(environment.getDbList).subscribe(res => {
  //     console.log("getLfTableData result: ", res);
  //     this.lfTableData = new MatTableDataSource(res);
  //     this.lfTableData.paginator = this.paginator;
  //   });
  // }

  getFtpTableData() {
    this.apis.getApi(environment.getFtpList).subscribe(res => {
      if(res.Status=='OK'){
        this.ftpTableData = new MatTableDataSource(res.Data);
        this.ftpTableData.paginator = this.ftpPageinator;
        console.log("getFtpTableData result: ", this.ftpTableData);
        this.loader = false;
      }else {
        this.snackBar.open("Filed to load Systems.", '', { duration:  5000, verticalPosition: "top" });
      }
    });
  }

  changeSystemType() {
    console.log("In changeSystemType: "+this.systemForm.value.systemType);
    // this.systemForm.clearValidators()
    if (this.systemForm.value.systemType == 'source') {
      // Source
      this.systemForm.controls.sourcetype.setValidators(Validators.required);
      this.systemForm.controls.sourcetype.updateValueAndValidity();
      this.systemForm.controls.appServer.setValidators(Validators.required);
      this.systemForm.controls.appServer.updateValueAndValidity();
      this.systemForm.controls.portNumber.setValidators(Validators.required);
      this.systemForm.controls.portNumber.updateValueAndValidity();
      this.systemForm.controls.client.setValidators(Validators.required);
      this.systemForm.controls.client.updateValueAndValidity();
      this.systemForm.controls.instance.setValidators(Validators.required);
      this.systemForm.controls.instance.updateValueAndValidity();
      this.systemForm.controls.loginId.setValidators(Validators.required);
      this.systemForm.controls.loginId.updateValueAndValidity();
      this.systemForm.controls.password.setValidators(Validators.required);
      this.systemForm.controls.password.updateValueAndValidity();
      this.systemForm.controls.systemId.setValidators(Validators.required);
      this.systemForm.controls.systemId.updateValueAndValidity();
      this.systemForm.controls.description.setValidators(Validators.required);
      this.systemForm.controls.description.updateValueAndValidity();
      // DB
      this.systemForm.controls.dbtype.clearValidators();
      this.systemForm.controls.dbtype.updateValueAndValidity();
      this.systemForm.controls.connectionName.clearValidators();
      this.systemForm.controls.connectionName.updateValueAndValidity();
      this.systemForm.controls.hostAddress.clearValidators();
      this.systemForm.controls.hostAddress.updateValueAndValidity();
      this.systemForm.controls.dbPort.clearValidators();
      this.systemForm.controls.dbPort.updateValueAndValidity();
      this.systemForm.controls.dbName.clearValidators();
      this.systemForm.controls.dbName.updateValueAndValidity();
      this.systemForm.controls.username.clearValidators();
      this.systemForm.controls.username.updateValueAndValidity();
      this.systemForm.controls.dbPassword.clearValidators();
      this.systemForm.controls.dbPassword.updateValueAndValidity();
      // FTP
      this.systemForm.controls.storageName.clearValidators();
      this.systemForm.controls.storageName.updateValueAndValidity();
      this.systemForm.controls.containerName.clearValidators();
      this.systemForm.controls.containerName.updateValueAndValidity();
      this.systemForm.controls.accessKey.clearValidators();
      this.systemForm.controls.accessKey.updateValueAndValidity();
      // this.systemForm.updateValueAndValidity();
    }
    else if (this.systemForm.value.systemType == 'db') {
      this.systemForm.controls.sourcetype.clearValidators();
      this.systemForm.controls.sourcetype.updateValueAndValidity();
      this.systemForm.controls.appServer.clearValidators();
      this.systemForm.controls.appServer.updateValueAndValidity();
      this.systemForm.controls.portNumber.clearValidators();
      this.systemForm.controls.portNumber.updateValueAndValidity();
      this.systemForm.controls.client.clearValidators();
      this.systemForm.controls.client.updateValueAndValidity();
      this.systemForm.controls.instance.clearValidators();
      this.systemForm.controls.instance.updateValueAndValidity();
      this.systemForm.controls.loginId.clearValidators();
      this.systemForm.controls.loginId.updateValueAndValidity();
      this.systemForm.controls.password.clearValidators();
      this.systemForm.controls.password.updateValueAndValidity();
      this.systemForm.controls.systemId.clearValidators();
      this.systemForm.controls.systemId.updateValueAndValidity();
      this.systemForm.controls.description.clearValidators();
      this.systemForm.controls.description.updateValueAndValidity();

      this.systemForm.controls.dbtype.setValidators(Validators.required);
      this.systemForm.controls.dbtype.updateValueAndValidity();
      this.systemForm.controls.connectionName.setValidators(Validators.required);
      this.systemForm.controls.connectionName.updateValueAndValidity();
      this.systemForm.controls.hostAddress.setValidators(Validators.required);
      this.systemForm.controls.hostAddress.updateValueAndValidity();
      this.systemForm.controls.dbPort.setValidators(Validators.required);
      this.systemForm.controls.dbPort.updateValueAndValidity();
      this.systemForm.controls.dbName.setValidators(Validators.required);
      this.systemForm.controls.dbName.updateValueAndValidity();
      this.systemForm.controls.username.setValidators(Validators.required);
      this.systemForm.controls.username.updateValueAndValidity();
      this.systemForm.controls.dbPassword.setValidators(Validators.required);
      this.systemForm.controls.dbPassword.updateValueAndValidity();
      
      this.systemForm.controls.storageName.clearValidators();
      this.systemForm.controls.storageName.updateValueAndValidity();
      this.systemForm.controls.containerName.clearValidators();
      this.systemForm.controls.containerName.updateValueAndValidity();
      this.systemForm.controls.accessKey.clearValidators();
      this.systemForm.controls.accessKey.updateValueAndValidity();
      // this.systemForm.updateValueAndValidity();
    }
    else if (this.systemForm.value.systemType == 'ftp') {
      this.systemForm.controls.sourcetype.clearValidators();
      this.systemForm.controls.sourcetype.updateValueAndValidity();
      this.systemForm.controls.appServer.clearValidators();
      this.systemForm.controls.appServer.updateValueAndValidity();
      this.systemForm.controls.portNumber.clearValidators();
      this.systemForm.controls.portNumber.updateValueAndValidity();
      this.systemForm.controls.client.clearValidators();
      this.systemForm.controls.client.updateValueAndValidity();
      this.systemForm.controls.instance.clearValidators();
      this.systemForm.controls.instance.updateValueAndValidity();
      this.systemForm.controls.loginId.clearValidators();
      this.systemForm.controls.loginId.updateValueAndValidity();
      this.systemForm.controls.password.clearValidators();
      this.systemForm.controls.password.updateValueAndValidity();
      this.systemForm.controls.systemId.clearValidators();
      this.systemForm.controls.systemId.updateValueAndValidity();
      this.systemForm.controls.description.clearValidators();
      this.systemForm.controls.description.updateValueAndValidity();

      this.systemForm.controls.dbtype.clearValidators();
      this.systemForm.controls.dbtype.updateValueAndValidity();
      this.systemForm.controls.connectionName.clearValidators();
      this.systemForm.controls.connectionName.updateValueAndValidity();
      this.systemForm.controls.hostAddress.clearValidators();
      this.systemForm.controls.hostAddress.updateValueAndValidity();
      this.systemForm.controls.dbPort.clearValidators();
      this.systemForm.controls.dbPort.updateValueAndValidity();
      this.systemForm.controls.dbName.clearValidators();
      this.systemForm.controls.dbName.updateValueAndValidity();
      this.systemForm.controls.username.clearValidators();
      this.systemForm.controls.username.updateValueAndValidity();
      this.systemForm.controls.dbPassword.clearValidators();
      this.systemForm.controls.dbPassword.updateValueAndValidity();

      this.systemForm.controls.storageName.setValidators(Validators.required);
      this.systemForm.controls.storageName.updateValueAndValidity();
      this.systemForm.controls.containerName.setValidators(Validators.required);
      this.systemForm.controls.containerName.updateValueAndValidity();
      this.systemForm.controls.accessKey.setValidators(Validators.required);
      this.systemForm.controls.accessKey.updateValueAndValidity();

      // this.systemForm.updateValueAndValidity();
    }
    console.log("In After changeSystemType: "+this.systemForm.value.systemType);

  }

  createUpdateSystem() {
    let paramsV = {
      "applicationServer": this.systemForm.controls.appServer.valid,
        "client": this.systemForm.controls.client.valid,
        "description": this.systemForm.controls.description.valid,
        "instances": this.systemForm.controls.instance.valid,
        "login": this.systemForm.controls.loginId.valid,
        "password": this.systemForm.controls.password.valid,
        "port": this.systemForm.controls.portNumber.valid,
        "systemId": this.systemForm.controls.systemId.valid,
        "connectionName": this.systemForm.controls.connectionName.valid,
        "hostAddress": this.systemForm.controls.hostAddress.valid,
        "dbPort": this.systemForm.controls.dbPort.valid,
        "dbName": this.systemForm.controls.dbName.valid,
        "username": this.systemForm.controls.username.valid,
        "dbpassword": this.systemForm.controls.dbPassword.valid,
        "storageName": this.systemForm.controls.storageName.valid,
        "containerName": this.systemForm.controls.containerName.valid,
        "accessKey": this.systemForm.controls.accessKey.valid,
    }
    console.log("ParamsV: ",this.systemForm.valid,'\n',paramsV);
    let systemType;
    let params;
    if (this.systemForm.value.systemType == 'source') {
      systemType = this.systemForm.value.systemType;
      params = {
        "sourcetype": this.systemForm.value.sourcetype,
        "applicationServer": this.systemForm.value.appServer || this.selectedSystemData.applicationServer,
        "client": this.systemForm.value.client,
        "description": this.systemForm.value.description,
        "instances": this.systemForm.value.instance,
        "login": this.systemForm.value.loginId,
        "password": this.systemForm.value.password,
        "port": this.systemForm.value.portNumber, // Change it to number
        "systemId": this.systemForm.value.systemId,
      }
    }
    else if (this.systemForm.value.systemType == 'db') {
      systemType = 'dbInsert';
      params = {
        "dbtype": this.systemForm.value.dbtype,
        "connectionName": this.systemForm.value.connectionName,
        "hostAddress": this.systemForm.value.hostAddress,
        // "url": 'jdbc:sqlserver://'+this.systemForm.value.hostAddress+':'+this.systemForm.value.dbPort+';databaseName='+this.systemForm.value.dbName,
        "portnumber": this.systemForm.value.dbPort,
        "dbname": this.systemForm.value.dbName,
        "username": this.systemForm.value.username,
        "password": this.systemForm.value.dbPassword,
      }
    }
    else if(this.systemForm.value.systemType == 'ftp') {
      systemType = this.systemForm.value.systemType;
      params = {
        "storageName": this.systemForm.value.storageName,
        "containerName": this.systemForm.value.containerName,
        "accessKey": this.systemForm.value.accessKey,
        
      }
    }
    

    console.log("createUpdateSystem Params: ", params);

    if(this.modelTitle=='Create') {
      // params.createdBy = localStorage.getItem('userName');
      this.apis.postApi(environment.rootUrl+systemType+'/', params).subscribe(res => {
        if(res.Status=="OK"){
          this.changeSystemTable();
          this.snackBar.open("System added successfully.", '', { duration:  5000, verticalPosition: "top" });
          console.log("createSystem res: ",res);
        } else {
          this.snackBar.open("Filed to add new System.", '', { duration:  5000, verticalPosition: "top" });
        }
      })
    }
    else if(this.modelTitle=='Edit') {
      // params.createdBy = this.selectedSystemData.createdBy;
      // params.id = this.selectedSystemData.id;
      this.apis.putApi(environment.rootUrl+systemType+'/'+this.selectedSystemData.id, params).subscribe(res => {
        console.log("Update System: ",res)
        if(res.Status=="OK"){
          this.changeSystemTable();
          this.snackBar.open("System updated successfully.", '', { duration:  5000, verticalPosition: "top" });
          console.log("updateSystem res: ",res);
        }else {
          this.snackBar.open("Filed to update System.", '', { duration:  5000, verticalPosition: "top" });
        }
      })
    }
    
  }

  deleteSystem() {
    let systemType;
    if(this.tableType=='db') {
      systemType = 'dbInsert';
    }else{
      systemType = this.tableType;
    }
    this.apis.deleteApi(environment.rootUrl+systemType+'/'+this.selectedSystemData.id).subscribe(res => {
      console.log("Result of deleteSystem: ",res);
      this.snackBar.open("System deleted successfully.", '', { duration:  5000, verticalPosition: "top" });
      this.changeSystemTable();
    });
    
  }
}

