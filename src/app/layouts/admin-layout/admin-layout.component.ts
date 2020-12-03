import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

// declare interface RouteInfo {
//   path: string;
//   title: string;
//   icon: string;
//   class: string;
// }

// export const ROUTES: RouteInfo[] = [
//   { path: '/dashboard', title: 'Dashboard',  icon: 'dashboard', class: '' },
//   { path: '/user-profile', title: 'User Profile',  icon:'person', class: '' },
//   { path: '/table-list', title: 'Table List',  icon:'content_paste', class: '' },
//   { path: '/typography', title: 'Typography',  icon:'library_books', class: '' },
//   { path: '/icons', title: 'Icons',  icon:'bubble_chart', class: '' },
//   { path: '/maps', title: 'Maps',  icon:'location_on', class: '' },
//   { path: '/notifications', title: 'Notifications',  icon:'notifications', class: '' },
//   { path: '/upgrade', title: 'Upgrade to PRO',  icon:'unarchive', class: 'active-pro' },
// ];


@Component({
  selector: 'app-admin-layout',
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.css']
})

export class AdminLayoutComponent implements OnInit {
  currentDate = new Date();

  menuList: any;
  userFullName: string = '';
  constructor(private router: Router) { }

  ngOnInit() {
    this.userFullName = localStorage.getItem('userFullName');
    console.log("1. ",this.menuList)
    console.log("Localstorage User: ",localStorage.getItem('userRole'))
    if(localStorage.getItem('userRole')=='ADMIN') {
      this.menuList = [
        { path: '/datalake/dashboard', title: 'Dashboard',  icon: 'home' , active: true},
        { path: '/datalake/jobs', title: 'Jobs',  icon: 'card_travel' , active: true},
        { path: '/datalake/systems', title: 'Systems',  icon: 'devices' , active: true},
        { path: '/datalake/schedule', title: 'Schedule',  icon: 'schedule' , active: true},
        { path: '/datalake/jobschain', title: 'Job Chain',  icon: 'card_travel' , active: true},
        { path: '/datalake/monitoring', title: 'Monitoring',  icon: 'tv' , active: true},
        { path: '/datalake/configuration', title: 'Configuration',  icon: 'wifi_tethering' , active: true},
        { path: '/datalake/user-management', title: 'User Management',  icon: 'group' , active: true},
      ];
      console.log("User is ADMIN");
    }
    else if(localStorage.getItem('userRole')=='DEVELOPER') {
      this.menuList = [
        { path: '/datalake/dashboard', title: 'Dashboard',  icon: 'home' , active: true},
        { path: '/datalake/jobs', title: 'Jobs',  icon: 'card_travel' , active: true},
        { path: '/datalake/systems', title: 'Systems',  icon: 'devices' , active: true},
        { path: '/datalake/schedule', title: 'Schedule',  icon: 'schedule' , active: true},
        { path: '/datalake/monitoring', title: 'Monitoring',  icon: 'tv' , active: true},
        { path: '/datalake/configuration', title: 'Configuration',  icon: 'wifi_tethering' , active: true},
        { path: '/datalake/user-management', title: 'User Management',  icon: 'group' , active: true},
      ];
      console.log("User is DEVELOPER");
    }
    else if(localStorage.getItem('userRole')=='BUSINESS') {
      this.menuList = [
        { path: '/datalake/dashboard', title: 'Dashboard',  icon: 'home' , active: true},
        { path: '/datalake/jobs', title: 'Jobs',  icon: 'card_travel' , active: true},
        { path: '/datalake/systems', title: 'Systems',  icon: 'devices' , active: true},
        { path: '/datalake/schedule', title: 'Schedule',  icon: 'schedule' , active: true},
        { path: '/datalake/monitoring', title: 'Monitoring',  icon: 'tv' , active: true},
        // { path: '/configuration', title: 'Configuration',  icon: 'wifi_tethering' , active: true},
        // { path: '/user-management', title: 'User Management',  icon: 'group' , active: true},
      ];
      console.log("User is BUSINESS");
    }
    
  }

  logout() {
    localStorage.clear();
    this.router.navigateByUrl('/');
  }
}