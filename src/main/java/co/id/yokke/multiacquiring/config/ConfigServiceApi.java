package co.id.yokke.multiacquiring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "config.service.api")
@PropertySource("classpath:config.properties")
public class ConfigServiceApi {

	private String cekMcc;
	private String validationMcc;
	private String cekUpdateMcc;
	private String verifyToken;

	public String getVerifyToken() {
		return verifyToken;
	}

	public void setVerifyToken(String verifyToken) {
		this.verifyToken = verifyToken;
	}

	public String getCekUpdateMcc() {
		return cekUpdateMcc;
	}

	public void setCekUpdateMcc(String cekUpdateMcc) {
		this.cekUpdateMcc = cekUpdateMcc;
	}

	public String getValidationMcc() {
		return validationMcc;
	}

	public void setValidationMcc(String validationMcc) {
		this.validationMcc = validationMcc;
	}

	public String getCekMcc() {
		return cekMcc;
	}

	public void setCekMcc(String cekMcc) {
		this.cekMcc = cekMcc;
	}

}
