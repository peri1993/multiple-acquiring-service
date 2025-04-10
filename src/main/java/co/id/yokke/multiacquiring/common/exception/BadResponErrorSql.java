package co.id.yokke.multiacquiring.common.exception;

import co.id.yokke.multiacquiring.common.constant.CommonMessage;
import org.springframework.http.HttpStatus;

public class BadResponErrorSql extends CustomException{

	public BadResponErrorSql() {
		super(HttpStatus.BAD_REQUEST, CommonMessage.SHORT_CODE_SQL_ERROR);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
