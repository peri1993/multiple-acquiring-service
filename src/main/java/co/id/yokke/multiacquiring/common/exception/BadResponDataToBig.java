package co.id.yokke.multiacquiring.common.exception;

import co.id.yokke.multiacquiring.common.constant.CommonMessage;
import org.springframework.http.HttpStatus;

public class BadResponDataToBig extends CustomException{

	public BadResponDataToBig() {
		super(HttpStatus.BAD_REQUEST, CommonMessage.SHORT_CODE_BIG_DATA);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
