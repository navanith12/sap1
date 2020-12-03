import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { CdkTableModule } from '@angular/cdk/table'

import { MatNativeDateModule, MatRippleModule } from '@angular/material/core';
import { 
  MatSidenavModule, 
  MatDividerModule, 
  MatToolbarModule, 
  MatIconModule, 
  MatButtonModule, 
  MatMenuModule, 
  MatListModule, 
  MatFormFieldModule, 
  MatInputModule,
  MatTableModule,
  MatPaginatorModule,
  MatGridListModule,
  MatDialogModule,
  MatSelectModule,
  MatDatepickerModule,
  MatTooltipModule,
  MatCardModule,
  MatChipsModule,
  MatAutocompleteModule,
  MatTabsModule,
  MatProgressSpinnerModule,
  // MatSnackBarModule,
  MatCheckboxModule,
} from '@angular/material';
import { DragDropModule } from '@angular/cdk/drag-drop';
import {ScrollingModule} from '@angular/cdk/scrolling';

// import { NgxMatDatetimePickerModule, NgxMatTimepickerModule, NgxMatNativeDateModule } from '@angular-material-components/datetime-picker';
// import { NgxMaterialTimepickerModule } from 'ngx-material-timepicker';


import { ReactiveFormsModule,FormsModule } from "@angular/forms";
import { from } from 'rxjs';
import { CronEditorModule } from 'ngx-cron-editor';

import { JobsComponent } from '../components/jobs/jobs.component';
import { SystemsComponent } from '../components/systems/systems.component';
import { SchedularComponent } from '../components/schedular/schedular.component';
import { MonitoringComponent } from '../components/monitoring/monitoring.component';
import { ConfigurationComponent } from '../components/configuration/configuration.component';
import { DashboardComponent } from '../components/dashboard/dashboard.component';
import { UserManagementComponent } from '../components/user-management/user-management.component';
// import { LoginComponent } from '../components/login/login.component';
import { SocketDataService } from '../services/socket-data.service';
import { JobsChainComponent } from '../components/jobs-chain/jobs-chain.component';



const AppRoutes: Routes = [
  // {
  //   path: '',
  //   component: LoginComponent,
  // },
  {
    path: '',
    component: DashboardComponent,
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
  },
  {
    path: 'jobs',
    component: JobsComponent,
  },
  {
    path: 'systems',
    component: SystemsComponent,
  },
  {
    path: 'schedule',
    component: SchedularComponent,
  },
  {
    path: 'jobschain',
    component: JobsChainComponent,
  },
  {
    path: 'monitoring',
    component: MonitoringComponent,
  },
  {
    path: 'configuration',
    component: ConfigurationComponent,
  },
  {
    path: 'user-management',
    component: UserManagementComponent,
  },
  
];

  
@NgModule({
  declarations: [
    JobsComponent,
    SystemsComponent,
    SchedularComponent,
    MonitoringComponent,
    ConfigurationComponent,
    DashboardComponent,
    UserManagementComponent,
    JobsChainComponent,
    // LoginComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(AppRoutes),
    MatSidenavModule,
    MatDividerModule, 
    MatToolbarModule, 
    MatIconModule, 
    MatButtonModule, 
    MatMenuModule, 
    MatListModule,
    CdkTableModule,
    MatTableModule,
    MatPaginatorModule,
    MatGridListModule,
    MatFormFieldModule,
    MatInputModule,
    MatDialogModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule, 
    MatRippleModule,
    MatTooltipModule,
    MatCardModule,
    MatIconModule,
    ReactiveFormsModule,
    FormsModule,
    MatChipsModule,
    MatAutocompleteModule,
    MatTabsModule,
    MatProgressSpinnerModule,
    CronEditorModule,
    // NgxMatDatetimePickerModule,
    // NgxMatTimepickerModule,
    // NgxMatNativeDateModule,
    // NgxMaterialTimepickerModule,
    // MatSnackBarModule,
    MatCheckboxModule,
    DragDropModule,
    ScrollingModule

  ],
  exports: [RouterModule],
  providers: [SocketDataService]
})
export class AdminLayoutModule { }

