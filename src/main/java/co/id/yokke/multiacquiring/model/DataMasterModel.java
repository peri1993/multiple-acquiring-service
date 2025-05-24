package co.id.yokke.multiacquiring.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "DATA_PTEN_PROD_MASTER_DEV")
//@Table(name = "DATA_PTEN_PROD_MASTER")
@Data
public class DataMasterModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "OOB_ID")
	private String oobId;

	@Column(name = "MID")
	private String mid;

	@Column(name = "MERCHANT_NAME")
	private String merchantName;

	@Column(name = "TID")
	private String tid;

	@Column(name = "KTP")
	private String ktp;

	@Column(name = "NPWP")
	private String npwp;

	@Column(name = "STATUS")
	private Long status;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "NMID")
	private String nmid;

	@Column(name = "QR_STRING")
	private String qrString;

	@Column(name = "STATUS_PROSES")
	private String statusProses;
	
	@Column(name = "CRC")
	private String crc;

	@Column(name = "UPDATE_DATE")
	private Timestamp updateDate;

	@Column(name = "TID_PTEN")
	private String tidPten;

	@Column(name = "SFTP_FLAG")
	private String sftpFlag;

	@Column(name = "TITTLE1")
	private String tittle1;

	@Column(name = "TITTLE2")
	private String tittle2;

	@Column(name = "SEND_FTP")
	private String sendFtp;

	@Column(name = "SEND_ALI")
	private String sendAli;

	@Column(name = "ALI_FLAG")
	private String aliFlag;

	@Column(name = "MB_NO")
	private String mbNo;

	@Column(name = "REQ_FLAG")
	private String reqFlag;

}
