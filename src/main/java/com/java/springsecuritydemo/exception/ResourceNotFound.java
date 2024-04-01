package com.java.springsecuritydemo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
//@NoArgsConstructor
// @AllArgsConstructor
public class ResourceNotFound  extends  RuntimeException{
    // private String message;
    private static final long serialVersionUID = -291211739734090347L;
    public ResourceNotFound (String message){
        super(message);
    }
}
