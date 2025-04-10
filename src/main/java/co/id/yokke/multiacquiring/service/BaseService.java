package co.id.yokke.multiacquiring.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.AttributedString;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aliyun.oss.OSS;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import co.id.yokke.multiacquiring.common.constant.CommonConstanst;
import co.id.yokke.multiacquiring.config.Config;
import co.id.yokke.multiacquiring.config.ConfigFont;
import co.id.yokke.multiacquiring.model.DataPtenModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional
public class BaseService {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	public Config config;

	@Autowired
	protected OSS ossClient;

	@Autowired
	private ConfigFont configFont;
	
	public static final DateTimeFormatter formatter_date_zoneDDMMYYYY = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static final DateTimeFormatter formatter_date_UTC = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
			.withZone(ZoneOffset.UTC);
	public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat formatTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final String FORMAT_START_HOURS = " 00:00:00";
	public static final String FORMAT_END_HOURS = " 23:59:59";
	public static final Font font_merchant_name = new Font("Rubik ExtraBold", Font.BOLD, 50);
	public static final Font font_nmid = new Font("Poppins Regular", Font.TRUETYPE_FONT, 44);
	public static final Font font_tid = new Font("Poppins Regular", Font.TRUETYPE_FONT, 44);
	public static final Font font_by = new Font("Poppins Regular", Font.TRUETYPE_FONT, 37);
	public static final Font font_print = new Font("Poppins Regular", Font.TRUETYPE_FONT, 37);
	
