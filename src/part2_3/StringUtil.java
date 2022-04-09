package part2_3;

import java.security.MessageDigest;
import com.google.gson.GsonBuilder;

public class StringUtil {

	// Applies Sha256 to a string and returns the result.
	public static String applySha256(String input) {

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			// Applies sha256 to our input,
			byte[] hash = digest.digest(input.getBytes("UTF-8"));

			StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Short hand helper to turn Object into a json string
	public static String getJson(Object o) {
		return new GsonBuilder().setPrettyPrinting().create().toJson(o).trim();
	}

	// Returns difficulty string target, to compare to hash. eg difficulty of 5 will
	// return "00000"
	public static String getDificultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}

	// json to object
	public static Object getObject(String json) {

		return new GsonBuilder().setPrettyPrinting().create().fromJson(json.trim(), Object.class);

	}

	// json to Block
	public static Block getBlock(String json) {
		
		GsonBuilder jsonBuilder = new GsonBuilder().setLenient();
		return jsonBuilder.setPrettyPrinting().create().fromJson(json.trim(), Block.class);

	}

}
