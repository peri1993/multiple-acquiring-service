package co.id.yokke.multiacquiring.common.exception;

import org.springframework.http.HttpStatus;

import co.id.yokke.multiacquiring.common.constant.CommonMessage;


@SuppressWarnings("serial")
public class InvalidTIDMore8CharException extends CustomException {

	public InvalidTIDMore8CharException() {
		super(HttpStatus.BAD_REQUEST, CommonMessage.SHORT_CODE_INVALID_TID_MORE_8_CHAR_EXCEPTION);
	}

}