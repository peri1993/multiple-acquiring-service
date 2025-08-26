package co.id.yokke.multiacquiring.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.id.yokke.multiacquiring.model.DataMasterModel;
import co.id.yokke.multiacquiring.model.DataTempModel;
import co.id.yokke.multiacquiring.repository.DataPtenMasterRepository;
import co.id.yokke.multiacquiring.repository.DataTempRepository;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.SFTPClient;

@Service
@Transactional
@Slf4j
public class DataTempService extends BaseService {

	@Autowired
	private DataTempRepository dataTempRepository;
	@Autowired
	private DataPtenMasterRepository dataPtenMasterRepository;

	private static final String FILE_REGENERATE = "regenerate-qr.xlsx";

	public void deleteAllTempMid() {
		dataTempRepository.deleteAll();
	}

	public void createTemplate() throws IOException {
		ByteArrayResource resource = null;
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Data Regenerate QRIS Static");

			// Create header row
			Row headerRow = sheet.createRow(0);
			String[] columns = { "MID", "NMID" };

			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
			}

			// Write to output stream
			workbook.write(out);
			resource = new ByteArrayResource(out.toByteArray());
		}

		SSHClient sshClient = setupJsch();
		try {
			SFTPClient sftpClient = sshClient.newSFTPClient();
			String remoteFile = config.getRegenerate() + FOLDER_REGENERATE();
			String folderTemp = FOLDER_TEMP_QR_NOW();

			// Tentukan nama file tujuan
			File file = new File(folderTemp + FILE_REGENERATE);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}

			// Simpan ke file
			try (FileOutputStream fos = new FileOutputStream(file)) {
				fos.write(resource.getByteArray());
			}

			String localDir = file.getPath();

			sftpClient.mkdirs(remoteFile);
			sftpClient.put(localDir, remoteFile + FILE_REGENERATE);

			sftpClient.close();
			sshClient.disconnect();

			if (file.exists()) {
				file.delete();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean cekingForRegenerate() {

		SSHClient sshClient = setupJsch();
		String remoteFile = config.getRegenerate() + FOLDER_REGENERATE() + FILE_REGENERATE;
		try {
			SFTPClient sftpClient = sshClient.newSFTPClient();
			Boolean var = sftpClient.statExistence(remoteFile) != null;

			sftpClient.close();
			sshClient.disconnect();

			return var;
		} catch (IOException e) {
			log.error("SFTP-exists check failed for {}", remoteFile, e);
			return false;
		}
	}

	@Async
	public void saveTempMID() throws IOException {
		if (cekingForRegenerate()) {
			SSHClient sshClient = setupJsch();
			String remoteFilePath = config.getRegenerate() + FOLDER_REGENERATE() + FILE_REGENERATE;

			try (SFTPClient sftpClient = sshClient.newSFTPClient();
					RemoteFile remoteFile = sftpClient.open(remoteFilePath);
					InputStream inputStream = remoteFile.new RemoteFileInputStream(0);
					Workbook workbook = new XSSFWorkbook(inputStream)) {

				Sheet sheet = workbook.getSheetAt(0);
				Iterator<Row> rowIterator = sheet.iterator();

				Row headerRow = sheet.getRow(0);
				if (headerRow == null) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
							"Data Tidak Sesuai Cek Dan Upload Kembali");
				}

				// validate header
				boolean validateHeader = true;
				for (int i = 0; i < expectedHeaders().size(); i++) {
					Cell cell = headerRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
					String actual = cell != null ? cell.getStringCellValue().trim() : "";
					String expected = expectedHeaders().get(i).trim();

					if (!expected.equals(actual)) {
						validateHeader = false;
					}
				}

				// Skip header row
				if (rowIterator.hasNext()) {
					rowIterator.next();
				}

				if (validateHeader) {
					while (rowIterator.hasNext()) {
						Row row = rowIterator.next();

						if (row.getCell(0) != null) {
							Cell cell = row.getCell(0);
							String cellValue = getStringValue(cell);

							if (cellValue == null || cellValue.trim().isEmpty()) {
								// Kosong (null atau hanya whitespace)
								continue;
							}
						}
						if (row.getCell(0) == null && row.getCell(1) == null && row.getCell(2) == null
								&& row.getCell(3) == null && row.getCell(4) == null) {
							continue;
						}

						// save data to temp LM MID
						DataTempModel lm = new DataTempModel();
						lm.setMid(getStringValue(row.getCell(0)));
						String nmid = getStringValue(row.getCell(1));

						List<DataMasterModel> list = dataPtenMasterRepository.listFindByMidAndNmid(lm.getMid(), nmid);
						if (!list.isEmpty()) {
							dataTempRepository.save(lm);
						}
					}
				}
				workbook.close();
			}
		}
	}

	private List<String> expectedHeaders() {
		List<String> expectedHeaders = Arrays.asList("MID", "NMID");

		return expectedHeaders;
	}

	private String getStringValue(Cell cell) {
		if (cell == null)
			return null;
		cell.setCellType(CellType.STRING); // safe fallback
		return cell.getStringCellValue();
	}

	@SuppressWarnings("rawtypes")
	@Async
	public void cek() {
		List<DataTempModel> list = dataTempRepository.findAll();
		Integer a = 1;
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			DataTempModel dataTempModel = (DataTempModel) iterator.next();
			List<DataMasterModel> model = dataPtenMasterRepository.listFindByMid(dataTempModel.getMid());
			if (!model.isEmpty()) {
				Boolean va = ceking(model.get(0).getNmid() + "_A01.png");
				if (va == false) {
					System.out.println("");
					System.out.println("Not Found " + a + " : " + model.get(0).getNmid());
					a++;
				}else {
//					System.out.println("Found " + a + " : " + model.get(0).getNmid());
					System.out.print(".");
					a++;
				}
			}
		}
		System.out.println("ceking done");
	}

	private boolean ceking(String file) {

		SSHClient sshClient = setupJsch();
		String remoteFile = config.getSFTPremoteDir() + FOLDER_REGENERATE() + "QR/" + file;
//		String remoteFile = config.getSFTPremoteDir() + FOLDER_YESTERDAY() + file;
		try {
			SFTPClient sftpClient = sshClient.newSFTPClient();
			Boolean var = sftpClient.statExistence(remoteFile) != null;

			sftpClient.close();
			sshClient.disconnect();

			return var;
		} catch (IOException e) {
			log.error("SFTP-exists check failed for {}", remoteFile, e);
			return false;
		}
	}
}
