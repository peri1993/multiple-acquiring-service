package co.id.yokke.multiacquiring.common.exception;

import org.springframework.http.HttpStatus;

import co.id.yokke.multiacquiring.common.constant.CommonMessage;

public class BadRequstDate60DaysException extends CustomException{

	public BadRequstDate60DaysException() {
		super(HttpStatus.BAD_REQUEST, CommonMessage.SHORT_CODE_BETWEEN_DATE_60_DAYS);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
