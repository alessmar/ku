package alessmar

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter
import java.io.File
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.math.BigDecimal
import java.math.BigInteger
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

enum class Hash() {
	MD5, RIPEMD160, SHA1, SHA256, SHA384, SHA512, WHIRLPOOL;

	companion object {
		init {
			Security.addProvider(BouncyCastleProvider())
		}
	}
	
	fun checksum(input: File, useUppercase: Boolean): String {
		val digest = MessageDigest.getInstance(name)
		
		BufferedInputStream(FileInputStream(input)).iterator().forEach {i ->
			digest.update(i)
		}
		
		val d = digest.digest()
		val result = String.format("%0" + (2 * d.size) + "X", BigInteger(1, d))
		return if(useUppercase) result else result.toLowerCase();
	}
}