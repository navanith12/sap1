import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { 
  MatPaginator,
  MatTableDataSource,
  MatDialog,
  MatSnackBar,
 } from '@angular/material';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { environment } from 'src/environments/environment';
import { DataLakesService } from 'src/app/services/data-lakes.service';
import { element } from 'protractor';


@Component({
  selector: 'app-configuration',
  templateUrl: './configuration.component.html',
  styleUrls: ['./configuration.component.css']
})
export class ConfigurationComponent implements OnInit {
  @ViewChild('PackagePageinator', { static: true }) PackagePageinator: MatPaginator;
  @ViewChild('ControlPageinator', { static: true }) ControlPageinator: MatPaginator;
  @ViewChild('ServicePageinator', { static: true }) servicePageinator: MatPaginator;
  @ViewChild('CategoryPageinator', { static: true }) categoryPageinator: MatPaginator;

  packageTableHeaders: string[] = ['#', 'extractorName', 'extractormodeType', 'packetsize', 'numberofthreads', 'actions'];
  packageTableData:  MatTableDataSource<any>;
  controlTableHeaders: string[] = ['#', 'extractor', 'current_token', 'previous_token'];
  controlTableData:  MatTableDataSource<any>;
  serviceTableHeaders: string[] = ['#', 'extractorName', 'serviceName', 'sourcetype', 'actions'];
  serviceTableData:  MatTableDataSource<any>;
  categoryTableHeaders: string[] = ['#', 'catergoryName', 'categoryType', 'categoryDescription', 'actions'];
  categoryTableData:  MatTableDataSource<any>;

  configurationForm: FormGroup;
  serviceForm: FormGroup;
  categoryForm: FormGroup;

  selectedConfigData: any;
  selectedServiceData: any;
  selectedCategoryData: any;
  modelTitle : string;
  configSearchKey: string;
  controlSearchKey: string;
  serviceSearchKey: string;
  categorySearchKey: string;

  mainSourceSystemList: Array<any> = [];
  sourceTypeList: Array<any> = [];
  subSourceTypeList: Array<any> = ['MASTER DATA INFOOBJECT', 'INFO CUBE', 'DSO', 'BEX QUERY'];
  sourceSystemList: Array<any> = [];

  loader: Boolean =true;

  constructor(private dialog: MatDialog, private formBuilder: FormBuilder, private apis: DataLakesService, private snackBar: MatSnackBar ){
    this.configurationForm = this.formBuilder.group({
      extractorName: ['',[Validators.required]],
      extractormodeType: ['',[Validators.required]],
      packetsize: ['',[Validators.required]],
      numberofthreads: ['',[Validators.required, Validators.min(1), Validators.max(10)]],
    });
    this.serviceForm = this.formBuilder.group({
      extractorName: ['',[Validators.required]],
      serviceName: ['',[Validators.required]],
      sourcetype: ['',[Validators.required]],
      sourceSystem: ['',[Validators.required]],
      subSourceType: ['',[Validators.required]]
    });

    this.categoryForm = this.formBuilder.group({
      catergoryName: ['', [Validators.required]],
      categoryDescription: ['', [Validators.required]],
      categoryType: ['', [Validators.required]],
    });

  }

  ngOnInit() {
    this.getConfiData();
    this.getControlData();
    this.getServiceData();
    this.getSourceSystems();
    this.getCategoryList();
    
    this.sourceTypeList = ['ECC', ' S/4 HANA', 'BW', 'ORACLE'];
  }

  configurationModel(templateRef: TemplateRef<any>, title) {
    this.modelTitle = title;
    this.configurationForm.reset();
    console.log("Selected Element: ",this.selectedConfigData);
    if(title=='Edit') {
      this.configurationForm.patchValue({
        extractorName: this.selectedConfigData.extractorName,
        extractormodeType: this.selectedConfigData.extractormodeType,
        packetsize: this.selectedConfigData.packetsize,
        numberofthreads: this.selectedConfigData.numberofthreads,
      });
    }
    this.dialog.open(templateRef, { width: '600px' });
  }

  getConfiData() {
    this.loader = true;
    this.apis.getApi(environment.getConfig).subscribe(res => {
      this.packageTableData = new MatTableDataSource(res);
      this.packageTableData.paginator = this.PackagePageinator;
      console.log("getScheduledData result: ", this.packageTableData);
      this.loader = false;
    });
  }

