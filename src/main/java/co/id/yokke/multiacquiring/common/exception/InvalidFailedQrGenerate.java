package co.id.yokke.multiacquiring.common.exception;

import org.springframework.http.HttpStatus;

import co.id.yokke.multiacquiring.common.constant.CommonMessage;

@SuppressWarnings("serial")
public class InvalidFailedQrGenerate extends CustomException{

	public InvalidFailedQrGenerate() {
		super(HttpStatus.BAD_REQUEST, CommonMessage.SHORT_CODE_FAIL_QR_GENERATE);
	}
}
