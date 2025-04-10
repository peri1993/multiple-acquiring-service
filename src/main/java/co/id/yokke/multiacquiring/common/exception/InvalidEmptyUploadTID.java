package co.id.yokke.multiacquiring.common.exception;

import org.springframework.http.HttpStatus;

import co.id.yokke.multiacquiring.common.constant.CommonMessage;

@SuppressWarnings("serial")
public class InvalidEmptyUploadTID extends CustomException {

	public InvalidEmptyUploadTID() {
		super(HttpStatus.BAD_REQUEST, CommonMessage.SHORT_CODE_INVALID_EMPTY_UPLOAD_TID);
	}

}
