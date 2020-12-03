import { Component, OnInit, ViewChild, TemplateRef, ElementRef } from '@angular/core';
import {
  MatPaginator,
  MatTableDataSource,
  MatDialog,
  MatAutocompleteSelectedEvent,
  MatSnackBar,
} from '@angular/material';
import { FormGroup, FormBuilder, Validators, FormArray, FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { startWith, map, mergeAll } from 'rxjs/operators';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Router } from "@angular/router";
import { saveAs } from 'file-saver';
// import { element } from 'protractor';
import { environment } from 'src/environments/environment';
import { DataLakesService } from 'src/app/services/data-lakes.service';

import { WebSocketService } from 'src/app/services/web-socket.service';
import { element } from 'protractor';
import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';

@Component({
  selector: 'app-jobs',
  templateUrl: './jobs.component.html',
  styleUrls: ['./jobs.component.css']
})

export class JobsComponent implements OnInit {
  @ViewChild('JobPageinator', { static: true }) jobPageinator: MatPaginator;
  @ViewChild('PreviewPageinator', { static: false }) previewPageinator: MatPaginator;

  webSocket: WebSocketService;
  jobLogs: any = [];
  jobLogsClose: Boolean = false;
  receivedData: any;
  jobsHeaders: string[];

  // bapiHeaders: string[] = ['#', 'name', 'function', 'createdAt', 'actions'];
  // extractorHeaders: string[] = ['#', 'name', 'function', 'targetType', 'targetName', 'createdAt', 'actions'];
  // tableHeaders: string[] = ['#', 'name', 'table', 'createdAt', 'actions'];

  jobsTableData: MatTableDataSource<any>;

  searchKey: string = null;
  minDate: Date = new Date();
  maxDate: Date;
  fromDate: Date = null;
  toDate: Date = null;

  jobForm: FormGroup;
  bapiExcuteForm: FormGroup;
  categoryForm: FormGroup;

  tableTypeList: Array<any> = [];
  tableType: string = 'extractor';
  selectedJobData: any;
  modelTitle: string = '';

  mainSourceSystemList: any;
  sourceSystemList: any = [];
  mainFunctionList: any = [];
  functionList: any = []
  filteredFunctionsList: Observable<string[]>;
  bapiFunctionList: Array<string> = ['BAPI_USER_GET_DETAIL', 'BAPI_COMPANYCODE_GETLIST']
  fullloadExtractors: Array<string> = [
    '0ACCOUNT_ATTR',
    '0CHRT_ACCTS_ATTR',
    '0CO_AREA_ATTR',
    '0COMP_CODE_ATTR',
    '0COSTCENTER_ATTR',
    '0PLANT_ATTR',
    '0AC_DOC_TYP_ATTR',
    '0AC_DOC_TYP_ATTR',
    '0ACCACCOUNT_ATTR',
    '0C_CTR_AREA_ATTR',
    '0COND_USAGE_ATTR',
    '0COSTCOMP_ATTR',
    '0COSTOBJ_ATTR',
    '0COUNTRY_ATTR',
    '0INSP_LOT_2_ATTR',
    '0INSP_LOT_ATTR',
    '0MAT_UNIT_ATTR',
    '0PRODORDER_ATTR',
    '0SALESORG_ATTR',
    '0SERVICE_ATTR',
    '0STKEYFIG_ATTR',
    '0FI_AR_50',
    '0FI_AP_51',
    '0FI_ACDOCA_20'
  ];



  targetFilesList: any = [];
  tableNameList: string[] = [];
  filteredTableNameList: Observable<string[]>;

  separatorKeysCodes: number[] = [COMMA];
  filteredColumnsList: Observable<string[]>;
  columnsChipList: string[] = [];
  chipRemove: Boolean = true;
  columnsList: string[] = [];
  modeOfExtractorList: any;
  targetSystem: Array<string>;

  jobTypeDisable: Boolean = false;

  bapiExecuteParameters: any = [];
  bapiExportTable: any = [];
  bapiExportParams: any = [];
  bapiExecuteParamsList: any = [];

  tableLoader: Boolean = true;
  paramLoader: Boolean = true;

  jobExeStep: string = '1';
  foregroundLoader: Boolean = false;
  modelloader: Boolean = true;

  sapUserId: string = '';
  sapPassword: string = '';
  hide: Boolean = true;
  sapCredentials: Boolean = true;
  userRole: string = '';
  selectAllToggle: Boolean = false;
  tableCredInvalid: Boolean = false;

  previewTableHeaders: Array<any> = [];
  previewData: MatTableDataSource<any>;
  previewShow: Boolean = false;

  sourcetypeList: Array<any> = [];
  categoryExtractorList: Array<any> = [];
  categoryTableList: Array<any> = [];
  tableCategoryType: string = 'all';
  prevTableType: string = '';
  @ViewChild('columnInput', { static: false }) columnInput: ElementRef<HTMLInputElement>;
  // @ViewChild('auto', {static: false}) matAutocomplete: MatAutocomplete;

  constructor(private dialog: MatDialog, private formBuilder: FormBuilder, private apis: DataLakesService, private router: Router, private snackBar: MatSnackBar) {
    this.jobForm = this.formBuilder.group({
      jobType: ['', [Validators.required]],
      jobName: ['', [Validators.required]],
      description: ['', [Validators.required]],
      category: ['', [Validators.required]],
      sourceType: ['', [Validators.required]],
      functionName: ['', [Validators.required]],
      modeOfExtractor: ['', [Validators.required]],
      sourceSystem: ['', [Validators.required]],
      targetSystem: ['', [Validators.required]],
      targetName: [''],
      tableName: ['', [Validators.required]],
      rowsCount: ['', [Validators.required]],
      columns: [''],
      filters: this.formBuilder.array([this.filtersArrayForm()]),  //this.filtersArrayForm()
      createdBy: [],
    });

    this.categoryForm = this.formBuilder.group({
      catergoryName: ['', [Validators.required]],
      categoryType: ['', [Validators.required]],
      categoryDescription: ['', [Validators.required]],
    })
  }

