package co.id.yokke.multiacquiring.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.xml.bind.DatatypeConverter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import co.id.yokke.multiacquiring.config.JwtConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {
	
	private static String TOKEN_SECRET_KEY = "Aj32yuefhebAJSDS29384^%23sdsdSD2";

	public JwtTokenAuthenticationFilter() {
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		// 1. get the authentication header. Tokens are supposed to be passed in the
		// authentication header
		String header = request.getHeader(JwtConfig.getHeader());

		// 2. validate the header and check the prefix
		if (header == null || !header.startsWith(JwtConfig.getPrefix())) {
			chain.doFilter(request, response); // If not valid, go to the next filter.
			return;
		}

		// If there is no token provided and hence the user won't be authenticated.
		// It's Ok. Maybe the user accessing a public path or asking for a token.

		// All secured paths that needs a token are already defined and secured in
		// config class.
		// And If user tried to access without access token, then he won't be
		// authenticated and an exception will be thrown.

		// 3. Get the token
		String token = header.replace(JwtConfig.getPrefix(), "");

		try { // exceptions might be thrown in creating the claims if for example the token is
				// expired

			// 4. Validate the token

//			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(JwtConfig.getSecret())).parseClaimsJws(token)
//					.getBody();

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
			JWTClaimsSet claimsSet = null;
			if (signedJWT.verify(verifier)) {
				// Verifikasi berhasil
				claimsSet = signedJWT.getJWTClaimsSet();
//							System.out.println("Claims: " + claimsSet.toJSONObject());
			}

			String username = claimsSet.getSubject();
			if (username != null) {
				@SuppressWarnings("unchecked")
				List<String> authorities = (List<String>) claimsSet.getClaims().get("authorities");
				// Boolean isHaveAccess = false;
				// // 5. Create auth object
				// // UsernamePasswordAuthenticationToken: A built-in object, used by spring to
				// // represent the current authenticated / being authenticated user.
				// // It needs a list of authorities, which has type of GrantedAuthority
				// interface,
				// // where SimpleGrantedAuthority is an implementation of that interface

				// for(String s : authorities){
				// if(s.equals("view_transaction") || s.equals("view_customer")){
				// isHaveAccess = true;
				// break;
				// }
				// }

				// UsernamePasswordAuthenticationToken auth = null;

				// if(isHaveAccess)
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
						authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

				// 6. Authenticate the user
				// Now, user is authenticated
				SecurityContextHolder.getContext().setAuthentication(auth);
			}

		} catch (Exception e) {
			// In case of failure. Make sure it's clear; so guarantee user won't be
			// authenticated
			SecurityContextHolder.clearContext();
		}

		// go to the next filter in the filter chain
		chain.doFilter(request, response);
	}

}