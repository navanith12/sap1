import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
// import { MatSnackBar } from '@angular/material/snack-bar/typings/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class DataLakesService {

  constructor(private http: HttpClient, private router: Router, ) { }
  // private snackBar: MatSnackBar
  getApi(endpoint): Observable<any> {
    console.log('get request url --> ', endpoint);
    return this.http.get(endpoint).pipe(
      map((uresponse: Response) => {
        // console.log('GET API res', uresponse);
        return uresponse;
      }), catchError(this.handleError<any>('Login')));
  }

  postApi(endpoint, data): Observable<any> {
    console.log('post request url --> ',endpoint, data);
    return this.http.post<any>(endpoint, data).pipe(
      map((uresponse: Response) => {
        // console.log('POST API res', uresponse);
        return uresponse;
      }), catchError(this.handleError<any>('Login'))
    );
  }

  putApi(endpoint, data): Observable<any> {
    console.log('put request url --> ',endpoint, data);
    return this.http.put(endpoint, data).pipe(
      map((uresponse: Response) => {
        // console.log('PUT API response', uresponse);
        return uresponse;
      }), catchError(this.handleError<any>('Login'))
    );
  }

  deleteApi(endpoint): Observable<any> {
    console.log('delete request url --> ',endpoint);
    return this.http.delete(endpoint).pipe(
      map((uresponse: Response) => {
        // console.log('DELETE API response', uresponse);
        return uresponse;
      }), catchError(this.handleError<any>('Login'))
    );
  }

  // options = {
  //     headers: new HttpHeaders({ 'Content-Type': 'application/json; charset=utf-8' }),
  //     responseType: 'arraybuffer'
  // }
  headers = new HttpHeaders({ 'Content-Type': 'application/json' });

  blobPostAPI(endpoint, data): Observable<Blob> {
    console.log('post request url --> ',endpoint, data);
    return this.http.post<Blob>(endpoint, data, { 'headers': this.headers, responseType: 'blob' as 'json' }).pipe(catchError(this.handleError<any>('Login')));
  }
  

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead
      // this.snackBar.open("Server Unavailable", '', { duration:  5000, verticalPosition: "top" });
      if (error.error.status == '401' && error.error.message == 'Unauthorized') {
        this.router.navigate(['/']);
      }
      // TODO: better job of transforming error for user consumption
      // console.log(`${operation} failed: ${error.message}`);
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