  ngOnInit() {

    this.userRole = localStorage.getItem('userRole')
    if (this.userRole == 'BUSINESS') {
      this.tableTypeList = [
        { name: "BAPI", value: "bapi" },
        { name: "TABLE", value: "table" }
      ];
      this.tableType = "bapi";
    }
    else {
      this.tableTypeList = [
        { name: "EXTRACTOR", value: "extractor" },
        { name: "BAPI", value: "bapi" },
        { name: "TABLE", value: "table" }
      ];
      this.tableType = "extractor";
    }

    this.sourcetypeList = ['ECC', ' S/4 HANA', 'BW', 'ORACLE'];

    this.targetSystemChange();
    this.getSourceSystemList();
    this.getCategoryList();
    setTimeout(() => this.tableTypeChange(), 2000);


    console.log("executeForeground: ", localStorage.getItem('executeForeground'))
    if (localStorage.getItem('executeForeground') == 'true') {
      this.foregroundLoader = true;
      // this.executeMethod('Foreground', ForegroundLogsDialog<TemplateRef>)
    }
    else {
      this.foregroundLoader = false;
    }
    this.webSocket = new WebSocketService();
    this.webSocket._connect();
    this.jobLogs = [];
    // this.webSocket.receivedMessage.subscribe(message => {
    //   console.log("On message change...!")
    //   if (message !== this.receivedData) {
    //       this.receivedData = message;
    //       this.jobLogs = message.status;
    //       if(message.jobStatus=='Finished') {
    //         this.jobLogsClose = true;
    //         localStorage.setItem('executeForeground', 'false');
    //         localStorage.setItem('foreGroundJob', '');
    //       }
    //   }
    // });

    this.webSocket.jobCompMethodCalled$.subscribe((message) => {
      message = (JSON.parse(message));
      // console.log("In jobComp socket variable : ",this.webSocket.receivedMessage.jobName);
      console.log("In jobComp message : ", message);
      if (localStorage.getItem('foreGroundJob') == message.jobName) {
        console.log("In Same Job...!");
        this.jobLogs = message.status;
        if (message.jobStatus == 'Finished' || message.jobStatus == 'Cancelled') {
          console.log("In Finished/Cancelled Job...!")
          this.jobLogsClose = true;
          this.foregroundLoader = false;
          localStorage.setItem('executeForeground', 'false');
          localStorage.setItem('foreGroundJob', '');
        }
        // console.log("jobLogs : ",this.jobLogs);
      }
      // else {
      //   console.log("jobLogs else : ",this.selectedJobData.name,' - ',message.jobName);
      // }
    });
  }


  disconnect() {
    this.webSocket._disconnect();
  }

  // sendMessage(){
  //   this.webSocket._send(this.name);
  // }

  // handleMessage(message){
  //   this.greeting = message;
  // }

  onSearchClear() {
    this.searchKey = "";
    this.applyFilter();
  }

  applyFilter() {
    this.jobsTableData.filter = this.searchKey.trim().toLowerCase();
  }

  // add(event: MatChipInputEvent): void {
  //   // Add column only when MatAutocomplete is not open
  //   // To make sure this does not conflict with OptionSelected Event
  //   if (!this.matAutocomplete.isOpen) {
  //     const input = event.input;
  //     const value = event.value;

  //     // Add our column
  //     if ((value || '').trim()) {
  //       this.columnsChipList.push(value.trim());
  //     }

  //     // Reset the input value
  //     if (input) {
  //       input.value = '';
  //     }

  //     this.fruitCtrl.setValue(null);
  //   }
  // }

  // remove(column: string): void {
  //   const index = this.columnsChipList.indexOf(column);

  //   if (index >= 0) {
  //     this.columnsChipList.splice(index, 1);
  //   }
  // }

  // selected(event: MatAutocompleteSelectedEvent): void {
  //   this.columnsChipList.push(event.option.viewValue);
  //   this.columnInput.nativeElement.value = '';
  //   this.jobForm.controls.columns.setValue(null);
  // }

  // private _filterColumnName(value: string): string[] {
  //   const filterValue = value.toLowerCase();
  //   return this.columnsList.filter(column => column.toLowerCase().indexOf(filterValue) === 0);
  // }

  private _filterTableName(value: string): string[] {
    console.log("In _filterTableName");
    const filterValue = value.toLowerCase();
    return this.tableNameList.filter(option => option.toLowerCase().indexOf(filterValue) === 0);
  }

  private _filterFunctionName(name: string): string[] {
    console.log("In _filterFunctionName");
    const filterValue = name.toLowerCase();
    return this.functionList.filter(fun => fun.toLowerCase().indexOf(filterValue) === 0);
  }


  jobModel(templateRef: TemplateRef<any>, title) {
    this.jobForm.reset();
    this.jobForm.enable();
    this.modelTitle = title;
    this.modelloader = false;
    let formArray = this.jobForm.controls.filters as FormArray;
    for (let i = formArray.length - 1; i >= 0; i--) {
      console.log("formArray: ", i);
      formArray.removeAt(i)
    }
    console.log("TableType: ", this.tableType);
    console.log("1. Job Type: ", this.jobForm.value.jobType);
    this.jobTypeDisable = false;
    if (title == 'Edit' || title == 'View') {
      this.modelloader = true;
      this.jobTypeDisable = true;
      console.log("Element: ", this.selectedJobData);
      this.jobForm.patchValue({
        jobType: this.tableType,
        jobName: this.selectedJobData.name,
        description: this.selectedJobData.description,
        category: this.selectedJobData.category,
        functionName: this.selectedJobData.function,
        modeOfExtractor: this.selectedJobData.targetType,
        sourceType: this.selectedJobData.sourceType,
        sourceSystem: this.selectedJobData.sourceId,
        targetSystem: this.selectedJobData.targetId,  //  targetName
        // targetName: this.selectedJobData.targetId,
        tableName: this.selectedJobData.table,
        rowsCount: this.selectedJobData.rows,
        filters: this.selectedJobData.filteroptions || [],
        createdBy: this.selectedJobData.createdBy
      });
      this.targetSystem = [this.selectedJobData.targetName];

      if (this.tableType == 'table') {
        console.log("In Table____________")
        // this.jobForm.controls['filters'].setValue(this.selectedJobData.filteroptions);
        // this.jobForm.patchValue({
        //   filters: this.selectedJobData.filteroptions
        // });
        this.jobForm.controls['columns'].setValue(this.selectedJobData.fields.split(","));
        console.log("Columns: ", this.jobForm.value.columns)
        this.getTableNameList(this.selectedJobData.sourceId);   // SourceId not there we need to in API side
        this.getColumnNameList();

        // this.columnsChipList = ["MANDT", "VBELN", "POSNR", "MATNR"];  //this.selectedJobData.fields;
        // this.chipRemove = true;
        // this.selectedJobData.filter = [
        //   {
        //     cloumnName: "1",
        //     operator: ">",
        //     value: 1
        //   },
        //   {
        //     cloumnName: "2",
        //     operator: "<",
        //     value: 2
        //   }
        // ]

        // for (let i=0; i<this.selectedJobData.filter.length-1; i++) {
        //   console.log("Length",this.selectedJobData.filter.length, "and Index of fileter: ",i);
        //   var _filter = <FormArray>this.jobForm.controls.filters;
        //   if (this.jobForm.controls.filters.valid) { 
        //     _filter.push(
        //       this.filtersArrayForm()
        //     )
        //   }
        // }

        // this.jobForm.controls.filters.setValue(this.selectedJobData.filter);
      }

      // this.targetSystemChange();
      this.getModeofExtractor();
      this.filterSourceSystem(this.selectedJobData.sourceType);
      console.log("2. Job Type: ", this.jobForm.value.jobType);

      // this.jobForm.controls['jobType'].disable();
      // this.jobForm.controls['jobType'].setValue(this.tableType);
      // this.jobForm.get('jobType').setValue(this.tableType)
      this.jobForm.controls['jobName'].disable();

    } else {
      this.jobForm.controls['jobType'].setValue(this.tableType);
    }
    if (title == 'View') {
      this.chipRemove = false;
      this.jobForm.disable();
    }


    if (title == 'Edit' || title == 'View' || title == 'Create') {
      console.log("3. Job Type: ", this.jobForm.value.jobType);
      // this.jobTypeDisable = true;
      this.jobTypeChange();
      this.targetSystemChange();
      this.getCategoryList();
    }
    this.jobExeStep = '1';
    this.tableCredInvalid = false;
    this.dialog.open(templateRef, { width: '600px' });
  }


