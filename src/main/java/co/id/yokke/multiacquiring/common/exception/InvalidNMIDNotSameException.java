package co.id.yokke.multiacquiring.common.exception;

import org.springframework.http.HttpStatus;

import co.id.yokke.multiacquiring.common.constant.CommonMessage;

@SuppressWarnings("serial")
public class InvalidNMIDNotSameException extends CustomException {

	public InvalidNMIDNotSameException() {
		super(HttpStatus.BAD_REQUEST, CommonMessage.SHORT_CODE_FAIL_NMID_NOT_SAME);
	}

}