  onConfigSearchClear() {
    this.configSearchKey = "";
    this.configApplyFilter();
  }

  configApplyFilter() {
    this.packageTableData.filter = this.configSearchKey.trim().toLowerCase();
  }

  createUpdateConfig(type) {
    console.log("createConfig params: ",type,' - ',this.configurationForm.value);
    if(type=='Create') {
      this.apis.postApi(environment.getConfig,this.configurationForm.value).subscribe(res => {
        console.log("createConfig Result: ",res);
        this.snackBar.open("Configuration added successfully.", '', { duration:  5000, verticalPosition: "top" });
        this.getConfiData();
       });
    }
    else if(type=='Edit') {
      this.apis.putApi(environment.getConfig+this.selectedConfigData.id,this.configurationForm.value).subscribe(res => {
        console.log("updateConfig Result: ",res);
        this.snackBar.open("Configuration updated successfully.", '', { duration:  5000, verticalPosition: "top" });
        this.getConfiData();
       });
    }
    
  }

  deleteConfig() {
    console.log("deleteConfig params: ",this.selectedConfigData);
    this.apis.deleteApi(environment.getConfig+this.selectedConfigData.id).subscribe(res => {
      console.log("deleteConfig Result: ",res);
      this.snackBar.open("Configuration deleted successfully.", '', { duration:  5000, verticalPosition: "top" });
      this.getConfiData();
     });
  }


  getControlData() {
    this.loader = true;
    this.apis.getApi(environment.getDelta).subscribe(res => {
      this.controlTableData = new MatTableDataSource(res);
      this.controlTableData.paginator = this.ControlPageinator;
      console.log("getControlData result: ", this.controlTableData);
      this.loader = false;
    });
  }

  onControlSearchClear() {
    this.controlSearchKey = "";
    this.controlApplyFilter();
  }

  controlApplyFilter() {
    this.controlTableData.filter = this.controlSearchKey.trim().toLowerCase();
  }

  getErrorMessage(feild) {
    // if (feild=='threads'){
    //   return this.configurationForm.value.numberofthreads.h ('required') ? 'You must enter a value' :
    //     this.configurationForm.value.numberofthreads.hasError('email') ? 'Not a valid email' : '';
    // }
    
  }

  serviceModel(templateRef: TemplateRef<any>, title) {
    this.modelTitle = title;
    this.serviceForm.reset();
    this.serviceForm.enable();
    console.log("Selected Element: ",this.selectedServiceData);
    if(title=='Edit' || title=='View') {
      this.serviceForm.patchValue({
        extractorName: this.selectedServiceData.extractorName,
        serviceName: this.selectedServiceData.serviceName,
        sourcetype: this.selectedServiceData.sourcetype,
        sourceSystem: this.selectedServiceData.sourceSystem,
        subSourceType: this.selectedServiceData.sourceSubType,
      });
      this.filterSourceSystems(this.selectedServiceData.sourcetype);
    }
    if(title=='View') {
      this.serviceForm.disable();
    }
    this.dialog.open(templateRef, { width: '600px' });
  }

  createUpdateService(type) {
    let params = {
      "createdBy": localStorage.getItem('userName'),
      "extractorName": this.serviceForm.value.extractorName,
      "serviceName": this.serviceForm.value.serviceName,
      "sourcetype": this.serviceForm.value.sourcetype,
      "sourceSystem": this.serviceForm.value.sourceSystem,
      "sourceSubType": this.serviceForm.value.subSourceType,
    };
    console.log("createService params: ",type,' - ',this.serviceForm.value);
    if(type=='Create') {
      this.apis.postApi(environment.getService, params).subscribe(res => {
        console.log("createService Result: ",res);
        this.snackBar.open("Service added successfully.", '', { duration:  5000, verticalPosition: "top" });
        this.getServiceData();
       });
    }
    else if(type=='Edit') {
      this.apis.putApi(environment.getService+this.selectedServiceData.id, params).subscribe(res => {
        console.log("updateService Result: ",res);
        this.snackBar.open("Service updated successfully.", '', { duration:  5000, verticalPosition: "top" });
        this.getServiceData();
       });
    }
  }