  tableTypeChange() {
    this.tableLoader = true;
    if(this.tableType != this.prevTableType) {
      this.tableCategoryType = 'all';
      this.prevTableType = this.tableType;
    }
    this.jobsTableData = new MatTableDataSource([]);
    console.log("In tableTypeChange - ", this.tableType);
    if (this.tableType == 'bapi') {
      this.getBapiData();
      this.functionList = this.bapiFunctionList;
    } else if (this.tableType == 'extractor') {
      this.getExtractorData();
      this.getExetractorFunList();
    } else if (this.tableType == 'table') {
      this.getTableData();
    }
  }

  getExetractorFunList() {
    this.apis.getApi(environment.getService).subscribe(res => {
      console.log("getServiceData result: ", res);
      if (res.Status == "OK") {
        // this.functionList = [];
        this.mainFunctionList = res.Data;
        // res.Data.forEach(element => {
        //   this.functionList.push(element.extractorName)
        // });
        console.log("mainFunctionList", this.mainFunctionList)
      }
    });
  }

  getBapiData() {
    if (this.userRole == 'BUSINESS') {
      this.jobsHeaders = ['#', 'description', 'name', 'function', 'actions'];
    } else {
      this.jobsHeaders = ['#', 'description', 'name', 'function', 'actions'];
    }

    this.apis.getApi(environment.getBapi + localStorage.getItem('userRole') + '/' + localStorage.getItem('userName')).subscribe(res => {
      console.log("getBapiData result: ", res);
      this.jobsTableData = new MatTableDataSource(res);
      this.jobsTableData.paginator = this.jobPageinator;
      this.tableLoader = false;
    });
  }

  getExtractorData() {
    if (this.userRole == 'BUSINESS') {
      this.jobsHeaders = ['#', 'description', 'name', 'function', 'targetType', 'targetName', 'actions'];
    } else {
      this.jobsHeaders = ['#', 'description', 'name', 'function', 'targetType', 'sourceId', 'targetName', 'actions'];
    }

    this.apis.getApi(environment.getExtractor + this.tableCategoryType +'/'+ localStorage.getItem('userRole') + '/' + localStorage.getItem('userName')).subscribe(res => {
      this.jobsTableData = new MatTableDataSource(res.Data);
      this.jobsTableData.paginator = this.jobPageinator;
      console.log("getExtractorData result: ", this.jobsTableData);
      this.tableLoader = false;
    });
  }

  getTableData() {
    if (this.userRole == 'BUSINESS') {
      this.jobsHeaders = ['#', 'description', 'name', 'table', 'targetName', 'actions'];
    } else {
      this.jobsHeaders = ['#', 'description', 'name', 'table', 'sourceId', 'targetName', 'actions'];
    }

    this.apis.getApi(environment.getTable + this.tableCategoryType +'/'+ localStorage.getItem('userRole') + '/' + localStorage.getItem('userName')).subscribe(res => {
      console.log("getTableData result: ", res);
      this.jobsTableData = new MatTableDataSource(res);
      this.jobsTableData.paginator = this.jobPageinator;
      this.tableLoader = false;
    });
  }

  getSourceSystemList() {
    this.apis.getApi(environment.getSourceSystemList).subscribe(res => {
      console.log("getSourceSystemList result: ", res);
      if (res.Status == 'OK') {
        this.mainSourceSystemList = res.Data;
      }
    });
  }

  getSourceSystemName(id) {
    let item = this.mainSourceSystemList.find(element => element.id == id);
    return item.systemId;
  }

  filterSourceSystem(sourceType) {
    this.sourceSystemList = [];
    this.mainSourceSystemList.forEach(element => {
      if (element.sourcetype == sourceType) {
        this.sourceSystemList.push(element);
      }
    });
    console.log("sourceSystemList: ", this.sourceSystemList);
  }

  filterFunctions(sourceSystemId) {
    this.getTableNameList(sourceSystemId);
    this.functionList = [];
    let sourceSystem;
    this.sourceSystemList.forEach(element => {
      if (element.id == sourceSystemId) {
        sourceSystem = element.systemId;
      }
    });
    this.mainFunctionList.forEach(element => {
      if (element.sourceSystem == sourceSystem) {
        this.functionList.push(element.extractorName);
      }
    });
    console.log("functionList: ", this.functionList);
  }

  getTableNameList(sourceId) {
    this.apis.getApi(environment.getTableNameList + sourceId).subscribe(res => {
      console.log("getTableNameList result: ", res);
      this.tableNameList = res;
    });
  }

  getColumnNameList() {
    // Check value is in List
    this.apis.getApi(environment.rootUrl + 'table/' + this.jobForm.value.tableName + '/columns/' + this.jobForm.value.sourceSystem).subscribe(res => {
      console.log("getColumnNameList result: ", res);
      this.columnsList = res;
    });
  }

  toggleSelectAllColumnName() {
    this.selectAllToggle = !this.selectAllToggle;
    if (this.selectAllToggle) {
      this.jobForm.controls.columns.patchValue([...this.columnsList.map(item => item), 'Select All']);
    } else {
      this.jobForm.controls.columns.patchValue([]);
    }
    console.log("Columns value : ", this.jobForm.value.columns);
  }

