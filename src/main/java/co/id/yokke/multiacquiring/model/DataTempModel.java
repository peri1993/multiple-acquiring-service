package co.id.yokke.multiacquiring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "LM_MID_TEMP_DEV")
@Data
public class DataTempModel {
	
	@Id
	@Column(name = "MID")
	private String mid;

}
