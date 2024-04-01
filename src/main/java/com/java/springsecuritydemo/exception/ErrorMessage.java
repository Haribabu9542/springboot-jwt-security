/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.java.springsecuritydemo.exception;

import java.time.LocalDate;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HaribabuChinthakunta
 */
@Data
@AllArgsConstructor
// @NoArgsConstructor
public class ErrorMessage extends RuntimeException {
    private static final long serialVersionUID = -291211739734090347L;


    // private Date date;
    // private String message;

    public ErrorMessage(String message) {
        super(message);
    }

}