  targetSystemChange() {
    // this.targetFilesList = [];
    console.log("In targetSystemChange");
    // if (this.jobForm.value.targetSystem == 'ftp') {
    this.apis.getApi(environment.getFtpList).subscribe(res => {
      console.log("targetNameList azuredb result: ", res);
      // this.targetFilesList = res;
      if (res.Status == "OK") {
        res.Data.forEach(element => {
          this.targetFilesList.push({
            id: element.id,
            targetName: 'AzureDB_' + element.containerName
          });
        });
      }

      this.apis.getApi(environment.getDbList).subscribe(res => {
        console.log("targetNameList DB result: ", res);
        // this.targetFilesList = res;
        if (res.Status == "OK") {
          res.Data.forEach(element => {
            this.targetFilesList.push({
              id: element.id,
              targetName: 'DB_' + element.connectionName
            });
          });
          // this.targetFilesList.push({
          //   id: 0, targetName: "Local File"
          // })
        }

        this.modelloader = false;
        this.targetFilesList = this.uniqeList(this.targetFilesList, it => it.id);
      });
      console.log("targetFilesList: ", this.targetFilesList);
    });

    // }
    // else if (this.jobForm.value.targetSystem == 'db') {

    // }
    // else {
    //   // targetSystem == 'LocalFile'
    //   // targetName = ?
    //   this.jobForm.controls.targetName.clearValidators();
    //   this.jobForm.controls.targetName.updateValueAndValidity();
    // }
  }

  uniqeList(data, key) {
    return [
      ... new Map(
        data.map(x => [key(x), x])
      ).values()
    ]
  }

  getTargetSystemName(id) {
    let item = this.targetFilesList.find(element => element.id == id);
    return (item == undefined ? 'Local File' : item.targetName);
  }

  getModeofExtractor() {
    if (this.jobForm.value.jobType == 'extractor') {
      let isChecked = this.fullloadExtractors.find(element => element == this.jobForm.value.functionName);
      if (isChecked === undefined) {
        this.modeOfExtractorList = [
          { label: 'INIT W/ DATA', value: 'init w/ data' },
          // { label: 'INIT W/O DATA', value: 'intitializewithoutdata' },
          { label: 'FULL', value: 'full' },
          { label: 'DELTA', value: 'delta' },
          { label: 'STRUCTURE', value: 'structure' },
        ];
      }
      else {
        this.modeOfExtractorList = [
          { label: 'FULL', value: 'full' },
          { label: 'STRUCTURE', value: 'structure' }
        ];
      }
      console.log("modeOfExtractorList: ", this.modeOfExtractorList);
    }
  }

  filtersArrayForm() {
    return this.formBuilder.group({
      columnName: [''],
      operator: [''],
      columnValue: ['']
    });
  }

  addFilterForm() {
    console.log("In addFilterForm");
    if (this.jobForm.controls.filters.valid) {
      var _filter = <FormArray>this.jobForm.controls.filters;
      _filter.push(
        this.filtersArrayForm()
      )
    }
  }

  removeFilter(index) {
    var _filter = <FormArray>this.jobForm.controls.filters;
    if (_filter.controls.length > 1) {
      _filter.removeAt(index)
    }
  }

  searchJobs() {
    let params = {
      // jobName: this.searchKey,
      fromDate: this.fromDate,
      toDate: this.toDate
    }
    console.log("searchJobs params: ", params);
  }


  jobTypeChange() {
    console.log("In jobTypeChange: ", this.jobForm.value.jobType);
    if (this.jobForm.value.jobType == 'bapi') {
      console.log("In bapi job type");
      this.jobForm.controls.functionName.setValidators(Validators.required);
      this.jobForm.controls.functionName.updateValueAndValidity();
      this.jobForm.controls.modeOfExtractor.clearValidators();
      this.jobForm.controls.modeOfExtractor.updateValueAndValidity();
      this.jobForm.controls.tableName.clearValidators();
      this.jobForm.controls.tableName.updateValueAndValidity();
      this.jobForm.controls.rowsCount.clearValidators();
      this.jobForm.controls.rowsCount.updateValueAndValidity();
      this.jobForm.controls.columns.clearValidators();
      this.jobForm.controls.columns.updateValueAndValidity();
      this.jobForm.controls.category.clearValidators();
      this.jobForm.controls.category.updateValueAndValidity();
      let formArray = this.jobForm.controls.filters as FormArray;
      formArray.removeAt(0);
      this.functionList = this.bapiFunctionList;

      this.filteredFunctionsList = this.jobForm.controls.functionName.valueChanges.pipe(
        startWith(''),
        map((value: string | null) => value ? this._filterFunctionName(value) : this.functionList.slice())
      );

    } else if (this.jobForm.value.jobType == 'extractor') {
      this.jobForm.controls.functionName.setValidators(Validators.required);
      this.jobForm.controls.functionName.updateValueAndValidity();
      this.jobForm.controls.modeOfExtractor.setValidators(Validators.required);
      this.jobForm.controls.modeOfExtractor.updateValueAndValidity();
      this.jobForm.controls.category.setValidators(Validators.required);
      this.jobForm.controls.category.updateValueAndValidity();
      this.jobForm.controls.tableName.clearValidators();
      this.jobForm.controls.tableName.updateValueAndValidity();
      this.jobForm.controls.rowsCount.clearValidators();
      this.jobForm.controls.rowsCount.updateValueAndValidity();
      this.jobForm.controls.columns.clearValidators();
      this.jobForm.controls.columns.updateValueAndValidity();
      let formArray = this.jobForm.controls.filters as FormArray;
      formArray.removeAt(0);
      // this.functionList = this.extractorFunctionList;
      this.getExetractorFunList();

      this.filteredFunctionsList = this.jobForm.controls.functionName.valueChanges.pipe(
        startWith(''),
        map((value: string | null) => value ? this._filterFunctionName(value) : this.functionList.slice())
      );

    }
    else if (this.jobForm.value.jobType == 'table') {
      this.jobForm.controls.functionName.clearValidators();
      this.jobForm.controls.functionName.updateValueAndValidity();
      this.jobForm.controls.modeOfExtractor.clearValidators();
      this.jobForm.controls.modeOfExtractor.updateValueAndValidity();
      this.jobForm.controls.tableName.setValidators(Validators.required);
      this.jobForm.controls.tableName.updateValueAndValidity();
      this.jobForm.controls.rowsCount.setValidators(Validators.required);
      this.jobForm.controls.rowsCount.updateValueAndValidity();
      this.jobForm.controls.columns.setValidators(Validators.required);
      this.jobForm.controls.columns.updateValueAndValidity();
      this.jobForm.controls.category.setValidators(Validators.required);
      this.jobForm.controls.category.updateValueAndValidity();
      // if(this.modelTitle=='Create'){
      //   this.addFilterForm();
      // }

      this.filteredTableNameList = this.jobForm.controls.tableName.valueChanges.pipe(
        startWith(''),
        map(value => this._filterTableName(value))
      );
      // this.filteredColumnsList = this.jobForm.controls.columns.valueChanges.pipe(
      //   startWith(null),
      //   map((column: string | null) => column ? this._filterColumnName(column) : this.columnsList.slice()));
      // this.columnsChipList = [];
    }
  }

