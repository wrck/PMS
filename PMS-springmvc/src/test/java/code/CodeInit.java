package code;

import java.util.Base64;

import javax.mail.internet.AddressException;

import com.dp.plat.MyBatisGenerator.CodeGenerator;

/**
 * @author w02611
 *
 */
public class CodeInit {
	
	public static void main(String[] args) throws AddressException {
//		CodeGenerator.generator(null);
	    System.out.println(Base64.getDecoder().decode("obW4uJgZmRo="));
	}
}
