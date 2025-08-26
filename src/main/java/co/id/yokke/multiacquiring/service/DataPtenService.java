package co.id.yokke.multiacquiring.service;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.WriterException;

import co.id.yokke.multiacquiring.common.constant.CommonConstanst;
import co.id.yokke.multiacquiring.dto.ValidationMerchantNameDTO;
import co.id.yokke.multiacquiring.model.DataMasterModel;
import co.id.yokke.multiacquiring.model.DataPtenModel;
import co.id.yokke.multiacquiring.repository.DataPtenMasterRepository;
import co.id.yokke.multiacquiring.repository.DataPtenRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;

@Service
@Transactional
public class DataPtenService extends BaseService {

	protected static Logger LOGGER = LoggerFactory.getLogger(BaseService.class);

	@Autowired
	private DataPtenRepository repository;
	@Autowired
	private DataPtenMasterRepository masterRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Async
	public void generateCsvNOW(String extName) {
		try {
			List<DataMasterModel> listModel = listDataCsvNOW();
			LOGGER.info("upload data for CSV : " + listModel.size() + " From " + extName);

			createCsvForSFTVNOW(listModel, extName);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Async
	public void generateCsv() {
		try {
			List<DataMasterModel> listModel = listDataCsv();
			LOGGER.info("upload data for CSV : " + listModel.size());
			createCsvForSFTV(listModel);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createCsvForSFTV(List<DataMasterModel> listModel) throws ParseException {

		try {
			File file = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DATE, -1);
			date = c.getTime();
			String yyyyMMdd = sdf.format(date);

			String fileName = CommonConstanst.FILE_NAME_CSV_PATH_MID + yyyyMMdd;
			String folderTemp = FOLDER_TEMP_QR_YESTERDAY();
			file = new File(folderTemp + fileName + CommonConstanst.CSV_EXT);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}

			PrintWriter writer = new PrintWriter(file);
			StringBuilder sb = new StringBuilder();

			sb.append(CommonConstanst.HEADER_CSV_MID);
			sb.append(CommonConstanst.CSV_SEPARATOR + CommonConstanst.HEADER_CSV_PATH);
			sb.append("\n");

			if (!listModel.isEmpty()) {
				List<DataMasterModel> listDistinct = listModel.stream()
						.filter(distinctByKey(p -> p.getNmid() + " " + p.getNmid())).collect(Collectors.toList());
				List<String> listSimilar = listSimilar();

				for (DataMasterModel data : listDistinct) {
					sb.append(data.getMid());
					if (listSimilar.contains(data.getMid())) {
						sb.append(CommonConstanst.CSV_SEPARATOR + data.getNmid() + CommonConstanst.EXT_ZIP);
					} else {
						sb.append(CommonConstanst.CSV_SEPARATOR + data.getNmid() + "_" + data.getTidPten()
								+ CommonConstanst.EXT_PNG);
					}
					sb.append("\n");

					data.setStatusProses(CommonConstanst.THREE_STRING);
					masterRepository.save(data);
				}
				LOGGER.info("count data update : " + listModel.size());
			}

			writer.write(sb.toString());
			writer.close();

			dataSendSFTPYesterday(file);
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createCsvForSFTVNOW(List<DataMasterModel> listModel, String extName) throws ParseException {
		try {
			File file = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			Date date = new Date();
			String yyyyMMdd = sdf.format(date);

			String fileName = CommonConstanst.FILE_NAME_CSV_PATH_MID + yyyyMMdd + extName;
			String folderTemp = FOLDER_TEMP_QR_NOW();
			file = new File(folderTemp + fileName + CommonConstanst.CSV_EXT);

			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			PrintWriter writer = new PrintWriter(file);
			StringBuilder sb = new StringBuilder();

			sb.append(CommonConstanst.HEADER_CSV_MID);
			sb.append(CommonConstanst.CSV_SEPARATOR + CommonConstanst.HEADER_CSV_PATH);
			sb.append("\n");

			if (!listModel.isEmpty()) {
				List<DataMasterModel> listDistinct = listModel.stream()
						.filter(distinctByKey(p -> p.getNmid() + " " + p.getNmid())).collect(Collectors.toList());
				List<String> listSimilar = listSimilarNOW();

				for (DataMasterModel data : listDistinct) {
					sb.append(data.getMid());
					if (listSimilar.contains(data.getMid())) {
						sb.append(CommonConstanst.CSV_SEPARATOR + data.getNmid() + CommonConstanst.EXT_ZIP);
					} else {
						sb.append(CommonConstanst.CSV_SEPARATOR + data.getNmid() + "_" + data.getTidPten()
								+ CommonConstanst.EXT_PNG);
					}
					sb.append("\n");

					data.setStatusProses(CommonConstanst.THREE_STRING);
					masterRepository.save(data);
				}
				LOGGER.info("count data update : " + listModel.size());
			}

			writer.write(sb.toString());
			writer.close();

			dataSendSFTP(file, false);
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<DataMasterModel> listDataCsvNOW() throws ParseException {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		date = c.getTime();
		String start = dateFormat.format(date);
		String end = dateFormat.format(date);

		start = start.concat(FORMAT_START_HOURS);
		end = end.concat(FORMAT_END_HOURS);
		
//		String ewe = " 08:41:00";
//		end = end.concat(ewe);
		List<DataMasterModel> listModel = masterRepository.listDataForCSV(formatStringToTimestamp(start),
				formatStringToTimestamp(end));

		return listModel;
	}

	public List<DataMasterModel> listDataCsv() throws ParseException {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, -1);
		date = c.getTime();
		String start = dateFormat.format(date);
		String end = dateFormat.format(date);

		start = start.concat(FORMAT_START_HOURS);
		end = end.concat(FORMAT_END_HOURS);

		List<DataMasterModel> listModel = masterRepository.listDataForCSV(formatStringToTimestamp(start),
				formatStringToTimestamp(end));

		return listModel;
	}

	public List<DataPtenModel> listTempQr() throws ParseException {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, -7);
		date = c.getTime();
		String start = dateFormat.format(date);
		String end = dateFormat.format(date);

		start = start.concat(FORMAT_START_HOURS);
		end = end.concat(FORMAT_END_HOURS);

		List<DataPtenModel> listModel = repository.removeDataTempQR(formatStringToTimestamp(start),
				formatStringToTimestamp(end));

		return listModel;
	}

	public List<DataPtenModel> listTempQrNOW() throws ParseException {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		date = c.getTime();
		String start = dateFormat.format(date);
		String end = dateFormat.format(date);

		start = start.concat(FORMAT_START_HOURS);
		end = end.concat(FORMAT_END_HOURS);

		List<DataPtenModel> listModel = repository.removeDataTempQR(formatStringToTimestamp(start),
				formatStringToTimestamp(end));

		return listModel;
	}

	public List<String> listSimilar() throws ParseException {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, -1);
		date = c.getTime();
		String start = dateFormat.format(date);
		String end = dateFormat.format(date);

		start = start.concat(FORMAT_START_HOURS);
		end = end.concat(FORMAT_END_HOURS);

		List<String> listModel = masterRepository.listZipDataMaster(formatStringToTimestamp(start),
				formatStringToTimestamp(end));

		return listModel;
	}

	public List<String> listSimilarNOW() throws ParseException {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		date = c.getTime();
		String start = dateFormat.format(date);
		String end = dateFormat.format(date);

		start = start.concat(FORMAT_START_HOURS);
		end = end.concat(FORMAT_END_HOURS);

		List<String> listModel = masterRepository.listZipDataMaster(formatStringToTimestamp(start),
				formatStringToTimestamp(end));

		return listModel;
	}

	public void callStoreProcedureUpdateFlagSFTP() {
		try {
			String query = config.getSpUpdateSftp();
			entityManager.createNativeQuery(query).executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void callStoreProcedure() {
		try {
			String query = config.getGenerateSP();
			entityManager.createNativeQuery(query).executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void callForRegenerate() {
		try {
			String query = config.getCallRegenerate();
			entityManager.createNativeQuery(query).executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void updateCrc() {
		List<DataPtenModel> listModel = repository.listDataForCRC();
		if (!listModel.isEmpty()) {
			for (DataPtenModel model : listModel) {
				String crc = generateChecksumCRC16(model.getQrString());
				if (null != crc) {
					if (crc.length() <= 4) {
						String paddingCrc = ("0000" + crc).substring(crc.length());
						model.setCrc(paddingCrc);
						model.setStatusProses(CommonConstanst.ONE_STRING);
						model.setUpdateDate(NOW_TIMESTAMP());
						repository.save(model);
					} else {
						model.setCrc(crc);
						model.setStatusProses(CommonConstanst.NINE_STRING);
						model.setUpdateDate(NOW_TIMESTAMP());
						repository.save(model);

					}
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void removeQrTempNOW() {
		try {
			List<DataPtenModel> listModel = listTempQrNOW();
			for (Iterator iterator = listModel.iterator(); iterator.hasNext();) {
				DataPtenModel model = (DataPtenModel) iterator.next();
				File initialFile = new File(FOLDER_TEMP_QR_NOW() + model.getNmid()
						+ CommonConstanst.DEFAULT_TID_PTEN_NAME + CommonConstanst.EXT_PNG);

				removeFileTemp(initialFile.getPath());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	@SuppressWarnings("rawtypes")
	public void removeQrTemp() {
		try {
			List<DataPtenModel> listModel = listTempQr();
			for (Iterator iterator = listModel.iterator(); iterator.hasNext();) {
				DataPtenModel model = (DataPtenModel) iterator.next();
				File initialFile = new File(FOLDER_TEMP_QR_YESTERDAY() + model.getNmid()
						+ CommonConstanst.DEFAULT_TID_PTEN_NAME + CommonConstanst.EXT_PNG);

				removeFileTemp(initialFile.getPath());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	@SuppressWarnings({ "rawtypes" })
	@Async
	public void uploadSFTP() {
		Page<DataPtenModel> page = repository.uploadForSFTP(PageRequest.of(0, CommonConstanst.LIMIT_PAGE));
		List<DataPtenModel> listModel = page.toList();
		if (!listModel.isEmpty()) {
			for (Iterator iterator = listModel.iterator(); iterator.hasNext();) {
				DataPtenModel model = (DataPtenModel) iterator.next();
				if (CommonConstanst.Y.equals(model.getSftpFlag())) {
					LOGGER.info("get data SFTP QR " + model.getNmid());
					File initialFile = new File(FOLDER_TEMP_QR_NOW() + model.getNmid()
							+ CommonConstanst.DEFAULT_TID_PTEN_NAME + CommonConstanst.EXT_PNG);
					if (initialFile.exists()) {
						Boolean flagRequest = false;
//						if(CommonConstanst.Y.equals(model.getReqFlag())) {
//							flagRequest = true;
//						}
						if (dataSendSFTP(initialFile, flagRequest)) {
							model.setSendFtp(CommonConstanst.Y);
							repository.save(model);

							LOGGER.info("upload data SFTP QR " + model.getNmid());
						}
					}
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes" })
	@Async
	public void uploadAli() throws FileNotFoundException {
		Page<DataPtenModel> page = repository.uploadForALI(PageRequest.of(0, CommonConstanst.LIMIT_PAGE));
		List<DataPtenModel> listModel = page.toList();
		if (!listModel.isEmpty()) {
			for (Iterator iterator = listModel.iterator(); iterator.hasNext();) {
				DataPtenModel model = (DataPtenModel) iterator.next();
				if (CommonConstanst.Y.equals(model.getAliFlag())) {
					LOGGER.info("get data ALI QR " + model.getNmid());
					if (CommonConstanst.LOCAL.equals(config.getRun()) || CommonConstanst.DEV.equals(config.getRun())) {
						LOGGER.info("get generate " + FOLDER_TEMP_QR_NOW() + model.getNmid()
								+ CommonConstanst.DEFAULT_TID_PTEN_NAME + CommonConstanst.EXT_PNG);
						File initialFile = new File(FOLDER_TEMP_QR_NOW() + model.getNmid()
								+ CommonConstanst.DEFAULT_TID_PTEN_NAME + CommonConstanst.EXT_PNG);
						InputStream in = new FileInputStream(initialFile);
						postToBucketOssAli(in, initialFile.getName(), FOLDER_NOW_OSS());

						model.setSendAli(CommonConstanst.Y);
						repository.save(model);
						LOGGER.info("upload data ALI QR " + model.getNmid());
					} else {

						File initialFile = new File(FOLDER_TEMP_QR_NOW() + model.getNmid()
								+ CommonConstanst.DEFAULT_TID_PTEN_NAME + CommonConstanst.EXT_PNG);
						if (!initialFile.exists()) {
							try {
								generateManuly(model);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						InputStream in = new FileInputStream(initialFile);
						postToBucketOssAliProd(in, initialFile.getName(), model.getNmid());

						model.setSendAli(CommonConstanst.Y);
						repository.save(model);
						LOGGER.info("upload data ALI QR " + model.getNmid());

					}
				}
			}
		}
	}

	private void generateManuly(DataPtenModel model) throws IOException {

		String nmid = CommonConstanst.NMID_QR + model.getNmid();
		String tid = model.getTidPten();
		String by = CommonConstanst.CODE_MANDIRI_QR_DEFAULT;
		String print = CommonConstanst.VERSION_QR + versionDateForQR();
		String folderTemp = FOLDER_TEMP_QR_NOW();
		// config images all
		File file = null;

		try {
			BufferedImage img = null;
			URL url = new URL(config.getTemplateQR());
			img = ImageIO.read(url);
			Graphics g = img.getGraphics();
			BufferedImage barcode = generateQRMandiri(model);
			g.drawImage(barcode, positionXbarcode(barcode), positionYbarcode(barcode), null);

			if (model.getTittle2() == null || CommonConstanst.EMPTY_STRING.equals(model.getTittle2())) {
				if (model.getMerchantName() != null && !CommonConstanst.EMPTY_STRING.equals(model.getMerchantName())) {
					g.drawString(attributeTextMerchantName(model.getTittle1()).getIterator(),
							positionXmerchantName(img, model.getTittle1()), positionYmerchantName(img));
				}
			} else {
				String nameUp = model.getTittle1();
				String nameDown = model.getTittle2();
				g.drawString(attributeTextMerchantName(nameUp).getIterator(), positionXmerchantNameMandiri(img, nameUp),
						positionYmerchantNameMandiriUp(img));
				g.drawString(attributeTextMerchantName(nameDown).getIterator(), positionXmerchantName(img, nameDown),
						positionYmerchantName(img));
			}
			g.drawString(attributeTextNMID(nmid).getIterator(), positionXNMID(img, nmid), positionYNMID(img));
			g.drawString(attributeTextTID(tid).getIterator(), positionXTID(img, tid), positionYTID(img));
			g.drawString(attributeTextBy(by).getIterator(), positionXBy(img, by), positionYBy(img));
			g.drawString(attributeTextprint(print).getIterator(), positionXprint(img, print), positionYprint(img));

			LOGGER.info("file generate temp " + folderTemp + model.getNmid() + CommonConstanst.DEFAULT_TID_PTEN_NAME
					+ CommonConstanst.EXT_PNG);

			file = new File(
					folderTemp + model.getNmid() + CommonConstanst.DEFAULT_TID_PTEN_NAME + CommonConstanst.EXT_PNG);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}

			ImageIO.write(img, "png", new File(file.getPath()));

			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void createZip(String zipname, ArrayList<String> pathFiles) {
		try {

			// create byte buffer
			byte[] buffer = new byte[1024];
			FileOutputStream fos = new FileOutputStream(zipname);
			ZipOutputStream zos = new ZipOutputStream(fos);
			for (int i = 0; i < pathFiles.size(); i++) {
				File srcFile = new File(pathFiles.get(i));
				FileInputStream fis = new FileInputStream(srcFile);

				// begin writing a new ZIP entry, positions the stream to the start of the entry
				// data
				zos.putNextEntry(new ZipEntry(srcFile.getName()));
				int length;
				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
				zos.closeEntry();
				// close the InputStream
				fis.close();
			}
			// close the ZipOutputStream
			zos.close();
		} catch (IOException ioe) {
			System.out.println("Error creating zip file: " + ioe);
		}
	}

	@Async
	public void generate() throws IOException {
//		List<DataPtenModel> listZip = repository.listZip();
		List<String> listZip = repository.listZip();
		if (!listZip.isEmpty()) {
//			List<DataPtenModel> listDistinct = listZip.stream().filter(distinctByKey(p -> p.getNmid() + " " +p.getNmid())).collect(Collectors.toList());
			for (String model : listZip) {
				List<DataPtenModel> listModel = repository.listDataMid(model);
				File file = null;
				if (!listModel.isEmpty()) {
					String zipFile = FOLDER_TEMP_QR_NOW() + listModel.get(0).getNmid() + CommonConstanst.EXT_ZIP;
					ArrayList<String> pathFiles = new ArrayList<String>();
					String nmid = CommonConstanst.EMPTY_STRING;
					for (DataPtenModel multi : listModel) {
						nmid = CommonConstanst.NMID_QR + multi.getNmid();
						String tid = multi.getTidPten();
						String by = CommonConstanst.CODE_MANDIRI_QR_DEFAULT;
						String print = CommonConstanst.VERSION_QR + versionDateForQR();
						String folderTemp = FOLDER_TEMP_QR_NOW();
						// config images all

						try {
							BufferedImage img = null;
							URL url = new URL(config.getTemplateQR());
							img = ImageIO.read(url);
							Graphics g = img.getGraphics();
							BufferedImage barcode = generateQRMandiri(multi);
							g.drawImage(barcode, positionXbarcode(barcode), positionYbarcode(barcode), null);

							if (multi.getTittle2() == null || CommonConstanst.EMPTY_STRING.equals(multi.getTittle2())) {
								if (multi.getMerchantName() != null
										&& !CommonConstanst.EMPTY_STRING.equals(multi.getMerchantName())) {
									g.drawString(attributeTextMerchantName(multi.getTittle1()).getIterator(),
											positionXmerchantName(img, multi.getTittle1()), positionYmerchantName(img));
								}
							} else {
								String nameUp = multi.getTittle1();
								String nameDown = multi.getTittle2();
								g.drawString(attributeTextMerchantName(nameUp).getIterator(),
										positionXmerchantNameMandiri(img, nameUp), positionYmerchantNameMandiriUp(img));
								g.drawString(attributeTextMerchantName(nameDown).getIterator(),
										positionXmerchantName(img, nameDown), positionYmerchantName(img));
							}
							g.drawString(attributeTextNMID(nmid).getIterator(), positionXNMID(img, nmid),
									positionYNMID(img));
							g.drawString(attributeTextTID(tid).getIterator(), positionXTID(img, tid),
									positionYTID(img));
							g.drawString(attributeTextBy(by).getIterator(), positionXBy(img, by), positionYBy(img));
							g.drawString(attributeTextprint(print).getIterator(), positionXprint(img, print),
									positionYprint(img));

							LOGGER.info("file multi generate temp " + folderTemp + multi.getNmid()
									+ CommonConstanst.DEFAULT_TID_PTEN_NAME + CommonConstanst.EXT_PNG);

							file = new File(
									folderTemp + multi.getNmid() + "_" + multi.getTidPten() + CommonConstanst.EXT_PNG);
							if (!file.getParentFile().exists()) {
								file.getParentFile().mkdirs();
							}
							if (!file.exists()) {
								file.createNewFile();
							}

							ImageIO.write(img, "png", new File(file.getPath()));
							pathFiles.add(file.getPath());

							multi.setStatusProses(CommonConstanst.TWO_STRING);
							multi.setUpdateDate(NOW_TIMESTAMP());
							multi.setSendFtp(CommonConstanst.Y);
							repository.save(multi);
							try {
								Thread.sleep(1 * 1000);
							} catch (InterruptedException ie) {
								Thread.currentThread().interrupt();
							}
						} catch (WriterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					createZip(zipFile, pathFiles);
					LOGGER.info("upload multiple QR data to SFTP ");
					File initialFile = new File(zipFile);
					dataSendSFTP(initialFile, false);

					// remove zip file temp
					if (initialFile.exists()) {
						removeFileTemp(initialFile.getPath());
					}
				}
			}

		} else {
			List<DataPtenModel> listModel = repository.listDataStatusProsesOne();
			File file = null;
			if (!listModel.isEmpty()) {
				String nmid = CommonConstanst.EMPTY_STRING;
				for (DataPtenModel model : listModel) {
					nmid = CommonConstanst.NMID_QR + model.getNmid();
					String tid = model.getTidPten();
					String by = CommonConstanst.CODE_MANDIRI_QR_DEFAULT;
					String print = CommonConstanst.VERSION_QR + versionDateForQR();
					String folderTemp = FOLDER_TEMP_QR_NOW();
					// config images all

					try {
						BufferedImage img = null;
						URL url = new URL(config.getTemplateQR());
						img = ImageIO.read(url);
						Graphics g = img.getGraphics();
						BufferedImage barcode = generateQRMandiri(model);
						g.drawImage(barcode, positionXbarcode(barcode), positionYbarcode(barcode), null);

						if (model.getTittle2() == null || CommonConstanst.EMPTY_STRING.equals(model.getTittle2())) {
							if (model.getMerchantName() != null
									&& !CommonConstanst.EMPTY_STRING.equals(model.getMerchantName())) {
								g.drawString(attributeTextMerchantName(model.getTittle1()).getIterator(),
										positionXmerchantName(img, model.getTittle1()), positionYmerchantName(img));
							}
						} else {
							String nameUp = model.getTittle1();
							String nameDown = model.getTittle2();
							g.drawString(attributeTextMerchantName(nameUp).getIterator(),
									positionXmerchantNameMandiri(img, nameUp), positionYmerchantNameMandiriUp(img));
							g.drawString(attributeTextMerchantName(nameDown).getIterator(),
									positionXmerchantName(img, nameDown), positionYmerchantName(img));
						}
						g.drawString(attributeTextNMID(nmid).getIterator(), positionXNMID(img, nmid),
								positionYNMID(img));
						g.drawString(attributeTextTID(tid).getIterator(), positionXTID(img, tid), positionYTID(img));
						g.drawString(attributeTextBy(by).getIterator(), positionXBy(img, by), positionYBy(img));
						g.drawString(attributeTextprint(print).getIterator(), positionXprint(img, print),
								positionYprint(img));

						LOGGER.info("file generate temp " + folderTemp + model.getNmid()
								+ CommonConstanst.DEFAULT_TID_PTEN_NAME + CommonConstanst.EXT_PNG);

						file = new File(folderTemp + model.getNmid() + CommonConstanst.DEFAULT_TID_PTEN_NAME
								+ CommonConstanst.EXT_PNG);
						if (!file.getParentFile().exists()) {
							file.getParentFile().mkdirs();
						}
						if (!file.exists()) {
							file.createNewFile();
						}

						ImageIO.write(img, "png", new File(file.getPath()));

						model.setStatusProses(CommonConstanst.TWO_STRING);
						model.setUpdateDate(NOW_TIMESTAMP());
						repository.save(model);
						try {
							Thread.sleep(1 * 1000);
						} catch (InterruptedException ie) {
							Thread.currentThread().interrupt();
						}
					} catch (WriterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}
	}

	@SuppressWarnings("unused")
	private Object[] splitMerchantName(String merchantName) {
		String[] merchant = null;
		String[] object = merchantName.split("\\s");
		String merchant1 = null;
		String merchant2 = null;
		for (String part : object) {

		}

		return null;
	}

	private String generateChecksumCRC16(String qrString) {

		int crcc = 0xFFFF; // initial value
		int polynomial = 0x1021; // 0001 0000 0010 0001 (0, 5, 12)

		// byte[] testBytes = "123456789".getBytes("ASCII");

		byte[] bytes = qrString.getBytes();

		for (byte b : bytes) {
			for (int i = 0; i < 8; i++) {
				boolean bit = ((b >> (7 - i) & 1) == 1);
				boolean c15 = ((crcc >> 15 & 1) == 1);
				crcc <<= 1;
				if (c15 ^ bit)
					crcc ^= polynomial;
			}
		}

		crcc &= 0xffff;

		return Integer.toHexString(crcc);
	}

	@SuppressWarnings("unused")
	private String generateChecksumCRC32(String qrString) {
		byte[] bytes = qrString.getBytes();

		Checksum checksum = new CRC32();
		checksum.update(bytes, 0, bytes.length);

		long checksumValue = checksum.getValue();

		return Long.toHexString(checksumValue);
	}

	@SuppressWarnings("unused")
	private boolean dataSendSFTPCSV(File initialFile) {

		SSHClient sshClient = setupJsch();
		try {
			SFTPClient sftpClient = sshClient.newSFTPClient();
			String remoteFile = config.getSFTPremoteDir() + FOLDER_NOW_CSV();
			String localDir = initialFile.getPath();

			sftpClient.mkdirs(remoteFile);
			sftpClient.put(localDir, remoteFile + initialFile.getName());

			sftpClient.close();
			sshClient.disconnect();

			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private boolean dataSendSFTP(File initialFile, Boolean isRequest) {

		SSHClient sshClient = setupJsch();
		try {
			SFTPClient sftpClient = sshClient.newSFTPClient();
			String remoteFile = null;
			if (isRequest) {
				remoteFile = config.getSFTPrequestDir() + FOLDER_NOW();
			} else {
				remoteFile = config.getSFTPremoteDir() + FOLDER_NOW();
			}

			String localDir = initialFile.getPath();

			sftpClient.mkdirs(remoteFile);
			sftpClient.put(localDir, remoteFile + initialFile.getName());

			sftpClient.close();
			sshClient.disconnect();

			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private boolean dataSendSFTPYesterday(File initialFile) {

		SSHClient sshClient = setupJsch();
		try {
			SFTPClient sftpClient = sshClient.newSFTPClient();
			String remoteFile = config.getSFTPremoteDir() + FOLDER_YESTERDAY();
			String localDir = initialFile.getPath();

			sftpClient.mkdirs(remoteFile);
			sftpClient.put(localDir, remoteFile + initialFile.getName());

			sftpClient.close();
			sshClient.disconnect();

			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unused")
	private ValidationMerchantNameDTO validateName(String name) {
		ValidationMerchantNameDTO dto = new ValidationMerchantNameDTO();
		dto.setFlag(false);
		String[] obj = name.split("\\s");
		if (obj.length >= 3 && name.length() >= 25) {
			dto.setFlag(true);
			dto.setNames(obj);
		}
		return dto;
	}
}
