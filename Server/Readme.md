This sample application demonstrates how to use the Motwin Salesforce connector in order to get data from a Salesforce account.

The connection to Salesforce requires OAuth2.0 authentication. To understand how to setup your Salesforce account to get it ready for running this sample, please refer to https://www.salesforce.com/us/developer/docs/api_rest/Content/intro_understanding_authentication.htm

In order to test Salesforce connection, you can use the following request : 
curl --data "grant_type=password&client_id=CONSUMER_KEY&client_secret=CONSUMER_SECRET&username=USERNAME&password=PASSWORD" https://login.salesforce.com/services/oauth2/token