  // testJobTypeChange() {
  //   console.log("In testJobTypeChange: ",this.jobForm.value.jobType);
  // }

  test() {
    console.log("In test ");
    console.log("targetSystem--: ", this.targetSystem);
  }

  createUpdateJob() {
    console.log("Job Type: ", this.jobForm.value.jobType);
    console.log("targetSystem--: ", this.targetSystem);
    let params;
    if (this.jobForm.value.jobType == 'bapi') {
      params = {
        name: this.jobForm.value.jobName || this.selectedJobData.name,
        function: this.jobForm.value.functionName,
        sourceType: this.jobForm.value.sourceType,
        sourceId: this.jobForm.value.sourceSystem,
        targetName: this.targetSystem[0].toLowerCase(),
        targetId: this.jobForm.value.targetSystem,
        description: this.jobForm.value.description,
      }
    } else if (this.jobForm.value.jobType == 'extractor') {
      params = {
        name: this.jobForm.value.jobName || this.selectedJobData.name,
        function: this.jobForm.value.functionName,
        // modeOfExtractor: this.jobForm.value.modeOfExtractor,
        sourceType: this.jobForm.value.sourceType,
        sourceId: this.jobForm.value.sourceSystem,
        targetName: this.targetSystem[0].toLowerCase(),
        targetId: this.jobForm.value.targetSystem,
        targetType: this.jobForm.value.modeOfExtractor,
        description: this.jobForm.value.description,
        category: this.jobForm.value.category,
        // targetId: 
      }
    } else if (this.jobForm.value.jobType == 'table') {
      let columns = '';
      let columnsList = this.jobForm.value.columns;
      let index = columnsList.indexOf("Select All");
      if (index > -1) {
        columnsList.splice(index, 1);
      }
      columnsList.forEach(element => {
        columns = columns + element + ',';
      });
      columns = columns.slice(0, columns.length - 1);
      params = {
        name: this.jobForm.value.jobName || this.selectedJobData.name,
        sourceType: this.jobForm.value.sourceType,
        sourceId: this.jobForm.value.sourceSystem,
        targetId: this.jobForm.value.targetSystem,
        targetName: this.targetSystem[0].toLowerCase(),
        table: this.jobForm.value.tableName,
        fields: columns,
        rows: this.jobForm.value.rowsCount,
        filteroptions: this.jobForm.value.filters,
        description: this.jobForm.value.description,
        category: this.jobForm.value.category,
      }
    }

    console.log("createUpdateJob Params: ", params);
    if (this.modelTitle == 'Create') {
      params.createdBy = localStorage.getItem('userName').toUpperCase();
      this.apis.postApi(environment.rootUrl + this.jobForm.value.jobType + '/', params).subscribe(res => {
        if (res) {
          this.tableTypeChange();
          this.snackBar.open("Job added successfully.", '', { duration: 5000, verticalPosition: "top" });
          console.log("createJob res: ", res);
        }
      })
    }
    else if (this.modelTitle == 'Edit') {
      params.createdBy = this.selectedJobData.createdBy;
      this.apis.putApi(environment.rootUrl + this.jobForm.value.jobType + '/' + this.selectedJobData.id, params).subscribe(res => {
        if (res) {
          this.tableTypeChange();
          this.snackBar.open("Job updated successfully.", '', { duration: 5000, verticalPosition: "top" });
          console.log("updateJob res: ", res);
        }
      })
    }


    // <,>,<=,>=,=,<>


    // let paramsV = {
    //   jobType: this.jobForm.controls.jobType.valid,
    //   jobName: this.jobForm.controls.jobName.valid,
    //   functionName: this.jobForm.controls.functionName.valid,
    //   modeOfExtractor: this.jobForm.controls.modeOfExtractor.valid,
    //   sourceSystem: this.jobForm.controls.sourceSystem.valid,
    //   targetSystem: this.jobForm.controls.targetSystem.valid,
    //   targetName: this.jobForm.controls.targetName.valid,
    //   tableName: this.jobForm.controls.tableName.valid,
    //   rowsCount: this.jobForm.controls.rowsCount.valid,
    //   columns: this.jobForm.controls.columns.valid,
    //   filters: this.jobForm.controls.filters.valid

    // }
    // console.log("paramsV: ",paramsV);
    // console.log("Form valid: ",this.jobForm.valid);
  }

  deleteJob() {
    this.apis.deleteApi(environment.rootUrl + this.tableType + '/' + this.selectedJobData.id).subscribe(res => {
      console.log("Result of deleteJob: ", res);
      this.snackBar.open("Job deleted successfully.", '', { duration: 5000, verticalPosition: "top" });
      this.tableTypeChange();
    });

  }

