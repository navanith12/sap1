//http://192.168.2.18:8084/  http://172.17.0.185:8084/
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
};
