
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CommonModule, } from '@angular/common';
import { BrowserModule  } from '@angular/platform-browser';

import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { LoginComponent } from '../app/components/login/login.component';

export const AppRoutes: Routes = [
  {
    path: '',
    component: LoginComponent,
  },
  {
    path: 'datalake',
    component: AdminLayoutComponent,
    children: [
      {
        path: '',
        loadChildren: './layouts/admin-layout.module#AdminLayoutModule',
      },
      // {
      //   path: 'employee',
      //   loadChildren: 'src/app/pages/antd/antd.module#AntdModule',
      // }
    ],
  }
];

// @NgModule({
//   imports: [
//     CommonModule,
//     BrowserModule,
//     RouterModule.forRoot(routes)
//   ],
//   exports: [
//   ],
// })
// export class AppRoutingModule { }
 