  executeJob() {
    console.log("JobType: ", this.tableType);
    console.log("In executeJob: ", this.selectedJobData);
    if (this.tableType == 'bapi') {
      console.log("In BAPI executeJob");
    }
    else if (this.tableType == 'extractor') {
      console.log("In Extractor executeJob");
      if (this.selectedJobData.targetType == 'structure') {
        if (this.selectedJobData.targetName == 'lf') {
          this.snackBar.open(this.selectedJobData.name + " Job Initiated Successfully. For logs, go to Monitoring tab.", '', { duration: 5000, verticalPosition: "top" });
          this.apis.blobPostAPI(environment.extractorExecute + 'structure/' + localStorage.getItem('userName'), this.selectedJobData).subscribe(res => {
            console.log("Excute -> structure - lf  Blob result: ", res);
            let blob = new Blob([res], { type: 'application/vnd.ms-excel' });
            saveAs(blob, this.selectedJobData.name + '.xlsx');
            this.snackBar.open("File downloaded.", '', { duration: 5000, verticalPosition: "top" });
            this.foregroundLoader = false;
            localStorage.setItem('executeForeground', 'false');
            // '_' + new Date().getFullYear() + '-' + new Date().getDay() + '-' + new Date().getDate() + '_' + new Date().getHours() + '-' + new Date().getMinutes() + '-' + new Date().getSeconds()
          })
        }
        else {
          console.log("Structure execution available only for Local File Target");
          this.snackBar.open("Structure execution available only for Local File Target.", '', { duration: 5000, verticalPosition: "top" });
          this.foregroundLoader = false;
          localStorage.setItem('executeForeground', 'false');
        }
      }
      else if (this.selectedJobData.targetType == 'intitializewithoutdata') {
        if (this.selectedJobData.targetName == 'db') {
          this.snackBar.open(this.selectedJobData.name + " Job Initiated Successfully. For logs, go to Monitoring tab.", '', { duration: 5000, verticalPosition: "top" });
          this.apis.postApi(environment.extractorExecute + 'jobinitwodata/' + localStorage.getItem('userName'), this.selectedJobData).subscribe(res => {
            console.log("Excute -> jobinitwodata - db  result: ", res);
            if (res.Status == "OK") {
              this.snackBar.open(res.Jobname + " Job Completed Successfully.", '', { duration: 5000, verticalPosition: "top" });
            } else {
              this.snackBar.open(res.Jobname + " Execution Failed", '', { duration: 5000, verticalPosition: "top" });
            }
            this.foregroundLoader = false;
            localStorage.setItem('executeForeground', 'false');
          })
        }
        else {
          console.log("Target not available");
          this.snackBar.open("Target not available.", '', { duration: 5000, verticalPosition: "top" });
          this.foregroundLoader = false;
          localStorage.setItem('executeForeground', 'false');
        }
      }
      else if (this.selectedJobData.targetType == 'init w/ data') {
        if (this.selectedJobData.targetName == 'db') {
          this.snackBar.open(this.selectedJobData.name + " Job Initiated Successfully. For logs, go to Monitoring tab.", '', { duration: 5000, verticalPosition: "top" });
          this.apis.postApi(environment.extractorExecute + 'jobinitwdata/' + localStorage.getItem('userName'), this.selectedJobData).subscribe(res => {
            console.log("Excute -> jobinitwdata - db  result: ", res);
            if (res.Status == "OK") {
              this.snackBar.open(res.Jobname + " Job Completed Successfully.", '', { duration: 5000, verticalPosition: "top" });
            } else {
              this.snackBar.open(res.Jobname + " Execution Failed", '', { duration: 5000, verticalPosition: "top" });
            }
            this.foregroundLoader = false;
            localStorage.setItem('executeForeground', 'false');
          })
        }
        else {
          console.log("Target not available");
          this.snackBar.open("Target not available.", '', { duration: 5000, verticalPosition: "top" });
          this.foregroundLoader = false;
          localStorage.setItem('executeForeground', 'false');
        }
      }
      else if (this.selectedJobData.targetType == 'delta') {
        if (this.selectedJobData.targetName == 'azuredb') {
          this.snackBar.open(this.selectedJobData.name + " Job Initiated Successfully. For logs, go to Monitoring tab.", '', { duration: 5000, verticalPosition: "top" });
          this.apis.postApi(environment.extractorExecute + 'deltalocalftp/' + localStorage.getItem('userName'), this.selectedJobData).subscribe(res => {
            console.log("Excute -> delta - azuredb  result: ", res);
            // if(res.Status== "OK") {
            //   this.snackBar.open(res.Jobname+" Job Completed Successfully.", '', { duration:  5000, verticalPosition: "top" });
            // } else {
            //   this.snackBar.open(res.Jobname+" Execution Failed", '', { duration:  5000, verticalPosition: "top" });
            // }
            this.snackBar.open(res.Jobname + " Job Completed Successfully.", '', { duration: 5000, verticalPosition: "top" });
            this.foregroundLoader = false;
            localStorage.setItem('executeForeground', 'false');
          })
        }
        else if (this.selectedJobData.targetName == 'db') {
          this.snackBar.open(this.selectedJobData.name + " Job Initiated Successfully. For logs, go to Monitoring tab.", '', { duration: 5000, verticalPosition: "top" });
          this.apis.postApi(environment.extractorExecute + 'deltaload/' + localStorage.getItem('userName'), this.selectedJobData).subscribe(res => {
            console.log("Excute -> delta - db  result: ", res);
            if (res.Status == "OK") {
              this.snackBar.open(res.Jobname + " Job Completed Successfully.", '', { duration: 5000, verticalPosition: "top" });
            } else {
              this.snackBar.open(res.Jobname + " Execution Failed", '', { duration: 5000, verticalPosition: "top" });
            }
            this.foregroundLoader = false;
            localStorage.setItem('executeForeground', 'false');
          })
        }
        else if (this.selectedJobData.targetName == 'lf') {
          this.snackBar.open(this.selectedJobData.name + " Job Initiated Successfully. For logs, go to Monitoring tab.", '', { duration: 5000, verticalPosition: "top" });
          this.apis.blobPostAPI(environment.extractorExecute + 'deltalocalftp/' + localStorage.getItem('userName'), this.selectedJobData).subscribe(res => {
            console.log("Excute -> delta - lf  result: ", res);
            let blob = new Blob([res], { type: 'text/csv' });
            saveAs(blob, this.selectedJobData.name + '.csv');
            this.snackBar.open("File downloaded.", '', { duration: 5000, verticalPosition: "top" });
            this.foregroundLoader = false;
            localStorage.setItem('executeForeground', 'false');
          })
        }
      }
      else if (this.selectedJobData.targetType == 'full') {
        if (this.selectedJobData.targetName == 'db') {
          this.snackBar.open(this.selectedJobData.name + " Job Initiated Successfully. For logs, go to Monitoring tab.", '', { duration: 5000, verticalPosition: "top" });
          this.apis.postApi(environment.extractorExecute + 'fullload/' + localStorage.getItem('userName'), this.selectedJobData).subscribe(res => {
            console.log("Excute -> full - db  result: ", res);
            if (res.Status == "OK") {
              this.snackBar.open(res.Jobname + " Job Completed Successfully.", '', { duration: 5000, verticalPosition: "top" });
            } else {
              this.snackBar.open(res.Jobname + " Execution Failed", '', { duration: 5000, verticalPosition: "top" });
            }
            this.foregroundLoader = false;
            localStorage.setItem('executeForeground', 'false');
          })
        }
        // if (this.selectedJobData.targetName == 'azuredb' || this.selectedJobData.targetName == 'lf')
        else {
          console.log("full - azuredb/lf ");
          this.snackBar.open(this.selectedJobData.name + " Job Initiated Successfully. For logs, go to Monitoring tab.", '', { duration: 5000, verticalPosition: "top" });
          this.apis.postApi(environment.extractorExecute + 'localftpfullload/' + localStorage.getItem('userName'), this.selectedJobData).subscribe(res => {
            console.log("Excute -> full - azuredb/lf  result: ", res);
            if (res.Status == "OK") {
              this.snackBar.open(res.Jobname + " Job Completed Successfully.", '', { duration: 5000, verticalPosition: "top" });
            } else {
              this.snackBar.open(res.Jobname + " Execution Failed", '', { duration: 5000, verticalPosition: "top" });
            }
            this.foregroundLoader = false;
            localStorage.setItem('executeForeground', 'false');
          })
        }
      }


    }
    else if (this.tableType == 'table') {
      console.log("In Table executeJob");
      if (this.selectedJobData.targetName == 'lf') {
        console.log("In Table LF");
        this.snackBar.open(this.selectedJobData.name + " Job Initiated Successfully. For logs, go to Monitoring tab.", '', { duration: 5000, verticalPosition: "top" });
        this.apis.blobPostAPI(environment.getTable + 'local', this.selectedJobData).subscribe(res => {
          console.log("Excute -> lf  result: ", res);
          let blob = new Blob([res], { type: 'application/vnd.ms-excel' });
          saveAs(blob, this.selectedJobData.name + '.xlsx');
          this.snackBar.open("File downloaded.", '', { duration: 5000, verticalPosition: "top" });
          this.foregroundLoader = false;
          localStorage.setItem('executeForeground', 'false');
        })
      }
      else if (this.selectedJobData.targetName == 'db') {
        console.log("In Table DB");
        this.snackBar.open(this.selectedJobData.name + " Job Initiated Successfully. For logs, go to Monitoring tab.", '', { duration: 5000, verticalPosition: "top" });
        this.apis.postApi(environment.getTable + 'mssql', this.selectedJobData).subscribe(res => {
          console.log("Excute -> db  result: ", res);
          if (res.Status == "OK") {
            this.snackBar.open(res.Jobname + " Job Completed Successfully.", '', { duration: 5000, verticalPosition: "top" });
          } else {
            this.snackBar.open(res.Jobname + " Execution Failed", '', { duration: 5000, verticalPosition: "top" });
          }
          this.foregroundLoader = false;
          localStorage.setItem('executeForeground', 'false');
        })
      }
      else if (this.selectedJobData.targetName == 'azuredb') {
        console.log("In Table azuredb");
        this.snackBar.open(this.selectedJobData.name + " Job Initiated Successfully. For logs, go to Monitoring tab.", '', { duration: 5000, verticalPosition: "top" });
        this.apis.postApi(environment.getTable + 'azuredb', this.selectedJobData).subscribe(res => {
          console.log("Excute -> db  result: ", res);
          if (res.Status == "OK") {
            this.snackBar.open(res.Jobname + " Job Completed Successfully.", '', { duration: 5000, verticalPosition: "top" });
          } else {
            this.snackBar.open(res.Jobname + " Execution Failed", '', { duration: 5000, verticalPosition: "top" });
          }
          this.foregroundLoader = false;
          localStorage.setItem('executeForeground', 'false');
        })
      }
    }
  }

