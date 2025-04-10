package co.id.yokke.multiacquiring.common.exception;

import org.springframework.http.HttpStatus;

import co.id.yokke.multiacquiring.common.constant.CommonMessage;

@SuppressWarnings("serial")
public class InvalidMccRacConfigException extends CustomException {

	public InvalidMccRacConfigException() {
		super(HttpStatus.BAD_REQUEST, CommonMessage.SHORT_CODE_INVALID_RAC_CONFIG_EXCEPTION);
	}

}
