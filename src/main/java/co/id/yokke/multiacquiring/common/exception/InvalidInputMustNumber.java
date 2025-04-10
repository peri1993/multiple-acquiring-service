package co.id.yokke.multiacquiring.common.exception;

import org.springframework.http.HttpStatus;

import co.id.yokke.multiacquiring.common.constant.CommonMessage;

@SuppressWarnings("serial")
public class InvalidInputMustNumber extends CustomException {

	public InvalidInputMustNumber() {
		super(HttpStatus.BAD_REQUEST, CommonMessage.SHORT_CODE_INVALID_INVALID_INPUT_MUST_NUMBER);
	}

}