  tableExecuteRoleCheck() {
    console.log("In tableExecuteRoleCheck");
    this.apis.postApi(environment.getTable + this.sapUserId + '/' + this.sapPassword, this.selectedJobData).subscribe(res => {
      console.log("tableExecuteRole res: ", res);
      if (res.Status == 'OK') {
        this.jobExeStep = '3'
        this.tableCredInvalid = false;
      } else {
        this.tableCredInvalid = true;
      }
    })

  }

  executeMethod(method, templateRef: TemplateRef<any>) {
    console.log("Execute Method: ", method);
    this.jobExeStep = '1';

    this.executeJob();
    if (method == 'Foreground') {
      this.jobLogs = [];
      localStorage.setItem('foreGroundJob', this.selectedJobData.name);
      localStorage.setItem('executeForeground', 'true');
      this.foregroundLoader = true;
      this.jobLogsClose = false;
      this.dialog.open(templateRef, { width: '600px', disableClose: true });
    }
    else {
      localStorage.setItem('executeForeground', 'false');
      this.foregroundLoader = false;
    }
  }

  bapiExecuteModel(templateRef: TemplateRef<any>) {
    this.paramLoader = true;
    let group = {};
    this.modelTitle = 'Parameters';

    this.apis.postApi(environment.getBapi + 'importparam', this.selectedJobData).subscribe(res => {
      console.log("BAPI Excute parameters result: ", res);
      this.bapiExecuteParameters = res;
      this.bapiExecuteParameters.forEach(element => {
        group[element.paramName] = new FormControl(element.defaultValue);
      })
      this.bapiExcuteForm = new FormGroup(group);
      console.log("bapiExcuteForm: ", this.bapiExcuteForm)
      this.dialog.open(templateRef, { width: '600px' });
      this.paramLoader = false;
    })
  }

  getBapiTableParameters() {
    this.paramLoader = true;

    console.log("getBapiTableParameters: ", this.bapiExcuteForm.value);
    let bapiExcuteFormValue = this.bapiExcuteForm.value;
    let temp = [];
    Object.keys(bapiExcuteFormValue).forEach(function (key) {
      // console.log("Key: " + key + " - " + bapiExcuteFormValue[key]);
      temp.push({
        paramName: key,
        paramValue: bapiExcuteFormValue[key]
      });
    });
    this.bapiExecuteParamsList = temp;
    console.log("bapiExecuteParamsList: ", this.bapiExecuteParamsList);
    let tableParamsAPI = '';
    let exportParamAPI = '';
    if (localStorage.getItem('userRole') == "BUSINESS") {
      tableParamsAPI = environment.getBapi + 'tableparams/' + this.sapUserId + '/' + this.sapPassword;
      exportParamAPI = environment.getBapi + 'exportparams/' + this.selectedJobData.id + '/' + this.sapUserId + '/' + this.sapPassword;
    } else {
      tableParamsAPI = environment.getBapi + 'tableparams';
      exportParamAPI = environment.getBapi + 'exportparams/' + this.selectedJobData.id;
    }
    this.bapiExportTable = [];
    this.apis.postApi(tableParamsAPI, this.selectedJobData).subscribe(res => {
      console.log("tableparams: ", res);
      this.paramLoader = false;
      if (res.Status == "OK") {
        this.bapiExportTable = res.Data;
        this.modelTitle = 'Table and Export Parameters';
        this.sapCredentials = true;
      } else if (res.Status == "BAD_REQUEST") {
        this.bapiExportParams = [];
        this.sapCredentials = false;
      } else {
        this.bapiExportTable = []
      }

    })

    // this.bapiExecuteParameters
    this.bapiExportParams = [];
    this.apis.postApi(exportParamAPI, this.bapiExecuteParamsList).subscribe(res => {
      console.log("exportparams: ", res);
      if (res.Status == "OK") {
        this.bapiExportParams = res.Data;
      } else if (res.Status == "BAD_REQUEST") {
        this.bapiExportParams = [];
      } else {
        this.bapiExportParams = [];
      }

    })
  }

