import { Component, OnInit, ViewChild } from '@angular/core';
import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { DataLakesService } from 'src/app/services/data-lakes.service';
import { environment } from 'src/environments/environment';
import { TemplateRef } from '@angular/core';
import { MatDialog, MatTableDataSource, MatPaginator, throwToolbarMixedModesError } from '@angular/material';
import { element } from 'protractor';

@Component({
  selector: 'app-jobs-chain',
  templateUrl: './jobs-chain.component.html',
  styleUrls: ['./jobs-chain.component.css']
})
export class JobsChainComponent implements OnInit {
  @ViewChild('JobchainPageinator', { static: true }) jobchainPageinator: MatPaginator;

  tableTypeList: any = [
    { name: "Extractor", value: "extractor" },
    { name: "Table", value: "table" }
  ];

  jobchainName: string = '';
  jobchainNameDisable: Boolean = false;
  tableType: string = 'extractor';
  modelTitle: string = '';
  jobsList: Array<any> = [];
  duplicateJobsList: Array<any> = [];
  chainjobList: Array<any> = [];


  jobchainTableHeaders: string[] = ['#', 'name', 'createdby', 'createdAt', 'actions']
  jobchainTable: MatTableDataSource<any>;
  tableLoader: Boolean = true;

  selectedJobchainData: any;

  constructor(private dialog: MatDialog, private apis: DataLakesService) { }

  ngOnInit() {
    this.getjobChainList();
    // this.tableTypeChange();
    this.getExtractorData();
    this.getTableData();
  }

  tableTypeChange() {
    console.log("In tableTypeChange - ", this.tableType);
    if (this.tableType == 'extractor') {
      this.getExtractorData();
    } else if (this.tableType == 'table') {
      this.getTableData();
    }
  }


  getExtractorData() {
    this.apis.getApi(environment.getExtractor+'all/'+localStorage.getItem('userRole')+'/'+localStorage.getItem('userName')).subscribe(res => {
      console.log("getExtractorData result: ", res);
      this.jobsList = res.Data;
      this.duplicateJobsList = this.duplicateJobsList.concat(res.Data);
      // this.duplicateJobsList = this.uniqeList(this.duplicateJobsList, it => it.id);
      console.log("getExtractorData duplicateJobsList: ",this.duplicateJobsList);
    });
  }

  getTableData() {
    this.apis.getApi(environment.getTable+'all/'+localStorage.getItem('userRole')+'/'+localStorage.getItem('userName')).subscribe(res => {
      console.log("getTableData result: ", res);
      this.jobsList = res;
      this.duplicateJobsList = this.duplicateJobsList.concat(res);
      // this.duplicateJobsList = this.uniqeList(this.duplicateJobsList, it => it.id);
      console.log("getTableData duplicateJobsList: ",this.duplicateJobsList);
    });
  }

  uniqeList(data, key) {
    return [
      ... new Map(
        data.map(x => [key(x), x])
      ).values()
    ]
  }

  drop(event: CdkDragDrop<string[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex);
    }
  }

  jobchainModel(templateRef: TemplateRef<any>, title){
    let dailogWidth = '600px';
    this.modelTitle = title;
    this.tableTypeChange();
    if(title=='Create') {
      this.jobchainNameDisable = false;
      this.jobchainName = '';
      this.chainjobList = [];
      dailogWidth = '900px';
    }
    else if(title=='Edit' || title=='View') {
      dailogWidth = (title=='Edit') ? '900px' : '600px';
      this.jobchainNameDisable = true;
      this.jobchainName = this.selectedJobchainData.name;
      this.chainjobList = [];
      this.duplicateJobsList = this.uniqeList(this.duplicateJobsList, it => it.id);
      this.selectedJobchainData.jobLists.forEach(jList => {
        this.duplicateJobsList.forEach(dList => {
          if(jList==dList.id){
            this.chainjobList.push(dList);
          }
        });
      });
      console.log("Final duplicateJobsList: ",this.duplicateJobsList);
      console.log("chainjobList: ",this.chainjobList);
      // this.chainjobList = this.selectedJobchainData.jobLists;
    }
    
    this.dialog.open(templateRef, { width: dailogWidth });
  }

  getjobChainList() {
    this.tableLoader = true;
    this.apis.getApi(environment.jobchains).subscribe(res => {
      console.log("getjobChainList res: ",res);
      this.jobchainTable = new MatTableDataSource(res);
      this.jobchainTable.paginator = this.jobchainPageinator;
      this.tableLoader = false;
    });
  }

  createJobchain() {
    this.tableLoader = true;
    console.log("createJobchain: ",this.chainjobList);
    let params = {
      "createdby": localStorage.getItem('userName'),
      "jobLists": [],
      "name": this.jobchainName
    }
    this.chainjobList.forEach(element => {
      params.jobLists.push(element.id)
    });
    console.log("createJobchain params: ",params);
    this.apis.postApi(environment.jobchains, params).subscribe(res => {
      console.log("createJobchain res: ",res);
      this.getjobChainList();
    });
  }

  updateJobchain() {
    this.tableLoader = true;
    console.log("In updateJobchain: ",this.chainjobList);
    this.selectedJobchainData.jobLists = [];
    this.chainjobList.forEach(element => {
      this.selectedJobchainData.jobLists.push(element.id)
    });
    console.log("updateJobchain params: ",this.selectedJobchainData);
    this.apis.putApi(environment.jobchains+this.selectedJobchainData.id, this.selectedJobchainData).subscribe(res => {
      console.log("updateJobchain res: ",res);
      this.getjobChainList();
    });
  }

  deleteJobchain() {
    this.tableLoader = true;
    console.log("In deleteJobchain");
    this.apis.deleteApi(environment.jobchains+this.selectedJobchainData.id).subscribe(res => {
      console.log("deleteJobchain res: ",res);
      this.getjobChainList();
    });
  }

  executeJobchain() {
    console.log("In executeJobchain");
    this.apis.postApi(environment.jobchains+'execute', this.selectedJobchainData).subscribe(res => {
      console.log("executeJobchain res: ",res);
    })
  }

}
