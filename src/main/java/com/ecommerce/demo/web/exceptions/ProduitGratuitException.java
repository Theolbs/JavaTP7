package com.ecommerce.demo.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS)
public class ProduitGratuitException extends RuntimeException{
    public ProduitGratuitException(String s) {
        super(s);
    }
}