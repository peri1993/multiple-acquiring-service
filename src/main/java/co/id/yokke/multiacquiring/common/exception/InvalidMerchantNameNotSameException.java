package co.id.yokke.multiacquiring.common.exception;

import org.springframework.http.HttpStatus;

import co.id.yokke.multiacquiring.common.constant.CommonMessage;

@SuppressWarnings("serial")
public class InvalidMerchantNameNotSameException extends CustomException {

	public InvalidMerchantNameNotSameException() {
		super(HttpStatus.BAD_REQUEST, CommonMessage.SHORT_CODE_FAIL_MERCHANT_NAME_NOT_SAME);
	}

}
