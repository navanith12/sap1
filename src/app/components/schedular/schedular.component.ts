import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { MatTableDataSource, MatPaginator, MatDialog, MatSnackBar, } from '@angular/material';
import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { CronOptions, CronGenComponent } from 'ngx-cron-editor';
import { environment } from 'src/environments/environment';
import { DataLakesService } from 'src/app/services/data-lakes.service';

@Component({
  selector: 'app-schedular',
  templateUrl: './schedular.component.html',
  styleUrls: ['./schedular.component.css']
})
export class SchedularComponent implements OnInit {

  public cronExpression = '0 0 1/1 * *';
  public isCronDisabled = false;
  public cronOptions: CronOptions = {
    formInputClass: 'form-control cron-editor-input',
    formSelectClass: 'form-control cron-editor-select',
    formRadioClass: 'cron-editor-radio',
    formCheckboxClass: 'cron-editor-checkbox',
    defaultTime: '00:00:00',
    hideMinutesTab: false,
    hideHourlyTab: false,
    hideDailyTab: false,
    hideWeeklyTab: false,
    hideMonthlyTab: false,
    hideYearlyTab: true,
    hideAdvancedTab: true,
    hideSpecificWeekDayTab: false,
    hideSpecificMonthWeekTab: false,
    use24HourTime: true,
    hideSeconds: false,
    cronFlavor: 'standard'
  };
  @ViewChild('cronEditorDemo', { static: false })
  cronEditorDemo: CronGenComponent;

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;

  tableHeaders: string[] = ['#', 'jobName', 'jobId', 'frequency', 'scheduledDate', 'status', 'actions'];
  scheduleData:  MatTableDataSource<any>;
  // tiles: any;
  searchKey: string;
  minDate: Date;
  maxDate: Date;

  scheduleForm: FormGroup;
  tableTypeList: any = [];
  tableType: string;
  modelTitle: string = '';
  selectedScheduleData: any;

  scheduleList: any;
  jobsList: any;
  loader: Boolean = true;

  // cronForm: FormControl;

  poolTypeList: any;
  // jobsList: Array<any> = [];
  duplicateJobsList: Array<any> = [];
  chainjobList: Array<any> = [];
  // userName: string;

  constructor(private dialog: MatDialog, private formBuilder: FormBuilder, private apis: DataLakesService, private snackBar: MatSnackBar ){
    this.scheduleForm = this.formBuilder.group({
      jobType: ['',[Validators.required]],
      scheduleName: ['',[Validators.required]],
      jobId: ['',[Validators.required]],
      frequency: ['',[Validators.required]],
      cronForm: [this.cronExpression,[Validators.required]],
      nextRunDate: ['',[Validators.required]],
      hour: ['',[Validators.required]],
      minute: ['',[Validators.required]],
      createdBy: [''],
      jobPoolType: ['']
    })
  }

  ngOnInit() {
    // this.userName = localStorage.getItem('userName');
    if(localStorage.getItem('userRole')=='BUSINESS') {
      this.tableTypeList = [
        // { name: "BAPI", value: "bapi" },
        { name: "Table", value: "table" },
        { name: 'Jobchain', value: 'jobchain'}
      ];
      this.tableType = "bapi";
      this.poolTypeList = [
        { name: "Table", value: "table" },
      ];
    }
    else {
      this.tableTypeList = [
        { name: "Extractor", value: "extractor" },
        // { name: "BAPI", value: "bapi" },
        { name: "Table", value: "table" },
        { name: 'Jobchain', value: 'jobchain'}
      ];
      this.tableType = "extractor";
      this.poolTypeList = [
        { name: "Extractor", value: "extractor" },
        { name: "Table", value: "table" }
      ];
    }
    this.changeTableType();
  }

  applyFilter() {
    this.scheduleList.filter = this.searchKey.trim().toLowerCase();
  }

  onSearchClear() {
    this.searchKey = "";
    this.applyFilter();
  }
  
