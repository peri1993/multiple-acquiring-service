package co.id.yokke.multiacquiring.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.id.yokke.multiacquiring.model.DataMasterModel;

public interface DataPtenMasterRepository extends JpaRepository<DataMasterModel, String> {

	@Query("SELECT pten FROM DataMasterModel pten WHERE pten.statusProses = '2' AND pten.qrString IS NOT NULL AND pten.sendFtp = 'Y' AND pten.reqFlag != 'Y' AND pten.updateDate BETWEEN ?1 AND ?2 ")
	List<DataMasterModel> listDataForCSV(Timestamp start, Timestamp end);
	
	@Query("SELECT DISTINCT pten.mid FROM DataMasterModel pten "
			+ "WHERE pten.statusProses = '2' AND pten.qrString IS NOT NULL AND pten.sendFtp = 'Y' AND pten.reqFlag != 'Y' AND pten.updateDate BETWEEN ?1 AND ?2 "
			+ "AND pten.tidPten != 'A01' "
			+ "ORDER BY pten.mid ASC ")
	List<String> listZipDataMaster(Timestamp start, Timestamp end);
	
	@Query("select a from DataMasterModel a "
			+ "where a.mid = ?1 "
			+ "and a.nmid = ?2")
	List<DataMasterModel> listFindByMidAndNmid(String mid, String nmid);
	
	@Query("select a from DataMasterModel a "
			+ "where a.mid = ?1 ")
	List<DataMasterModel> listFindByMid(String mid);
}
