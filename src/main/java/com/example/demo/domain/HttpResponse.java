package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;



@Data
public class HttpResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "MM-dd-yyyy hh:mm:ss")
    private Date timestamp;
    private int statusCode;
    private HttpStatus httpStatus;
    private String reason;
    private String message;

    public HttpResponse() {
    }

    public HttpResponse(int statusCode, HttpStatus httpStatus, String reason, String message) {
        this.statusCode = statusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
    }

}
