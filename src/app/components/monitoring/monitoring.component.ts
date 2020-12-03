import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { MatTableDataSource, MatPaginator, MatDialog, } from '@angular/material';
import { FormBuilder } from '@angular/forms';
import { environment } from 'src/environments/environment';
import { DataLakesService } from 'src/app/services/data-lakes.service';
import { WebSocketService } from 'src/app/services/web-socket.service';

@Component({
  selector: 'app-monitoring',
  templateUrl: './monitoring.component.html',
  styleUrls: ['./monitoring.component.css']
})
export class MonitoringComponent implements OnInit {
  // @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild('MonitorPageinator', { static: true }) monitorPageinator: MatPaginator;
  @ViewChild('FetchedPageinator', { static: true }) fetchedPageinator: MatPaginator;

  tableHeaders: string[] = ['#', 'job_Name', 'dated', 'instance', 'duration', 'status', 'actions'];
  logsList: MatTableDataSource<any>;
  // tiles: any;
  jobName: string = '';
  createdBy: string = '';
  jobStatus: Array<string> = ['Finished', 'Cancelled', 'In Progress'];

  toMinDate: Date;
  maxDate: Date = new Date();
  fromDate: Date;
  toDate: Date;
  selectedInstance: any;
  fullLogs: any;
  updateJobLogs: any;

  tableLoader: Boolean = true;
  logLoader: Boolean = true;

  webSocket: WebSocketService;
  userRole: string = '';

  fetchedTableHeader: any;
  fetchedData: any;
  fetchedShow: Boolean = false;

  constructor(private dialog: MatDialog, private formBuilder: FormBuilder, private apis: DataLakesService) { }

  ngOnInit() {
    // this.logsList = new MatTableDataSource(LIST_DATA);
    // this.logsList.paginator = this.paginator;
    // console.log(this.logsList);
    // this.updateJobLogs = setInterval(() => {
    //   console.log("In log Interval");
    //   this.apis.getApi(environment.getLogsFilterList+'logsUpdate').subscribe(res => {
    //     console.log("updateJobLogs result: ", this.fullLogs);
    //   });
    //   this.getLogsData();
    // }, 10000);
    this.userRole = localStorage.getItem('userRole');
    if( this.userRole!='BUSINESS') {
      this.tableHeaders = ['#', 'job_Name', 'dated', 'instance', 'duration', 'status', 'createdby', 'actions'];
      
    } else {
      this.tableHeaders = ['#', 'job_Name', 'dated', 'instance', 'duration', 'status', 'actions'];
    }
    this.fetchedTableHeader = ['#', 'jobName', 'instanceid', 'recordsFromURL', 'recordsFromDb', 'time'];

    // this.getUpdateLogs();
    this.getLogsData();
    this.getFetchedData();
    this.webSocket = new WebSocketService();
    // this.webSocket._connect();
    this.webSocket.monitorCompMethodCalled$.subscribe((message) => {
      console.log("In monitorComp : ", message);
      // if(this.selectedInstance.job_Name==message.jobName) {
      //   this.fullLogs.message = message.status
      // }
    });
  }

  ngOnDestroy() {
    if (this.updateJobLogs) {
      clearInterval(this.updateJobLogs);
    }
  }

  applyFilter() {
    this.logsList.filter = this.jobName.trim().toLowerCase();
  }

  onSearchClear() {
    this.jobName = "";
    this.applyFilter();
  }

  monitoringModel(templateRef: TemplateRef<any>) {
    this.logLoader = true;
    this.fullLogs = [];
    this.getFullLogsOfJob();
    this.dialog.open(templateRef, { width: '800px', backdropClass: 'backdropBackground' });
  }

  changeMaxDate() {
    this.toMinDate = new Date(this.fromDate.getFullYear(), this.fromDate.getMonth(), this.fromDate.getDate() - 1);
  }

  // getUpdateLogs() {
  //   this.tableLoader = true;
  //   this.apis.getApi(environment.getLogsFilterList + 'logsUpdate').subscribe(res => {
  //     console.log("updateJobLogs result: ", this.fullLogs);
  //   });
  //   this.getLogsData();
  // }

  getLogsData() {
    console.log("jobStatus: ", this.jobStatus);
    console.log("fromDate - ",this.fromDate,", toDate - ",this.toDate);
    
    let params = {
      "role": this.userRole,
      "createdby": this.userRole=='BUSINESS'? localStorage.getItem('userName') : this.createdBy,
      "startdate": this.fromDate==undefined? new Date('1970-1-1') : this.fromDate,
      "enddate": this.toDate==undefined? new Date() : this.toDate,
      "status": this.jobStatus
    }
    console.log("getLogsData params: ",params);
    this.apis.postApi(environment.getLogsList, params).subscribe(res => {
      console.log("getLogsData res: ", res);
      this.logsList = new MatTableDataSource(res.sort(this.getSortOrder("instance_id")));
      this.logsList.paginator = this.monitorPageinator;
      this.applyFilter();
      this.tableLoader = false;
    })

    // let allLogs = [];
    // (this.jobStatus).forEach((status, index) => {
    //   console.log("API call - ", index);
    //   this.apis.getApi(environment.getLogsList + status + '/' + localStorage.getItem('userRole') + '/' + localStorage.getItem('userName')).subscribe(res => {
    //     allLogs = allLogs.concat(res);
    //     console.log("getLogsData res: ", res);
    //     this.logsList = new MatTableDataSource(allLogs.sort(this.getSortOrder("instance_id")));
    //     this.logsList.paginator = this.paginator;
    //     console.log("getLogsData result: ", this.logsList);
    //     this.tableLoader = false;
    //   });
    // });


  }

  getSortOrder(prop) {
    return function (a, b) {
      if (Number(a[prop]) < Number(b[prop])) {
        return 1;
      } else if (Number(a[prop]) > Number(b[prop])) {
        return -1;
      }
      return 0;
    }
  }


  filterLogsdata() {
    let fromDate = this.fromDate.getFullYear() + '-' + (this.fromDate.getMonth()+1) + '-' + this.fromDate.getDate();
    let toDate = this.toDate.getFullYear() + '-' + (this.toDate.getMonth()+1) + '-' + this.toDate.getDate();

    // No createdBy in API
    let api = '';
    if(this.jobName!='') {
      api = environment.getLogsFilterList + 'job_Name/' + this.jobName + '/status/' + this.jobStatus + '/date/' + fromDate + '/end/' + toDate + '/' + localStorage.getItem('userRole') + '/' + localStorage.getItem('userName');
    } else {
      api = environment.getLogsFilterList + 'status/' + this.jobStatus + '/date/' + fromDate + '/end/' + toDate + '/' + localStorage.getItem('userRole') + '/' + localStorage.getItem('userName')
    }
    this.apis.getApi(api).subscribe(res => {
      this.logsList = new MatTableDataSource(res);
      this.logsList.paginator = this.monitorPageinator;
      console.log("getLogsData with filter result: ", this.logsList);
    });
  }

  getFullLogsOfJob() {
    this.apis.getApi(environment.getFullLogs + this.selectedInstance.instance_id).subscribe(res => {
      this.fullLogs = res;
      console.log("getFullLogsOfJob result: ", this.fullLogs);
      this.logLoader = false;
    });
  }

  getFetchedData() {
    this.apis.getApi(environment.fetched).subscribe(res => {
      console.log("getFetchedData result: ", res);
      // this.fetchedTableHeader = Object.keys(res.Data[0]);
      this.fetchedData = new MatTableDataSource(res.Data);
      this.fetchedData.paginator = this.fetchedPageinator;
      this.fetchedShow = true;
    });
  }


}
