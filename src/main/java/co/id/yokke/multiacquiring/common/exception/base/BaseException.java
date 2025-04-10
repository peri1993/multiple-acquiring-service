package co.id.yokke.multiacquiring.common.exception.base;

import org.springframework.http.HttpStatus;

public interface BaseException {

    HttpStatus getStatus();

    void setStatus(HttpStatus status);

    int getCode();

    void setCode(int code);

    String getShortCode();

    void setShortCode(String shortCode);
}
