//package com.guidodelbo.usercrud.exception;
//
//import org.springframework.context.support.DefaultMessageSourceResolvable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//import javax.servlet.http.HttpServletResponse;
//import javax.validation.ConstraintViolationException;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.Date;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@ControllerAdvice
//public class UserCrudExceptionHandler {
//
//    @ExceptionHandler(value = {UserCrudRequestException.class})
//    public ResponseEntity<UserCrudException> handleUserCrudRequestException(UserCrudRequestException ex, WebRequest webRequest) {
//        HttpStatus status = HttpStatus.BAD_REQUEST;
//        UserCrudException exception = new UserCrudException(status, LocalDateTime.now(), ex.getMessage());
//
//        return new ResponseEntity<>(exception, status);
//    }
//
//    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
//    public ResponseEntity<UserCrudException> handleUserCrudArgumentNotValidException(MethodArgumentNotValidException ex, HttpStatus status, WebRequest request) {
//        UserCrudException exception = new UserCrudException(status, LocalDateTime.now(), ex.getMessage());
//
//        return new ResponseEntity<>(exception, status);
//    }
//
//    @ExceptionHandler(value = {ConstraintViolationException.class})
//    public ResponseEntity<UserCrudException> handleUserCrudConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
//        HttpStatus status = HttpStatus.BAD_REQUEST;
//        UserCrudException exception = new UserCrudException(status, LocalDateTime.now(), ex.getMessage());
//
//        return new ResponseEntity<>(exception, status);
//    }
//}
