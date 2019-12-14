export const API_BASE_URL = 'http://openhome275.us-west-1.elasticbeanstalk.com';
export const CLIENT_BASE_URL = 'http://openhome275.us-west-1.elasticbeanstalk.com'
export const ACCESS_TOKEN = 'accessToken';

export const OAUTH2_REDIRECT_URI = 'http://openhome275.us-west-1.elasticbeanstalk.com/oauth2/redirect'

export const GOOGLE_AUTH_URL = API_BASE_URL + '/api/oauth2/authorize/google?redirect_uri=' + OAUTH2_REDIRECT_URI;
export const FACEBOOK_AUTH_URL = API_BASE_URL + '/api/oauth2/authorize/facebook?redirect_uri=' + OAUTH2_REDIRECT_URI;
