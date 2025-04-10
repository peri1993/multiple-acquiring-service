package co.id.yokke.multiacquiring.common.exception;

import org.springframework.http.HttpStatus;

import co.id.yokke.multiacquiring.common.constant.CommonMessage;

@SuppressWarnings("serial")
public class InvalidFileException extends CustomException {

	public InvalidFileException() {
		super(HttpStatus.BAD_REQUEST, CommonMessage.SHORT_CODE_INVALID_FILE_EXCEPTION);
	}

}
