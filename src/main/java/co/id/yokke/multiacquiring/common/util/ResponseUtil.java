package co.id.yokke.multiacquiring.common.util;

import co.id.yokke.multiacquiring.common.exception.CustomException;
import co.id.yokke.multiacquiring.common.exception.base.BaseException;
import co.id.yokke.multiacquiring.common.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Calendar;

public class ResponseUtil {

    private static MessagePropertiesCache messageProperties = MessagePropertiesCache.getInstance();

    public static ResponseEntity<?> response(Object data, String shortCode, String language) {
        BaseResponse payload = new BaseResponse();

        String message = messageProperties.getProperty(shortCode + "." + language);

        payload.setCode(HttpStatus.OK.value());
        payload.setShortCode(shortCode);
        payload.setTimestamp(Calendar.getInstance().getTime());
        payload.setMessage(message);
        payload.setData(data);

        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    public static ResponseEntity<?> response(CustomException e, String language) {
        BaseResponse payload = new BaseResponse();

        String message = messageProperties.getProperty(e.getShortCode() + "." + language);

        payload.setCode(e.getCode());
        payload.setShortCode(e.getShortCode());
        payload.setTimestamp(Calendar.getInstance().getTime());
        payload.setMessage(message);

        return new ResponseEntity<>(payload, e.getStatus());
    }

    public static Object responseObject(CustomException e, String language) {
        BaseResponse payload = new BaseResponse();

        String message = messageProperties.getProperty(e.getShortCode() + "." + language);

        payload.setCode(e.getCode());
        payload.setShortCode(e.getShortCode());
        payload.setTimestamp(Calendar.getInstance().getTime());
        payload.setMessage(message);

        return payload;
    }
    
    public static ResponseEntity<?> response(BaseException e, String language) {
        BaseResponse payload = new BaseResponse();

        String message = messageProperties.getProperty(e.getShortCode() + "." + language);

        payload.setCode(e.getCode());
        payload.setShortCode(e.getShortCode());
        payload.setTimestamp(Calendar.getInstance().getTime());
        payload.setMessage(message);

        return new ResponseEntity<>(payload, e.getStatus());
    }

}
