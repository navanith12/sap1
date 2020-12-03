import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import * as io from 'socket.io-client';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SocketDataService {

  socket: any; 

  constructor(private http: HttpClient) { 
    // this.socket = io('http://192.168.2.18:8086/')
  }

  getSocketData(eventName: string){
    // console.log("In socket servie Out")
    return new Observable(Subscriber => {
      this.socket.on(eventName, (data) => {
        // console.log("In socket servie In");
        Subscriber.next(data);
      })
    })
  }


  sendSocketData(eventName, message){
      // console.log("sendSocketData called...!")
      this.socket.emit(eventName, message);
  }

}