  downloadBapiExecute() {
    let downloadAPI = '';
    if (localStorage.getItem('userRole') == "BUSINESS") {
      downloadAPI = environment.getBapi + 'executejob/' + this.selectedJobData.id + '/' + this.sapUserId + '/' + this.sapPassword;
    } else {
      downloadAPI = environment.getBapi + 'executejob/' + this.selectedJobData.id;
    }
    this.apis.blobPostAPI(downloadAPI, this.bapiExecuteParamsList).subscribe(res => {
      console.log("downloadBapiExecute result: ", res);
      let blob = new Blob([res], { type: 'application/vnd.ms-excel' });
      saveAs(blob, this.selectedJobData.name + '.xlsx');
      this.snackBar.open("File downloaded.", '', { duration: 5000, verticalPosition: "top" });
    })
  }

  bapiExecuteRole() {
    if (localStorage.getItem('userRole') == "BUSINESS") {
      this.modelTitle = 'Credentials';
      this.hide = true;
      this.sapUserId = '';
      this.sapPassword = '';
    } else {
      this.getBapiTableParameters();
    }
  }

  jobDataPerview(type, templateRef: TemplateRef<any>) {
    this.previewShow = false;

    let functionName = '';
    let sourceSystem = '';
    let tableName = '';
    let columnsList;
    let jobType = '';
    console.log("type: ", type);

    if (type == 1) {
      jobType = this.jobForm.value.jobType;
    } else {
      jobType = this.tableType;
    }

    if (jobType == 'extractor') {
      console.log("Before: functionName - ", functionName, ' sourceSystem - ', sourceSystem);
      if (type == 1) {
        console.log("In 1");
        functionName = this.jobForm.value.functionName;
        sourceSystem = this.jobForm.value.sourceSystem;
      } else {
        console.log("In 2");
        functionName = this.selectedJobData.function;
        sourceSystem = this.selectedJobData.sourceId;
      }
      console.log("After: functionName - ", functionName, ' sourceSystem - ', sourceSystem);

      this.apis.getApi(environment.getExtractor + 'preview/' + functionName + '/' + sourceSystem).subscribe(res => {
        console.log("Exe jobDataPerview res: ", res);
        this.previewTableHeaders = Object.keys(res.Data[0]);
        console.log("Keys : ", Object.keys(res.Data[0]));
        const index = this.previewTableHeaders.indexOf('__metadata', 0);
        if (index > -1) {
          this.previewTableHeaders.splice(index, 1);
        }
        console.log("Remove metaData: ", this.previewTableHeaders);
        let previewDataGen = [];
        let previewElement = {};
        res.Data.forEach(element => {
          previewElement = {};
          this.previewTableHeaders.forEach(header => {
            previewElement[header] = (element[header].string != undefined ? element[header].string : '');
          });
          previewDataGen.push(previewElement);
        });
        this.previewData = new MatTableDataSource(previewDataGen);
        this.previewData.paginator = this.previewPageinator;
        this.previewShow = true;
      });
    } else if (jobType == 'table') {
      if (type == 1) {
        tableName = this.jobForm.value.tableName;
        columnsList = this.jobForm.value.columns;
        sourceSystem = this.jobForm.value.sourceSystem;
      } else {
        tableName = this.selectedJobData.table;
        columnsList = this.selectedJobData.fields.split(",");
        sourceSystem = this.selectedJobData.sourceId;
      }

      let columns = '';
      let index = columnsList.indexOf("Select All");
      if (index > -1) {
        columnsList.splice(index, 1);
      }
      columnsList.forEach(element => {
        columns = columns + element + ',';
      });
      columns = columns.slice(0, columns.length - 1);

      this.apis.getApi(environment.getTable + 'preview/' + tableName + '/' + columns + '/' + sourceSystem).subscribe(res => {
        console.log("Table preview res: ", res);
        if (res.Data.length != 0) {
          this.previewTableHeaders = Object.keys(res.Data[0]);
          this.previewData = new MatTableDataSource(res.Data);
          this.previewData.paginator = this.previewPageinator;
        } else {
          console.log("No data in table");
        }

        this.previewShow = true;
      })
    }

    this.dialog.open(templateRef, { width: '80%' });
  }

  categoryModel(templateRef: TemplateRef<any>) {
    this.categoryForm.reset();
    this.dialog.open(templateRef, { width: '600px' });
  }

  addNewCategory() {
    let params = {
      catergoryName: this.categoryForm.value.catergoryName.toUpperCase(),
      categoryType : this.categoryForm.value.categoryType.toUpperCase(),
      categoryDescription: this.categoryForm.value.categoryDescription.toUpperCase(),
    }
    this.jobForm.controls['category'].setValue(this.categoryForm.value.catergoryName.toUpperCase());
    this.apis.postApi(environment.category, params).subscribe(res => {
      console.log("addNewCategory res: ", res);
      if(res.Status=="OK") {
        this.getCategoryList();
        this.jobForm.controls['category'].setValue(res.Data.catergoryName);
      }
    });
  }

  getCategoryList() {
    this.apis.getApi(environment.category).subscribe(res => {
      console.log("getCategoryList res: ", res);
      this.categoryExtractorList = [];
      this.categoryTableList = [];
      if(res.Status=="OK") {
        res.Data.forEach(element => {
          if (element.categoryType=='EXTRACTOR') {
            this.categoryExtractorList.push(element);
          } else {
            this.categoryTableList.push(element);
          }
        });
      }
    });
  }

  receiveSocketData(data) {
    console.log("receiveSocketData: ", data)
  }



  // testRoute(){
  //   console.log("In testRoute");
  //   this.router.navigateByUrl('/admin/systems', { skipLocationChange: true });
  // }

}



