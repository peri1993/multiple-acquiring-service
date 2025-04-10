package co.id.yokke.multiacquiring.common.exception;

import org.springframework.http.HttpStatus;

import co.id.yokke.multiacquiring.common.constant.CommonMessage;

@SuppressWarnings("serial")
public class InvalidSameRequestNMID extends CustomException {

	public InvalidSameRequestNMID() {
		super(HttpStatus.BAD_REQUEST, CommonMessage.SHORT_CODE_INVALID_SAME_NMID_REQUEST);
	}

}
