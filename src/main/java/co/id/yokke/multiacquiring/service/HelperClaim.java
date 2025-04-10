package co.id.yokke.multiacquiring.service;

import java.io.IOException;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import co.id.yokke.multiacquiring.common.constant.CommonMessage;
import co.id.yokke.multiacquiring.common.exception.CustomException;
import co.id.yokke.multiacquiring.common.util.ResponseUtil;
import co.id.yokke.multiacquiring.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * HelperService
 */

@Component
public class HelperClaim {

	private static String TOKEN_SECRET_KEY = "Aj32yuefhebAJSDS29384^%23sdsdSD2";
	
	public Claims claim(HttpServletRequest request) throws Exception {
//		String header = request.getHeader(JwtConfig.getHeader());
//		String token = header.replace(JwtConfig.getPrefix(), "");

//		return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(JwtConfig.getSecret()))
//				.parseClaimsJws(token).getBody();
		
		String header = request.getHeader(JwtConfig.getHeader());
		String token = header.replace(JwtConfig.getPrefix(), "");

		// Buat SecretKey dari string secret key
		SecretKey secretKey = new SecretKeySpec(TOKEN_SECRET_KEY.getBytes(), "AES");

		// Parsing token JWE
		JWEObject jweObject = JWEObject.parse(token);

		// Dekripsi dengan kunci simetris
		DirectDecrypter decrypter = new DirectDecrypter(secretKey);
		jweObject.decrypt(decrypter);

		// Dapatkan SignedJWT
		SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();

		// Verifikasi tanda tangan JWT
		MACVerifier verifier = new MACVerifier(TOKEN_SECRET_KEY.getBytes());
		if (!signedJWT.verify(verifier)) {
			throw new Exception("Signature verification failed");
		}

		// Dapatkan klaim dari JWT
		JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
		Map<String, Object> claimsMap = claimsSet.getClaims();

		// Konversi claimsMap ke Claims dan kembalikan
		Claims claims = Jwts.claims(claimsMap);
		return claims;
	}

	public String getCorporateCodes(HttpServletRequest request) {
		try {
			Claims claims = claim(request);
			return claims.get("corporateCode").toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public String type(HttpServletRequest request) {
		try {
			Claims claims = claim(request);
			String corporateType = claims.get("corporateType").toString();
			return corporateType;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	public String code(HttpServletRequest request) {
		try {
			Claims claims = claim(request);
			String code = claims.get("corporateCode").toString();
			return code;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	public String corpAdmin(HttpServletRequest request) {
		try {
			Claims claims = claim(request);
			String admin = claims.get("admin").toString();
			return admin;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	public String getUserId(HttpServletRequest request) {
		try {
			Claims claims = claim(request);
			String corporateType = claims.get("userId").toString();
			return corporateType;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	public Boolean isOwner(HttpServletRequest request) {
		try {
			Claims claims = claim(request);
			String corporateType = claims.get("corporateType").toString();
			return corporateType.equals("OWNER");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	public void handling(HttpServletRequest req, HttpServletResponse rsp) {
		try {
			rsp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			rsp.setContentType("application/json");
			rsp.getWriter().write(new ObjectMapper().writeValueAsString(response(req)));
			rsp.flushBuffer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Object response(HttpServletRequest req) {
		try {
			claim(req);
		} catch (Exception e) {
			if (e.getMessage().contains("JWT expired"))
				return ResponseUtil.responseObject(
						new CustomException(HttpStatus.UNAUTHORIZED, CommonMessage.SHORT_CODE_EXPIRE),
						req.getHeader("Accept-Language"));
			else
				return ResponseUtil.responseObject(
						new CustomException(HttpStatus.UNAUTHORIZED, CommonMessage.SHORT_CODE_TOKEN_INVALID),
						req.getHeader("Accept-Language"));
		}

		return ResponseUtil.responseObject(
				new CustomException(HttpStatus.UNAUTHORIZED, CommonMessage.SHORT_CODE_INSUFFICIENT_PRIVILEGES),
				req.getHeader("Accept-Language"));
	}
}