  schedularModel(templateRef: TemplateRef<any>, title) {
    this.scheduleForm.reset();
    this.scheduleForm.enable();
    this.minDate = new Date();
    // this.maxDate = new Date(2020, 7, 20);
    this.modelTitle = title
    this.scheduleForm.controls['jobType'].setValue(this.tableType);
    if(title == 'Edit'){
      
      console.log("In editSchedule",this.selectedScheduleData);
      this.scheduleForm.patchValue({
        jobType: this.selectedScheduleData.jobtype,
        scheduleName: this.selectedScheduleData.jobName,
        jobId: this.selectedScheduleData.jobId,
        frequency: this.selectedScheduleData.frequency,
        nextRunDate: new Date(this.selectedScheduleData.scheduledDate),
        hour: new Date(this.selectedScheduleData.scheduledDate).getHours(),
        minute: new Date(this.selectedScheduleData.scheduledDate).getMinutes(),
        cronForm: this.selectedScheduleData.cronDate,
        createdBy: this.selectedScheduleData.createdBy
      });
      // this.scheduleForm.controls['jobType'].disable();
      // this.scheduleForm.controls['scheduleName'].disable();
      // this.scheduleForm.controls['jobId'].disable();
      this.changeFrequency1();
      
    }
    this.getJobsList(this.scheduleForm.value.jobType);
    this.dialog.open(templateRef, {minWidth: '600px'});
  }

  changeTableType() {
    this.loader = true;
    if(this.tableType=='bapi') {
      this.scheduleList = []; // new MatTableDataSource(res);
      this.scheduleList.paginator = this.paginator;
      this.loader = false;
    }
    else if(this.tableType=='extractor') {
      this.getScheduledData();
    }
    else if(this.tableType=='table') {
      this.scheduleList = []; // new MatTableDataSource(res);
      this.scheduleList.paginator = this.paginator;
      this.loader = false;
    }
  }


  getScheduledData() {
    this.scheduleList = new MatTableDataSource([]);
    this.apis.getApi(environment.getSchedularList+localStorage.getItem('userRole')+'/'+localStorage.getItem('userName')).subscribe(res => {
      this.scheduleList = new MatTableDataSource(res);
      this.scheduleList.paginator = this.paginator;
      console.log("getScheduledData result: ", this.scheduleList);
      this.loader = false;
    });
  }


  getJobsList(type) {
    console.log("In getJobsList: ",type);
    let api;
    if(type=='bapi') {
      api = environment.getBapi;
      this.apis.getApi(api+localStorage.getItem('userRole')+'/'+localStorage.getItem('userName')).subscribe(res => {
        console.log("getJobsList result: ", res);
          this.jobsList = res;
      });
    }
    else if(type=='extractor') {
      api = environment.getExtractor+'all/';
      this.apis.getApi(api+localStorage.getItem('userRole')+'/'+localStorage.getItem('userName')).subscribe(res => {
          console.log("getJobsList result: ", res);
          if (res.Status=='OK') {
            this.jobsList = res.Data;
          }
        });
    }
    else if(type=='table') {
      api = environment.getTable+'all/';
      this.apis.getApi(api+localStorage.getItem('userRole')+'/'+localStorage.getItem('userName')).subscribe(res => {
        console.log("getJobsList result: ", res);
          this.jobsList = res;
      });
    }
    else if(type=='jobchain') {
      api = environment.jobchains;
      this.apis.getApi(api).subscribe(res => {
        console.log("getJobsList result: ", res);
          this.jobsList = res;
      });
    }

    // this.apis.getApi(api+localStorage.getItem('userRole')+'/'+localStorage.getItem('userName')).subscribe(res => {
    //   console.log("getJobsList result: ", res);
    //   if (res.Status=='OK') {
    //     this.jobsList = res.Data;
    //   }
    // });
    
  }

  addUpdateSchedule(type) {
    let runDate = this.scheduleForm.value.nextRunDate;
    console.log("date: ",runDate);
    let params = {
      // "createdAt": "2020-08-04T13:34:56.248Z",
      "cronDate": this.scheduleForm.value.cronForm || null,
      "frequency": this.scheduleForm.value.frequency,
      // "id": this.selectedScheduleData.id || '',
      "jobId": this.scheduleForm.value.jobId,
      "jobName": this.scheduleForm.value.scheduleName,  //Why we need again?
      "jobtype": this.scheduleForm.value.jobType,
      "scheduledDate": new Date(runDate).getFullYear() + '-' + ((new Date(runDate).getMonth()+1)<10? '0'+(new Date(runDate).getMonth()+1) : new Date(runDate).getMonth()+1) + '-' + new Date(runDate).getDate() + ' ' + (this.scheduleForm.value.hour<10 ? '0'+this.scheduleForm.value.hour : this.scheduleForm.value.hour) +':'+ (this.scheduleForm.value.minute<10 ? '0'+this.scheduleForm.value.minute : this.scheduleForm.value.minute),
      // "status":this.selectedScheduleData.status || null
      "createdBy": localStorage.getItem('userName'),
    }
    console.log("Params: ",params);
    if(type=='Create') {
      params.createdBy = localStorage.getItem('userName');
      this.apis.postApi(environment.getSchedularList,params).subscribe(res => {
        console.log("addSchedule result: ", res);
        this.snackBar.open("Schedule added successfully.", '', { duration:  5000, verticalPosition: "top" });
        this.changeTableType();
      });
    }
    else if (type='Edit') {
      params.createdBy = this.selectedScheduleData.createdBy;
      this.apis.putApi(environment.getSchedularList+this.selectedScheduleData.id,params).subscribe(res => {
        console.log("UpdateSchedule result: ", res);
        this.snackBar.open("Schedule updated successfully.", '', { duration:  5000, verticalPosition: "top" });
        this.changeTableType();
      });
    }
    
  }

