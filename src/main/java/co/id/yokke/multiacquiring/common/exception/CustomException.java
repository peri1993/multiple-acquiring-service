package co.id.yokke.multiacquiring.common.exception;

import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
public class CustomException extends Exception {

    private HttpStatus status;
    private int code;
    private String shortCode;

    public CustomException(HttpStatus status, String shortCode) {
        this.status = status;
        this.code = status.value();
        this.shortCode = shortCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
}