	private static final Font ARIAL_FONT = new Font("Arial", Font.PLAIN, 24);
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) 
	{
	    Map<Object, Boolean> map = new ConcurrentHashMap<>();
	    return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	public String FOLDER_TEMP_QR_YESTERDAY() {
		return System.getProperty("user.dir") + FOLDER_YESTERDAY();
	}
	
	public String FOLDER_TEMP_QR_NOW() {
		return System.getProperty("user.dir") + FOLDER_NOW();
	}
	
	public int positionXmerchantNameMandiri(BufferedImage img, String merchantName) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_merchant_name);
		int positionX = 0;
		if(merchantName.length() <= 28) {
			positionX  = ((img.getWidth() + 50) - metric.stringWidth(merchantName)) / 2;
		}else {
			positionX  = ((img.getWidth() - 30) - metric.stringWidth(merchantName)) / 2;
		}
		return positionX;
	}
	
	public int positionYmerchantNameMandiriUp(BufferedImage img) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_merchant_name);
		int positionY = 0;
		positionY = ((img.getHeight() - 1100) - metric.getHeight()) / 2 + metric.getAscent();
		return positionY;
	}
	
	private static Font getFont(String urlFile) {
	    Font font = null;
	    if (urlFile == null) {
	        return ARIAL_FONT;
	    }

	    try {
	    	URL url = new URL(urlFile);
	    	URLConnection conn = url.openConnection();
	        InputStream myStream = conn.getInputStream();
	        font = Font.createFont(Font.TRUETYPE_FONT, myStream);
	        GraphicsEnvironment ge = GraphicsEnvironment
	                .getLocalGraphicsEnvironment();

	        ge.registerFont(font);
	        ge.getAvailableFontFamilyNames();

	    } catch (Exception ex) {
	        font = ARIAL_FONT;
	    }
	    return font;
	}

	public static String convertDateToString(Date date, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		String formattedDate = dateFormat.format(date);
		return formattedDate;

	}

	public ZonedDateTime convertStringToDateZone(String source) {
		LocalDate local = LocalDate.parse(source, formatter_date_UTC);

		ZonedDateTime result = local.atStartOfDay(ZoneId.systemDefault());

		return result;
	}

	public Timestamp formatStringToTimestamp(String source) throws ParseException {
		Date date = formatTimestamp.parse(source);
		Timestamp time = new Timestamp(date.getTime());
		return time;
	}

	public String postToBucketOssAli(InputStream fileStream, String fileName, String folder) {
		Date expiration = new Date(new Date().getTime() + 3600 * 100000);

		String bucketName = config.getBucket();
		String finalName = config.getFolderEndpoint() + folder + fileName;
		ossClient.putObject(bucketName, finalName, fileStream);
		String fileDownloadURI = ossClient.generatePresignedUrl(bucketName, fileName, expiration).toString();
		return fileDownloadURI;
	}
	
	public String postToBucketOssAliProd(InputStream fileStream, String fileName, String folder) {

		Date expiration = new Date(new Date().getTime() + 3600 * 100000);

		String bucketName = config.getBucket();
//		String name = CommonConstanst.EMPTY_STRING;
//		if(CommonConstanst.PROD.equals(config.getRun())) {
//			
//		}else {
//			
//		}
		String finalName = config.getFolderEndpoint() + folder +"/"+ fileName;
		ossClient.putObject(bucketName, finalName, fileStream);
		String fileDownloadURI = ossClient.generatePresignedUrl(bucketName, fileName, expiration).toString();
		return fileDownloadURI;
	}

	public void removeFileTemp(String directoryTemp) {
		File file = new File(directoryTemp);
		if (file.exists()) {
			file.delete();
		}
	}

	public String getUsername(HttpServletRequest request) {
		return request.getUserPrincipal().getName();
	}

	public Timestamp NOW_TIMESTAMP() {
		return new Timestamp(new Date().getTime());
	}

	public String NOW_DATE_TIME_STRING() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		String val = String.valueOf(dateFormat.format(date) + "_" + System.currentTimeMillis());
		return val;
	}

	public void fileZipper(File file) {
		StringBuilder sb = new StringBuilder();
		ZipOutputStream out;
		try {
			out = new ZipOutputStream(new FileOutputStream(file));
			ZipEntry zip = new ZipEntry(file.getName() + "1" + ".zip");
			out.putNextEntry(zip);

			byte[] data = sb.toString().getBytes();
			out.write(data, 0, data.length);
			out.closeEntry();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String FOLDER_NOW() {
		String folder = CommonConstanst.EMPTY_STRING;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		folder = String.valueOf(dateFormat.format(date));

		return "/" + folder + "/QR/";
	}
	
	public String FOLDER_YESTERDAY() {
		String folder = CommonConstanst.EMPTY_STRING;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, -1);
		date = c.getTime();
		folder = String.valueOf(dateFormat.format(date));

		return "/" + folder + "/QR/";
	}

	public String FOLDER_NOW_OSS() {
		String folder = CommonConstanst.EMPTY_STRING;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		folder = String.valueOf(dateFormat.format(date));

		return "oob/" + folder + "/";
	}
	
	public String FOLDER_NOW_OSS_PROD() {
		String folder = CommonConstanst.EMPTY_STRING;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		folder = String.valueOf(dateFormat.format(date));

		return folder + "/";
	}

	public String FOLDER_NOW_CSV() {
		String folder = CommonConstanst.EMPTY_STRING;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		folder = String.valueOf(dateFormat.format(date));

		return "/" + folder + "/CSV/";
	}
	
	public String versionDateForQR() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String dateString = dateFormat.format(date);
		String years = dateString.substring(0, 4);
		String month = dateString.substring(4, 6);
		String day = dateString.substring(6, 8);
		return years.concat(".").concat(month).concat(".").concat(day);
	}

	public String NOW_DATE_TIME_STRING_FORMAT_YYYY_MM_DD() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String val = String.valueOf(dateFormat.format(date));
		String years = val.substring(0, 4);
		String month = val.substring(4, 6);
		String day = val.substring(6, 8);
		return years.concat(".").concat(month).concat(".").concat(day);
	}

	public String STRING_DATE_NOW() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date today = new Date();
		String now = format.format(today);
		return "TO_TIMESTAMP('" + now + "', 'yyyy-mm-dd hh24:mi:ss')";
	}

	public int positionXbarcodeMandiri(BufferedImage img) {
		int positionX = ((img.getWidth() + 467)) / 2;
		return positionX;
	}

	public int positionYbarcodeMandiri(BufferedImage img) {
		int positionY = (img.getHeight() + 1460) / 2;
		return positionY;
	}

	public AttributedString attributeTextMerchantName(String merchantName) {
		AttributedString attributedText = new AttributedString(merchantName.toUpperCase());
		
		try {
			getFont(configFont.getRubikPath());
			attributedText.addAttribute(TextAttribute.FONT, new Font(configFont.getRubikName(), Font.PLAIN, 50));
			attributedText.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return attributedText;
	}

	public int positionXmerchantName(BufferedImage img, String merchantName) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_merchant_name);
		int positionX = 0;
		if(merchantName.length() <= 28 && merchantName.length() >= 20) {
			positionX  = ((img.getWidth() + 50) - metric.stringWidth(merchantName)) / 2;
		}else if(merchantName.length() < 20){
			positionX  = (img.getWidth() - metric.stringWidth(merchantName)) / 2;
		}else {
			positionX  = ((img.getWidth() - 30) - metric.stringWidth(merchantName)) / 2;
		}
		return positionX;
	}

	public int positionYmerchantName(BufferedImage img) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_merchant_name);
		int positionY = ((img.getHeight() - 980) - metric.getHeight()) / 2 + metric.getAscent();
		return positionY;
	}

	public AttributedString attributeTextMerchantName1(String merchantName) {
		AttributedString attributedText = new AttributedString(merchantName);
		attributedText.addAttribute(TextAttribute.FONT, font_merchant_name);
		attributedText.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
		return attributedText;
	}

	public int positionXmerchantName1(BufferedImage img, String merchantName) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_merchant_name);
		int positionX = (img.getWidth() - metric.stringWidth(merchantName)) / 2;
		return positionX;
	}

	public int positionYmerchantName1(BufferedImage img) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_merchant_name);
		int positionY = ((img.getHeight() - 380) - metric.getHeight()) / 2 + metric.getAscent();
		return positionY;
	}

	public AttributedString attributeTextMerchantName2(String merchantName) {
		AttributedString attributedText = new AttributedString(merchantName);
		attributedText.addAttribute(TextAttribute.FONT, font_merchant_name);
		attributedText.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
		return attributedText;
	}

	public int positionXmerchantName2(BufferedImage img, String merchantName) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_merchant_name);
		int positionX = (img.getWidth() - metric.stringWidth(merchantName)) / 2;
		return positionX;
	}

	public int positionYmerchantName2(BufferedImage img) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_merchant_name);
		int positionY = ((img.getHeight() - 380) - metric.getHeight()) / 2 + metric.getAscent();
		return positionY;
	}

	public AttributedString attributeTextNMID(String nmid) {
		AttributedString attributedText = new AttributedString(nmid);
		getFont(configFont.getPoppinsPath());
		attributedText.addAttribute(TextAttribute.FONT, new Font(configFont.getPoppinsName(), Font.PLAIN, 45));
		attributedText.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
		return attributedText;
	}

	public int positionXNMID(BufferedImage img, String nmid) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_nmid);
		int positionX = (img.getWidth() - metric.stringWidth(nmid)) / 2;
		return positionX;
	}

	public int positionYNMID(BufferedImage img) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_nmid);
		int positionY = ((img.getHeight() - 850) - metric.getHeight()) / 2 + metric.getAscent();
		return positionY;
	}

	public AttributedString attributeTextTID(String tid) {
		AttributedString attributedText = new AttributedString(tid);
		getFont(configFont.getPoppinsPath());
		attributedText.addAttribute(TextAttribute.FONT, new Font(configFont.getPoppinsName(), Font.PLAIN, 45));
		attributedText.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
		return attributedText;
	}

	public int positionXTID(BufferedImage img, String tid) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_tid);
		int positionX = (img.getWidth() - metric.stringWidth(tid)) / 2;
		return positionX;
	}

	public int positionYTID(BufferedImage img) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_tid);
		int positionY = ((img.getHeight() - 680) - metric.getHeight()) / 2 + metric.getAscent();
		return positionY;
	}

	public AttributedString attributeTextBy(String yokke) {
		AttributedString attributedText = new AttributedString(yokke);
		getFont(configFont.getPoppinsPath());
		attributedText.addAttribute(TextAttribute.FONT, new Font(configFont.getPoppinsPath(), Font.PLAIN, 37));
		attributedText.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
		return attributedText;
	}

	public int positionXBy(BufferedImage img, String yokke) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_by);
		int positionX = ((img.getWidth() - 350) - metric.stringWidth(yokke)) / 2;
		return positionX;
	}

	public int positionYBy(BufferedImage img) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_by);
		int positionY = ((img.getHeight() + 1360) - metric.getHeight()) / 2 + metric.getAscent();
		return positionY;
	}

	public AttributedString attributeTextprint(String print) {
		AttributedString attributedText = new AttributedString(print);
		getFont(configFont.getPoppinsPath());
		attributedText.addAttribute(TextAttribute.FONT, new Font(configFont.getPoppinsName(), Font.PLAIN, 37));
		attributedText.addAttribute(TextAttribute.FOREGROUND, Color.BLACK);
		return attributedText;
	}

	public int positionXprint(BufferedImage img, String print) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_print);
		int positionX = ((img.getWidth() - 270) - metric.stringWidth(print)) / 2;
		return positionX;
	}

	public int positionYprint(BufferedImage img) {
		Graphics g = img.getGraphics();
		FontMetrics metric = g.getFontMetrics(font_print);
		int positionY = ((img.getHeight() + 1440) - metric.getHeight()) / 2 + metric.getAscent();
		return positionY;
	}

	public int positionXbarcode(BufferedImage img) {
		int positionX = ((img.getWidth() - 147)) / 2;
		return positionX;
	}

	public int positionYbarcode(BufferedImage img) {
		int positionY = (img.getHeight() + 410) / 2;
		return positionY;
	}
	
	public BufferedImage generateQRMandiri(DataPtenModel model) throws WriterException {
		String barcodeText = model.getQrString().concat(model.getCrc().toUpperCase());

		QRCodeWriter barcodeWriter = new QRCodeWriter();

		Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 0); /* default = 4 */
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

		BitMatrix bitmatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 650, 750, hints);
		BufferedImage cropImages = MatrixToImageWriter.toBufferedImage(bitmatrix).getSubimage(0, 0, 650, 700);
		Graphics2D g1 = cropImages.createGraphics();
		g1.rotate(Math.toRadians(180), cropImages.getWidth() / 2, cropImages.getHeight() / 2);
		g1.drawImage(cropImages, null, 0, 0);

		cropImages = cropImages.getSubimage(0, 0, 650, 650);
		Graphics2D g2 = cropImages.createGraphics();
		g2.rotate(Math.toRadians(180), cropImages.getWidth() / 2, cropImages.getHeight() / 2);
		g2.drawImage(cropImages, null, 0, 0);

		return cropImages;
	}

}