  changeFrequency1() {
    if(this.scheduleForm.value.frequency=='once') {
      this.scheduleForm.controls.nextRunDate.setValidators(Validators.required);
      this.scheduleForm.controls.nextRunDate.updateValueAndValidity();
      this.scheduleForm.controls.hour.setValidators(Validators.required);
      this.scheduleForm.controls.hour.updateValueAndValidity();
      this.scheduleForm.controls.minute.setValidators(Validators.required);
      this.scheduleForm.controls.minute.updateValueAndValidity();
      this.scheduleForm.controls.cronForm.clearValidators();
      this.scheduleForm.controls.cronForm.updateValueAndValidity();
    }else {
      this.scheduleForm.controls.nextRunDate.clearValidators();
      this.scheduleForm.controls.nextRunDate.updateValueAndValidity();
      this.scheduleForm.controls.hour.clearValidators();
      this.scheduleForm.controls.hour.updateValueAndValidity();
      this.scheduleForm.controls.minute.clearValidators();
      this.scheduleForm.controls.minute.updateValueAndValidity();
      this.scheduleForm.controls.cronForm.setValidators(Validators.required);
      this.scheduleForm.controls.cronForm.updateValueAndValidity();
    }
    // this.scheduleForm.patchValue({
    //   // jobType: this.tableType,
    //   // scheduleName: this.selectedScheduleData.jobName,
    //   // jobId: this.selectedScheduleData.jobId,
    //   // frequency: this.selectedScheduleData.frequency,
    //   nextRunDate: this.selectedScheduleData.scheduledDate==undefined ? '' : new Date(this.selectedScheduleData.scheduledDate),
    //   hour: this.selectedScheduleData.scheduledDate==undefined ? '' : new Date(this.selectedScheduleData.scheduledDate).getHours(),
    //   minute: this.selectedScheduleData.scheduledDate==undefined ? '' : new Date(this.selectedScheduleData.scheduledDate).getMinutes(),
    //   cronForm: this.selectedScheduleData.cronDate==undefined ? '' :  this.selectedScheduleData.cronDate
    // });

    
  }

  generateArray(n: number): any[] {
    return Array(n);
  }

  executeSchedule() {
    console.log("In executeSchedule: ",this.selectedScheduleData);
    this.snackBar.open("Schedule sent to SAP successfully.", '', { duration:  5000, verticalPosition: "top" });
    this.apis.postApi(environment.getSchedularList+'jobschedule/'+localStorage.getItem('userName'),this.selectedScheduleData).subscribe(res => {
      console.log("executeSchedule result: ", res);
      this.snackBar.open("Schedule has executed successfully.", '', { duration:  5000, verticalPosition: "top" });
    });
  }

  deleteSchedule() {
    console.log("In deleteSchedule: ",this.selectedScheduleData);
    this.apis.deleteApi(environment.getSchedularList+this.selectedScheduleData.id).subscribe(res => {
      console.log("deleteSchedule result: ", res);
      this.changeTableType();
      this.snackBar.open("Schedule deleted successfully.", '', { duration:  5000, verticalPosition: "top" });
    });
  }



  test() {
    let paramV = {
      jobType: this.scheduleForm.controls.jobType.valid,
      scheduleName: this.scheduleForm.controls.scheduleName.valid,
      jobId: this.scheduleForm.controls.jobId.valid,
      frequency: this.scheduleForm.controls.frequency.valid,
      nextRunDate: this.scheduleForm.controls.nextRunDate.valid,
      hour: this.scheduleForm.controls.hour.valid,
      minute: this.scheduleForm.controls.minute.valid,
      cronForm: this.scheduleForm.controls.cronForm.valid
    }
    console.log("paramV: ",paramV);
    console.log("Form: ",this.scheduleForm.valid);
  }


  
}
