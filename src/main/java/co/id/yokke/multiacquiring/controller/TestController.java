package co.id.yokke.multiacquiring.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.id.yokke.multiacquiring.common.constant.CommonConstanst;
import co.id.yokke.multiacquiring.dto.TestDto;
import co.id.yokke.multiacquiring.service.DataPtenService;
import co.id.yokke.multiacquiring.service.DataTempService;
import co.id.yokke.multiacquiring.service.TestService;

@RestController
public class TestController {

	@Autowired
	private DataPtenService service;
	@Autowired
	private TestService testService;
	@Autowired
	private DataTempService dataTempService;

	@GetMapping("/call-sp")
	public ResponseEntity<Object> callSp() throws IOException {
		service.callStoreProcedure();
		return new ResponseEntity<Object>(CommonConstanst.SUCCESS, HttpStatus.OK);
	}

	@GetMapping("/create-crc")
	public ResponseEntity<Object> createCrc() throws IOException {
		service.updateCrc();
		return new ResponseEntity<Object>(CommonConstanst.SUCCESS, HttpStatus.OK);
	}

	@GetMapping("/generate")
	public ResponseEntity<Object> generate() throws IOException {
		service.generate();
		return new ResponseEntity<Object>(CommonConstanst.SUCCESS, HttpStatus.OK);
	}

	@GetMapping("/data-csv")
	public ResponseEntity<Object> getCsv() throws IOException, ParseException {
		return new ResponseEntity<Object>(service.listDataCsv(), HttpStatus.OK);
	}

	@GetMapping("/upload-csv")
	public ResponseEntity<Object> uploadCsv() throws IOException, ParseException {
		service.generateCsv();
		return new ResponseEntity<Object>(CommonConstanst.SUCCESS, HttpStatus.OK);
	}

	@GetMapping("/upload-csv-now")
	public ResponseEntity<Object> uploadCsvNow(@RequestParam(value = "extName", defaultValue = "") String extName)
			throws IOException, ParseException {
		service.generateCsvNOW(extName);
		return new ResponseEntity<Object>(CommonConstanst.SUCCESS, HttpStatus.OK);
	}

	@GetMapping("/diffrent")
	public ResponseEntity<?> diffrent() throws IOException, ParseException {
		List<TestDto> listDto = testService.list();
		return new ResponseEntity<>(listDto, HttpStatus.OK);
	}

	@GetMapping("/cek")
	public ResponseEntity<?> cek() throws IOException, ParseException {
		dataTempService.cek();
		return new ResponseEntity<>(CommonConstanst.SUCCESS, HttpStatus.OK);
	}
	
	@GetMapping("/save-generate")
	public ResponseEntity<?> readTempMID() throws IOException, ParseException {
		dataTempService.saveTempMID();
		return new ResponseEntity<>(CommonConstanst.SUCCESS, HttpStatus.OK);
	}
	
	@GetMapping("/create-generate-temp")
	public ResponseEntity<?> saveTempMID() throws IOException, ParseException {
		dataTempService.createTemplate();
		return new ResponseEntity<>(CommonConstanst.SUCCESS, HttpStatus.OK);
	}
}
