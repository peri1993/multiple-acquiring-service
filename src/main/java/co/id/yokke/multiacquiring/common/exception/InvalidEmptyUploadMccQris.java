package co.id.yokke.multiacquiring.common.exception;

import org.springframework.http.HttpStatus;

import co.id.yokke.multiacquiring.common.constant.CommonMessage;

@SuppressWarnings("serial")
public class InvalidEmptyUploadMccQris extends CustomException {

	public InvalidEmptyUploadMccQris() {
		super(HttpStatus.BAD_REQUEST, CommonMessage.SHORT_CODE_INVALID_INVALID_EMPTY_MCC_QRIS);
	}

}
