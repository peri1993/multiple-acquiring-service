package co.id.yokke.multiacquiring.controller;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import co.id.yokke.multiacquiring.common.constant.CommonConstanst;
import co.id.yokke.multiacquiring.service.DataPtenService;

@Component
public class SchedulerController {

	protected static Logger logger = LoggerFactory.getLogger(SchedulerController.class);

	@Autowired
	private DataPtenService servicePten;

	@Scheduled(cron = "2 * * * * *")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void schedulerReplaceQr() throws IOException {
		logger.info("call SP ");
		servicePten.callStoreProcedure();

		logger.info("update CRC ");
		servicePten.updateCrc();

		logger.info("generate QR ");
		servicePten.generate();
	}

	//// 6 am
	@Scheduled(cron = "0 0 6 * * ?")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void removeQrTemp() {

		logger.info("remove qr temp ");
		servicePten.removeQrTemp();
	}

	@Scheduled(cron = "*/10 * * * * *")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void uploadSFTP() throws FileNotFoundException {
		servicePten.uploadSFTP();

//		servicePten.uploadAli();
	}

// Hanya untuk development	
//	@Scheduled(cron = "5 * * * * *")
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	public void schedulerUpdateMasterFlag() throws IOException {
//		logger.info("call SP Update flag SFTP");
//		servicePten.callStoreProcedureUpdateFlagSFTP();
//	}
	
//	===============================================================Data Job DEVELOPMENT & PRODUCTION ===========================================================================

	
//	//// 12 am
//	@Scheduled(cron = "0 0 0 * * ?")
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	public void schedulerUploadCsv() {
//
//		logger.info("upload CSV to SFTV 12 am");
//		servicePten.generateCsv();
//
//	}
//
//	// 08:41 Am
//	@Scheduled(cron = "0 41 8 * * ?")
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	public void schedulerUploadCsvNOW8() {
//
//		logger.info("upload CSV to SFTV 8:41 pm");
//		servicePten.generateCsvNOW(CommonConstanst.EXT_CSV_1);
//
//		logger.info("remove qr temp ");
//		servicePten.removeQrTempNOW();
//	}
//
//	// 09:41 Am
//	@Scheduled(cron = "0 41 9 * * ?")
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	public void schedulerUploadCsvNOW9() {
//
//		logger.info("upload CSV to SFTV 9:41 pm");
//		servicePten.generateCsvNOW(CommonConstanst.EXT_CSV_2);
//
//		logger.info("remove qr temp ");
//		servicePten.removeQrTempNOW();
//	}
//
//	// 10:41 Am
//	@Scheduled(cron = "0 41 10 * * ?")
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	public void schedulerUploadCsvNOW10() {
//
//		logger.info("upload CSV to SFTV 10:41 pm");
//		servicePten.generateCsvNOW(CommonConstanst.EXT_CSV_3);
//
//		logger.info("remove qr temp ");
//		servicePten.removeQrTempNOW();
//	}
//
//	// 11:41 Am
//	@Scheduled(cron = "0 41 11 * * ?")
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	public void schedulerUploadCsvNOW11() {
//
//		logger.info("upload CSV to SFTV 11:41 pm");
//		servicePten.generateCsvNOW(CommonConstanst.EXT_CSV_4);
//
//		logger.info("remove qr temp ");
//		servicePten.removeQrTempNOW();
//	}
//
//	// 13:41 Am
//	@Scheduled(cron = "0 41 13 * * ?")
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	public void schedulerUploadCsvNOW13() {
//
//		logger.info("upload CSV to SFTV 13:41 pm");
//		servicePten.generateCsvNOW(CommonConstanst.EXT_CSV_5);
//
//		logger.info("remove qr temp ");
//		servicePten.removeQrTempNOW();
//	}
//
//	// 14:41 Am
//	@Scheduled(cron = "0 41 14 * * ?")
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	public void schedulerUploadCsvNOW14() {
//
//		logger.info("upload CSV to SFTV 14:41 pm");
//		servicePten.generateCsvNOW(CommonConstanst.EXT_CSV_6);
//
//		logger.info("remove qr temp ");
//		servicePten.removeQrTempNOW();
//	}
//
//	// 15:41 Am
//	@Scheduled(cron = "0 41 15 * * ?")
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	public void schedulerUploadCsvNOW15() {
//
//		logger.info("upload CSV to SFTV 15:41 pm");
//		servicePten.generateCsvNOW(CommonConstanst.EXT_CSV_7);
//
//		logger.info("remove qr temp ");
//		servicePten.removeQrTempNOW();
//	}
//
//	// 16:41 Am
//	@Scheduled(cron = "0 41 16 * * ?")
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	public void schedulerUploadCsvNOW16() {
//
//		logger.info("upload CSV to SFTV 16:41 pm");
//		servicePten.generateCsvNOW(CommonConstanst.EXT_CSV_8);
//
//		logger.info("remove qr temp ");
//		servicePten.removeQrTempNOW();
//	}

}
