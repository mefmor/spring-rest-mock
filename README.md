# Universal REST mock

The goal of this project is to create a universal REST stub.

## Customization
The default settings are specified in the application.properties file.

## Using
The default endpoint path for requests from integrations is specified in the property: server.servlet.context-path
The path to the resource file used for the default answer can be found in the property: default.response.path

Accessing to the endpoint (e.g. http://localhost:8080/endpoint/) required basic http auth
(credentials: user/password).

In order to set a new response body of the stub, please use the POST request to /responseBody
(e.g. http://localhost:8080/endpoint/responseBody) where body of your request contains new response body.

In order to set a new response code value of the stub, please use the POST request to /responseCode
(e.g. http://localhost:8080/endpoint/responseCode).

In order to set a new response header, please use the POST request to /responseHeaders/{header} 
(e.g. http://localhost:8080/endpoint/responseHeaders/Accept) where {header} is header name and body is header value.

If you want to get the body of the last request to the endpoint, please use the GET request to /lastRequestBody
(e.g. http://localhost:8080/endpoint/lastRequestBody).