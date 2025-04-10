package co.id.yokke.multiacquiring.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ListModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.id.yokke.multiacquiring.dto.TestDto;
import co.id.yokke.multiacquiring.model.DataPtenModel;
import co.id.yokke.multiacquiring.repository.DataPtenMasterRepository;
import co.id.yokke.multiacquiring.repository.DataPtenRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
public class TestService extends BaseService{
	
	protected static Logger log = LoggerFactory.getLogger(TestService.class);
	
	@Autowired
	private DataPtenRepository repository;
	@Autowired
	private DataPtenMasterRepository masterRepository;
	@PersistenceContext
	private EntityManager entityManager;
	
	public List<TestDto> list(){
		List<TestDto> listDto = new ArrayList<TestDto>();
		
		List<DataPtenModel> listModel = repository.lisDiffrentMerchantName();
		log.info("size list " + listModel.size());
		for (Iterator iterator = listModel.iterator(); iterator.hasNext();) {
			DataPtenModel dataPtenModel = (DataPtenModel) iterator.next();
			String merNameOnQr =  dataPtenModel.getQrString();
			String[] parts = merNameOnQr.split("ID59");
			if (parts[1] != null) {
				//log.info(dataPtenModel.getNmid() + " " + parts[1]);
				if(!dataPtenModel.getMerchantName().equals(parts[1])) {
					int digit = Integer.parseInt(parts[1].substring(0, 2));
					if(digit <= 25) {
						String value = parts[1].substring(2);
						String valName = value.substring(0, digit);
						log.info(dataPtenModel.getMerchantName() +" "+ valName.length());
						String valOldName = dataPtenModel.getMerchantName();
						if(valOldName.length() > 25) {
							valOldName = valOldName.substring(0, valName.length());
							if(!valOldName.toUpperCase().equals(valName.toUpperCase())) {
								TestDto dto = setFromModel(dataPtenModel, valName);
								listDto.add(dto);
							}
						}else {
							if(!valOldName.toUpperCase().equals(valName.toUpperCase())) {
								TestDto dto = setFromModel(dataPtenModel, valName);
								listDto.add(dto);
							}
						}
						
					}else {
						TestDto dto = setFromModel(dataPtenModel, null);
						listDto.add(dto);
					}
				}
			}
			
		}
		log.info("total jumlah beda : " + listDto.size());
		return listDto;
		//return listDto.stream().limit(20).collect(Collectors.toList());
	}
	
	private TestDto setFromModel(DataPtenModel model, String merchantNameOnQr) {
		TestDto dto = new TestDto();
		dto.setOobId(model.getOobId());
		dto.setMid(model.getMid());
		dto.setQrString(model.getQrString());
		dto.setMerchantName(model.getMerchantName());
		dto.setMerchantNameOnQr(merchantNameOnQr);
		return dto;
	}

}
