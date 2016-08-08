package org.telegram.hojjat.DTOS;

public class AckDTO<T> {

    private Integer errorCode;
    private String message;
    private T entity;

    public AckDTO() {
    }

    public AckDTO(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public AckDTO(Integer errorCode, String message, T entity) {
        this.errorCode = errorCode;
        this.message = message;
        this.entity = entity;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
}
