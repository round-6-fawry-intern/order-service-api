package com.fawry.orderservice.exception;

import com.fawry.orderservice.error.GlobalError;
import com.fawry.orderservice.error.ProductErrorModel;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalHandler {

  @ExceptionHandler(ClientException.class)
  public ResponseEntity<GlobalError> handleClientException(ClientException e) {

    return new ResponseEntity<>(new GlobalError(e.getMessage()), HttpStatusCode.valueOf(400));
  }

  @ExceptionHandler(ProductException.class)
  public ResponseEntity<ProductErrorModel> handleClientException(ProductException e) {

    return new ResponseEntity<>(new ProductErrorModel(e.getErrors()), HttpStatusCode.valueOf(400));
  }
}
