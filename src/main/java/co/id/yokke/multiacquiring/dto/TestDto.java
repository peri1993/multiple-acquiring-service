package co.id.yokke.multiacquiring.dto;

public class TestDto {

	private String oobId;
	private String mid;
	private String qrString;
	private String merchantName;
	private String merchantNameOnQr;

	/**
	 * @return the qrString
	 */
	public String getQrString() {
		return qrString;
	}

	/**
	 * @param qrString the qrString to set
	 */
	public void setQrString(String qrString) {
		this.qrString = qrString;
	}

	/**
	 * @return the mid
	 */
	public String getMid() {
		return mid;
	}

	/**
	 * @param mid the mid to set
	 */
	public void setMid(String mid) {
		this.mid = mid;
	}

	/**
	 * @return the oobId
	 */
	public String getOobId() {
		return oobId;
	}

	/**
	 * @param oobId the oobId to set
	 */
	public void setOobId(String oobId) {
		this.oobId = oobId;
	}

	/**
	 * @return the merchantName
	 */
	public String getMerchantName() {
		return merchantName;
	}

	/**
	 * @param merchantName the merchantName to set
	 */
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	/**
	 * @return the merchantNameOnQr
	 */
	public String getMerchantNameOnQr() {
		return merchantNameOnQr;
	}

	/**
	 * @param merchantNameOnQr the merchantNameOnQr to set
	 */
	public void setMerchantNameOnQr(String merchantNameOnQr) {
		this.merchantNameOnQr = merchantNameOnQr;
	}

}
