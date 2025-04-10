package co.id.yokke.multiacquiring.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.id.yokke.multiacquiring.model.DataPtenModel;

@Repository
public interface DataPtenRepository extends JpaRepository<DataPtenModel, String>{
	
	// ENV PROD
	@Query(value = "SELECT *  FROM DATA_PTEN_PROD_10 WHERE STATUS_PROSES = '1' ORDER BY OOB_ID FETCH FIRST 10 ROWS ONLY ", nativeQuery = true)
	List<DataPtenModel> listDataStatusProsesOne();
	
	@Query(value = "SELECT *  FROM DATA_PTEN_PROD_10 WHERE STATUS_PROSES = '0' AND QR_STRING IS NOT NULL ORDER BY OOB_ID FETCH FIRST 50 ROWS ONLY ", nativeQuery = true)
	List<DataPtenModel> listDataForCRC();
	
//	// ENV DEV
//	@Query(value = "SELECT *  FROM DATA_PTEN_5 WHERE STATUS_PROSES = '1' ORDER BY OOB_ID FETCH FIRST 50 ROWS ONLY ", nativeQuery = true)
//	List<DataPtenModel> listDataStatusProsesOne();
	
//	@Query(value = "SELECT *  FROM DATA_PTEN_5 WHERE STATUS_PROSES = '0' AND QR_STRING IS NOT NULL ORDER BY OOB_ID FETCH FIRST 50 ROWS ONLY ", nativeQuery = true)
//	List<DataPtenModel> listDataForCRC();
	
	@Query("SELECT pten FROM DataPtenModel pten WHERE pten.statusProses = '0'")
	List<DataPtenModel> listDataStatusProsesZero();

	@Query("SELECT pten FROM DataPtenModel pten WHERE pten.statusProses = '2' AND pten.qrString IS NOT NULL AND pten.sftpFlag = 'Y' AND pten.updateDate BETWEEN ?1 AND ?2 ")
	List<DataPtenModel> listDataForCSV(Timestamp start, Timestamp end);
	
	@Query("SELECT pten FROM DataPtenModel pten WHERE pten.statusProses = '2' AND pten.qrString IS NOT NULL AND (pten.sendFtp = 'N' OR pten.sendFtp IS NULL) AND pten.sftpFlag = 'Y' ")
	Page<DataPtenModel> uploadForSFTP(Pageable pageable);
	
	@Query("SELECT pten FROM DataPtenModel pten WHERE pten.statusProses = '2' AND pten.qrString IS NOT NULL AND (pten.sendAli = 'N' OR pten.sendAli IS NULL) AND pten.aliFlag = 'Y' ")
	Page<DataPtenModel> uploadForALI(Pageable pageable);
	
	@Query("SELECT pten FROM DataPtenModel pten WHERE pten.statusProses = '3' AND pten.qrString IS NOT NULL AND (pten.sendAli = 'Y' OR pten.sendFtp = 'Y') AND pten.updateDate BETWEEN ?1 AND ?2 ")
	List<DataPtenModel> removeDataTempQR(Timestamp start, Timestamp end);
	
	@Query("SELECT DISTINCT pten.mid FROM DataPtenModel pten "
			+ "WHERE (pten.sendFtp = 'N' OR pten.sendFtp IS NULL) "
			+ "AND (pten.sendAli = 'N' OR pten.sendAli IS NULL) "
			+ "AND pten.tidPten != 'A01' "
			+ "ORDER BY pten.mid ASC ")
	List<String> listZip();

	@Query("SELECT pten FROM DataPtenModel pten WHERE pten.mid = ?1 AND pten.nmid = ?2 ")
	List<DataPtenModel> listDataMidAndNmid(String mid, String nmid);
	
	@Query("SELECT pten FROM DataPtenModel pten WHERE pten.mid = ?1 ")
	List<DataPtenModel> listDataMid(String mid);
	
	@Query("SELECT pten FROM DataPtenModel pten WHERE lower(pten.qrString) not like lower(concat('%', pten.merchantName, '%')) ")
	List<DataPtenModel> lisDiffrentMerchantName();
}
