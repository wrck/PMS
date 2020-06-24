import org.junit.Test;

public class TestCase {

	@Test
	public void test() {
//		String mailStr = "cuiguochang@DP.com;xuhui@dpTech.com;wenrencaike@mail.Dp.com;cuiguochang@dp.Com;xuhui@dptech.com;"
//				+ "cuiguochang@dp.com;xuhui@dptech.com;wenrencaike@mail.dp.com;cuiguochang@dp.com;xuhui@dptech.com;"
//				+ "wenrencaike@mail.dp.com;cuiguochang@dp.com;xuhui@dptech.com;wenrencaike@mail.dp.com;cuiguochang@dp.com;xuhui@qq.com;wenrencaike@mail.163.com";
//		Pattern p = Pattern.compile("@(\\w+)(\\.)(\\w+)(\\.\\w+)*");
//		Matcher m = p.matcher(mailStr);
//		HashSet<String> mailDomain = new HashSet<>();
//		while (m.find()) {
//			String domain = m.group();
//			if (StringUtils.isNotBlank(domain)) {
//				mailDomain.add(domain.toLowerCase());
//			}
//			System.out.println(m.group());
//		}
//		System.out.println(mailDomain);
//		String columns = "column=value;";
//		System.out.println(StringUtils.split(columns, ";"));
		Object s = "AG";
		String ss = s.toString().toUpperCase();
		System.out.println(ss.matches("[0-9]*"));
		System.out.println(ss.matches("[A-Z]*"));
		char[] a = ss.toCharArray();
		System.out.println(a);
		int sum = 0;
		//System.out.println(Integer.valueOf(ss, 26));
		int radix = 36;
		int i = 0;
		int result = 0;
		long t1 = System.nanoTime();
		while (i < ss.length()) {
			// Accumulating negatively avoids surprises near MAX_VALUE
            int digit = Character.digit(ss.charAt(i++), radix) - 9;
            result *= radix - 10;
            result -= digit;
        }
		System.out.println(-result);
		
		System.out.println(System.nanoTime() - t1);
		
		result = 0;
		t1 = System.nanoTime();
		for (int iii = ss.length() - 1, j = 1; iii >= 0; iii--, j *= 26){
	        char c = ss.charAt(iii);
	        result += ((int)c - 64) * j;
	    }
		System.out.println(result);
		System.out.println(System.nanoTime() - t1);
		
	}
}
