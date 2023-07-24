package com.scrape.ephraim.data;

public class StatusIssue extends Issue
{
    ///the status code
    private int mStatusCode;

    /**
     * Default constructor
     */
    public StatusIssue(int code, String url)
    {
        super("Non 200 status code", 1, url);
        setCategory("Status Issue");
        mStatusCode = code;
        setSummary(String.valueOf(mStatusCode));

        setDescription(getRealDescription(code));
    }

    private String getRealDescription(int statusCode)
    {
        String description;

        switch (statusCode) {
            case 100:
                description = "Continue: The server has received the request headers and the client should proceed to send the request body.";
                break;
            case 101:
                description = "Switching Protocols: The server is switching protocols according to the client's request.";
                break;
            case 204:
                description = "No Content: The server has successfully fulfilled the request, but there is no additional information to send.";
                break;
            case 400:
                description = "Bad Request: The server cannot or will not process the request due to a client error.";
                break;
            case 401:
                description = "Unauthorized: Authentication is required, and the client has not provided valid credentials.";
                break;
            case 403:
                description = "Forbidden: The client does not have the necessary permission to access the requested resource.";
                break;
            case 404:
                description = "Not Found: The requested resource could not be found on the server.";
                break;
            case 500:
                description = "Internal Server Error: The server encountered an unexpected condition that prevented it from fulfilling the request.";
                break;
            case 501:
                description = "Not Implemented: The server does not support the functionality required to fulfill the request.";
                break;
            case 502:
                description = "Bad Gateway: The server, while acting as a gateway or proxy, received an invalid response from the upstream server.";
                break;
            case 503:
                description = "Service Unavailable: The server is not ready to handle the request.";
                break;
            case 505:
                description = "HTTP Version Not Supported: The server does not support the HTTP protocol version used in the request.";
                break;
            default:
                description = "Non 200 status code.";
        }
        return description;
    }

    /**
     * Getter for status code
     * @return
     */
    public int getStatusCode() {return mStatusCode;}



}
