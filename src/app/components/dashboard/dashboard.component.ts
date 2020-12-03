import { Component, OnInit } from '@angular/core';
import { MatIconModule, MatChipsModule } from '@angular/material';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { DataLakesService } from 'src/app/services/data-lakes.service';
// import { SocketDataService } from 'src/app/services/socket-data.service';
import { Subscription } from 'rxjs/internal/Subscription';
import { WebSocketService } from 'src/app/services/web-socket.service';
import { from } from 'rxjs';
// MatChipsModule
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  dashboardData: any;
  loader: Boolean = true;
  breakpoint: any;
  // subscription: Subscription;
  // webSocket: WebSocketService;
  // greeting: any;
  // name: string = "Sendind Data from client through webSocket.";

  receivedData: any;
  // private socket: SocketDataService
  constructor(private router: Router, private apis: DataLakesService, ) { }

  ngOnInit() {
    this.loader = true;
    this.getDashboardData();
    console.log(this.router.url);
    // this.breakpoint = (window.innerWidth <= 400) ? 1 : 2;
    this.onResize('', 0);
    // this.webSocket = new WebSocketService(new DashboardComponent());
    // this.webSocket = new WebSocketService();
    // this.webSocket._connect();
    // this.getSocketData();
    // this.getWebSocketData();
  }

  onResize(event, type) {
    let size = ((type==0)? window.innerWidth : event.target.innerWidth);
    if(size<=450) {
      this.breakpoint = 1;
    } else if(size<=1080) {
      this.breakpoint = 2;
    } else {
      this.breakpoint = 4;
    } 
  }

  // disconnect(){
  //   this.webSocket._disconnect();
  // }

  // sendMessage(){
  //   this.webSocket._send(this.name);
  // }

  // handleMessage(message){
  //   this.greeting = message;
  // }

  // getWebSocketData() {
  //   this.subscription = this.webSocket.onMessageReceived('liveLogs')
  //       .subscribe(data => {
  //         console.log("webSocket received: ",data);
  //         this.receivedData = data;
  //       })
  // }
  // sendSocketData() {
  //   this.socket.sendSocketData('logs',{ "message": "Testing sockets...!"});
  // }

  // getSocketData() {
  //   this.subscription = this.socket.getSocketData('liveLogs')
  //     .subscribe(data => {
  //       console.log("Socket received: ",data);
  //       this.receivedData = data;
  //     })
  // }
  
  getDashboardData() {
    this.apis.getApi(environment.getDashboard+localStorage.getItem('userRole')+'/'+localStorage.getItem('userName')).subscribe(res => {
      console.log("getDashboardData result: ",res);
      if(res.Status=='OK') {
        this.dashboardData = res.Data;
        console.log("dashboardData: ",this.dashboardData);
        this.loader = false;
      }
    });
  }

  navigateToPage(path) {
    this.router.navigateByUrl(path);
  }

}
