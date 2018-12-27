package com.longcoding.undefined.exceptions;

import com.longcoding.undefined.models.CommonResponseEntity;
import lombok.Getter;

/**
 * Created by longcoding on 16. 4. 9..
 * Updated by longcoding on 18. 12. 27..
 */
@Getter
public class GeneralException extends RuntimeException {

    private static final long serialVersionUID = -6517361615703262319L;

    private String code;
    private String message;

    public GeneralException() { super(); }

    public GeneralException(String message) { super(message); }

    public GeneralException(CommonResponseEntity exceptionResponse) {
        super();
        this.code = exceptionResponse.getCode();
        this.message = exceptionResponse.getMessage().toString();
    }

}
