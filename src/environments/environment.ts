// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.
// karun - http://192.168.2.18:8085/  192.168.1.127:8084
// Server - http://172.17.0.185:8084/
// https://elektra-api.miraclesoft.com/
const rootUrl = 'http://192.168.2.18:8084/';

export const environment = {
  production: false,
  rootUrl: rootUrl,
  // jobs Component
  login: rootUrl+'api/auth/signin',
  
  getDashboard: rootUrl+'dashboard/',

  getTable : rootUrl+'table/',
  getBapi : rootUrl+'bapi/',
  getExtractor : rootUrl+'extractor/',
  getSourceSystemList: rootUrl+'source/',
  getFtpList: rootUrl+'ftp/',
  getDbList: rootUrl+'dbInsert/',
  getTableNameList: rootUrl+'table/tables/',

  getSchedularList: rootUrl+'scheduler/',
  
  getLogsList: rootUrl+ 'logs/load',
  // 'logs/status/',
  getLogsFilterList: rootUrl+'logs/',
  getFullLogs: rootUrl+'logs/instance/',

  extractorExecute: rootUrl+'extractor/execute/',

  getConfig: rootUrl+'config/',
  getDelta: rootUrl+'delta/',
  getService: rootUrl+'extracservice/',

  getUserList: rootUrl+'api/test/',
  userSignUpIn: rootUrl+'api/auth/',
  
  jobchains: rootUrl+'batchjob/',

  fetched: rootUrl+'datareconcilation/',
  
  category: rootUrl+'category/',
  // getColumnNameList: rootUrl+

};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
