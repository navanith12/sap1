import { Injectable, OnInit } from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment';
import { JobsComponent } from '../components/jobs/jobs.component';
import { AppComponent } from '../app.component';
import { Subject } from 'rxjs/internal/Subject';

@Injectable({
    providedIn: 'root'
})
export class WebSocketService implements OnInit {
    webSocketEndPoint: string = environment.rootUrl + 'ws';
    topic: string = "/topic/user";
    stompClient: any;
    jobComponent: AppComponent;
    // Observable string sources
    private jobCompReceive = new Subject<any>();
    private monitorCompReceive = new Subject<any>();
    receivedMessage: any;

    constructor() {}
    
    ngOnInit() {
        console.log("In wev-socket Service..!");
        this._connect();
    }

    _connect() {
        console.log("Initialize WebSocket Connection");
        let ws = new SockJS(this.webSocketEndPoint);
        this.stompClient = Stomp.over(ws);
        const _this = this;
        _this.stompClient.connect({}, function (frame) {
            _this.stompClient.subscribe(_this.topic, function (sdkEvent) {
                _this.onMessageReceived(sdkEvent);
            });
            //_this.stompClient.reconnect_delay = 2000;
        }, this.errorCallBack);
    };

    _disconnect() {
        if (this.stompClient !== null) {
            this.stompClient.disconnect();
        }
        console.log("Disconnected");
    }

    // on error, schedule a reconnection attempt
    errorCallBack(error) {
        console.log("errorCallBack -> " + error)
        setTimeout(() => {
            this._connect();
        }, 5000);
    }

    /**
    * Send message to sever via web socket
    * @param {*} message 
    */
    _send(message) {
        console.log("calling logout api via web socket");
        this.stompClient.send("/app/hello", {}, JSON.stringify(message));
    }

    onMessageReceived(message) {
        console.log("Message Recieved from Server :: " + message.body);
        // this.jobComponent.receiveSocketData(JSON.stringify(message.body));
        this.receivedMessage = (message.body);
        this.callJobComponentMethod();
        // this.callMonitorComponentMethod();
    }

    
    // Observable string streams
    jobCompMethodCalled$ = this.jobCompReceive.asObservable();

    // Service message commands
    callJobComponentMethod() {
        this.jobCompReceive.next(this.receivedMessage);
    }

    // Observable string streams
    monitorCompMethodCalled$ = this.monitorCompReceive.asObservable();

    // Service message commands
    callMonitorComponentMethod() {
        this.monitorCompReceive.next(this.receivedMessage);
    }
}