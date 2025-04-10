package co.id.yokke.multiacquiring.config;

public class JwtConfig {

	private static String Uri = "/auth/**";

	private static String header = "Authorization";

	private static String prefix = "Bearer";

	private static int expiration= 86400;

	private static String secret = "Aj32yuefhebAJSDS29384^%23sdsdSD23";

	public static String getUri() {
		return Uri;
	}

	public static String getHeader() {
		return header;
	}

	public static String getPrefix() {
		return prefix;
	}

	public static int getExpiration() {
		return expiration;
	}

	public static String getSecret() {
		return secret;
	}

}
