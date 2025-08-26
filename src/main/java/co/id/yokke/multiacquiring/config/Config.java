
package co.id.yokke.multiacquiring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "config")
@PropertySource("classpath:config.properties")
public class Config {

	private String generateSP;
	private String templateQR;
	private String run;
	private String bucket;
	private String folderEndpoint;

	private String SFTPusername;
	private String SFTPpassword;
	private String SFTPremoteHost;
	private String SFTPremoteDir;

	private String qrisStatic;
	private String qrisOob;

	private String spUpdateSftp;
	private String SFTPrequestDir;

	private String regenerate;
	private String callRegenerate;

	public String getCallRegenerate() {
		return callRegenerate;
	}

	public void setCallRegenerate(String callRegenerate) {
		this.callRegenerate = callRegenerate;
	}

	public String getRegenerate() {
		return regenerate;
	}

	public void setRegenerate(String regenerate) {
		this.regenerate = regenerate;
	}

	public String getSFTPrequestDir() {
		return SFTPrequestDir;
	}

	public void setSFTPrequestDir(String sFTPrequestDir) {
		SFTPrequestDir = sFTPrequestDir;
	}

	/**
	 * @return the spUpdateSftp
	 */
	public String getSpUpdateSftp() {
		return spUpdateSftp;
	}

	/**
	 * @param spUpdateSftp the spUpdateSftp to set
	 */
	public void setSpUpdateSftp(String spUpdateSftp) {
		this.spUpdateSftp = spUpdateSftp;
	}

	/**
	 * @return the qrisStatic
	 */
	public String getQrisStatic() {
		return qrisStatic;
	}

	/**
	 * @param qrisStatic the qrisStatic to set
	 */
	public void setQrisStatic(String qrisStatic) {
		this.qrisStatic = qrisStatic;
	}

	/**
	 * @return the qrisOob
	 */
	public String getQrisOob() {
		return qrisOob;
	}

	/**
	 * @param qrisOob the qrisOob to set
	 */
	public void setQrisOob(String qrisOob) {
		this.qrisOob = qrisOob;
	}

	/**
	 * @return the sFTPusername
	 */
	public String getSFTPusername() {
		return SFTPusername;
	}

	/**
	 * @param sFTPusername the sFTPusername to set
	 */
	public void setSFTPusername(String sFTPusername) {
		SFTPusername = sFTPusername;
	}

	/**
	 * @return the sFTPpassword
	 */
	public String getSFTPpassword() {
		return SFTPpassword;
	}

	/**
	 * @param sFTPpassword the sFTPpassword to set
	 */
	public void setSFTPpassword(String sFTPpassword) {
		SFTPpassword = sFTPpassword;
	}

	/**
	 * @return the sFTPremoteHost
	 */
	public String getSFTPremoteHost() {
		return SFTPremoteHost;
	}

	/**
	 * @param sFTPremoteHost the sFTPremoteHost to set
	 */
	public void setSFTPremoteHost(String sFTPremoteHost) {
		SFTPremoteHost = sFTPremoteHost;
	}

	/**
	 * @return the sFTPremoteDir
	 */
	public String getSFTPremoteDir() {
		return SFTPremoteDir;
	}

	/**
	 * @param sFTPremoteDir the sFTPremoteDir to set
	 */
	public void setSFTPremoteDir(String sFTPremoteDir) {
		SFTPremoteDir = sFTPremoteDir;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getFolderEndpoint() {
		return folderEndpoint;
	}

	public void setFolderEndpoint(String folderEndpoint) {
		this.folderEndpoint = folderEndpoint;
	}

	public String getRun() {
		return run;
	}

	public void setRun(String run) {
		this.run = run;
	}

	public String getTemplateQR() {
		return templateQR;
	}

	public void setTemplateQR(String templateQR) {
		this.templateQR = templateQR;
	}

	public String getGenerateSP() {
		return generateSP;
	}

	public void setGenerateSP(String generateSP) {
		this.generateSP = generateSP;
	}

}
