package co.id.yokke.multiacquiring.common.exception;

import org.springframework.http.HttpStatus;

import co.id.yokke.multiacquiring.common.constant.CommonMessage;

@SuppressWarnings("serial")
public class InternalServerErrorException extends CustomException {
	public InternalServerErrorException() {
		super(HttpStatus.INTERNAL_SERVER_ERROR, CommonMessage.SHORT_CODE_FAIL_INTERNAL_SERVER_ERROR);
	}
}
