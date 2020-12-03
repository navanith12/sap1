import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { DatePipe, CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { 
  MatSidenavModule, 
  MatDividerModule, 
  MatToolbarModule, 
  MatIconModule, 
  MatButtonModule, 
  MatMenuModule, 
  MatListModule,
  MatCardModule,
  MatChipsModule,
  MatGridListModule,
  MatInputModule,
  MatSnackBarModule
} from '@angular/material';


import { AppRoutes } from './app.routing';
import { AppComponent } from './app.component';
import { FormsModule } from '@angular/forms';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { LoginComponent } from '../app/components/login/login.component';


@NgModule({
  declarations: [
    AppComponent,
    AdminLayoutComponent,
    LoginComponent,
    
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    MatChipsModule,
    MatSidenavModule, 
    MatDividerModule, 
    MatToolbarModule, 
    MatIconModule, 
    MatButtonModule, 
    MatMenuModule, 
    MatListModule,
    MatCardModule,
    MatGridListModule,
    MatInputModule,
    CommonModule,
    RouterModule.forRoot(AppRoutes),
    // AdminLayoutComponent,
    MatSnackBarModule
    
  ],
  providers: [ DatePipe ],
  bootstrap: [AppComponent],
  exports: [RouterModule],
})
export class AppModule { }
