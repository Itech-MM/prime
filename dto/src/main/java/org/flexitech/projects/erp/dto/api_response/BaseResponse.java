package org.flexitech.projects.erp.dto.api_response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    private boolean success;
    private Integer statusCode;
    private String message;
    private T data;
    private List<?> errors;

    public static <T> BaseResponse<T> success(T data, String message, Integer statusCode) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatusCode(statusCode);
        response.setMessage(message);
        response.setData(data);
        response.setErrors(null);
        return response;
    }

    public static <T> BaseResponse<T> fail(String message, Integer statusCode, List<?> errors) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setSuccess(false);
        response.setStatusCode(statusCode);
        response.setMessage(message);
        response.setData(null);
        response.setErrors(errors);
        return response;
    }
}