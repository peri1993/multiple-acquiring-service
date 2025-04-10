package co.id.yokke.multiacquiring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "configfont")
@PropertySource("classpath:config.properties")
public class ConfigFont {

	private String rubikPath;
	private String rubikName;
	private String poppinsPath;
	private String poppinsName;

	/**
	 * @return the rubikPath
	 */
	public String getRubikPath() {
		return rubikPath;
	}

	/**
	 * @param rubikPath the rubikPath to set
	 */
	public void setRubikPath(String rubikPath) {
		this.rubikPath = rubikPath;
	}

	/**
	 * @return the rubikName
	 */
	public String getRubikName() {
		return rubikName;
	}

	/**
	 * @param rubikName the rubikName to set
	 */
	public void setRubikName(String rubikName) {
		this.rubikName = rubikName;
	}

	/**
	 * @return the poppinsPath
	 */
	public String getPoppinsPath() {
		return poppinsPath;
	}

	/**
	 * @param poppinsPath the poppinsPath to set
	 */
	public void setPoppinsPath(String poppinsPath) {
		this.poppinsPath = poppinsPath;
	}

	/**
	 * @return the poppinsName
	 */
	public String getPoppinsName() {
		return poppinsName;
	}

	/**
	 * @param poppinsName the poppinsName to set
	 */
	public void setPoppinsName(String poppinsName) {
		this.poppinsName = poppinsName;
	}

}