  getServiceData() {
    this.apis.getApi(environment.getService).subscribe(res => {
      console.log("getServiceData result: ", res);
      if(res.Status=="OK"){
        this.serviceTableData = new MatTableDataSource(res.Data);
        this.serviceTableData.paginator = this.servicePageinator;
        // console.log("getServiceData result: ", this.serviceTableData);
      }
    });
  }

  deleteService() {
    console.log("deleteService params: ",this.selectedServiceData);
    this.apis.deleteApi(environment.getService+this.selectedServiceData.id).subscribe(res => {
      if(res.Status=="OK") {
        console.log("deleteService Result: ",res);
        this.snackBar.open("Service deleted successfully.", '', { duration:  5000, verticalPosition: "top" });
        this.getServiceData();
      }else {
        this.snackBar.open("Service deletion failed.", '', { duration:  5000, verticalPosition: "top" });
      }
      
     });
  }

  onServiceSearchClear() {
    this.serviceSearchKey = "";
    this.serviceApplyFilter();
  }

  serviceApplyFilter() {
    this.serviceTableData.filter = this.serviceSearchKey.trim().toLowerCase();
  }

  getSourceSystems() {
    this.apis.getApi(environment.getSourceSystemList).subscribe(res => {
      if(res.Status=='OK') {
        console.log("getSourceSystems res:",res);
        this.mainSourceSystemList = res.Data;
      }
    });
  }

  filterSourceSystems(sourceType) {
    this.sourceSystemList = [];
    this.mainSourceSystemList.forEach(element => {
      if(element.sourcetype==sourceType) {
        this.sourceSystemList.push(element.systemId);
      }
    });

    if(sourceType=='BW') {
      this.serviceForm.controls.subSourceType.setValidators(Validators.required);
      this.serviceForm.controls.subSourceType.updateValueAndValidity();
    } else {
      this.serviceForm.controls.subSourceType.clearValidators();
      this.serviceForm.controls.subSourceType.updateValueAndValidity();
    }

  }  
  
  categoryModel(templateRef: TemplateRef<any>, title) {
    this.modelTitle = title
    if(title=='Edit') {
      this.categoryForm.patchValue({
        catergoryName: this.selectedCategoryData.catergoryName,
        categoryType: this.selectedCategoryData.categoryType,
        categoryDescription: this.selectedCategoryData.categoryDescription,
      });
    }
    
    this.dialog.open(templateRef, { width: '600px' });
  }

  addNewCategory() {
    let params = {
      catergoryName: this.categoryForm.value.catergoryName.toUpperCase(),
      categoryDescription: this.categoryForm.value.categoryDescription.toUpperCase(),
    }
    this.apis.postApi(environment.category, params).subscribe(res => {
      console.log("addNewCategory res: ", res);
      if(res.Status=="OK") {
        this.getCategoryList();
      }
    });
  }

  addUpdateCategory(type) {
    let params = {
      catergoryName: this.categoryForm.value.catergoryName.toUpperCase(),
      categoryType: this.categoryForm.value.categoryType,
      categoryDescription: this.categoryForm.value.categoryDescription.toUpperCase(),
    }
    if (type=='Create') {
      this.apis.postApi(environment.category, params).subscribe(res => {
        console.log("addNewCategory res: ", res);
        if(res.Status=="OK") {
          this.getCategoryList();
        }
      });
    } else {
      this.apis.putApi(environment.category+this.selectedCategoryData.id, params).subscribe(res => {
        console.log("updateCategory res: ", res);
        if(res.Status=="OK") {
          this.getCategoryList();
        }
      });
    }
    
  }

  deleteCategory() {
    this.apis.deleteApi(environment.category+this.selectedCategoryData.id).subscribe(res => {
      console.log("deleteCategory res: ", res);
      if(res.Status=="OK") {
        this.getCategoryList();
      }
    });
  }

  getCategoryList() {
    this.apis.getApi(environment.category).subscribe(res => {
      console.log("getCategoryList res: ", res);
      if(res.Status=="OK") {
        this.categoryTableData = new MatTableDataSource(res.Data);
        this.categoryTableData.paginator = this.categoryPageinator;
      }
    });
  }

  onCategorySearchClear() {
    this.categorySearchKey = "";
    this.categoryApplyFilter();
  }

  categoryApplyFilter() {
    this.categoryTableData.filter = this.categorySearchKey.trim().toLowerCase();
  }

}
