package code;

import java.util.Arrays;

public class Mailtest {

	private static String quoteSplit(String split) {
		if (!split.matches(".*[\\$|\\(|\\)|\\*|\\+|\\.|\\[|\\]|\\?|\\\\|\\/|\\^|\\{|\\}].*")) {
			return split;
		}
		char[] signs =  new char[] {'$','(',')','*','+','.','[',']','?','\\','/','^','{','}'};
		Arrays.sort(signs);
		StringBuilder sb = new StringBuilder();
        for (int i=0; i<split.length(); i++) {
            char c = split.charAt(i);
            if (Arrays.binarySearch(signs, c) >= 0) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
	}
	public static void main(String[] args) {
//		String content = "ssad${randomPassword}sskdsH$#sjdsds#A";
//		String replace = "Pk~$YUA$";
//		replace = java.util.regex.Matcher.quoteReplacement(replace);  
//		content = content.replaceAll("\\Q${randomPassword}\\E", replace);
//		System.out.println(content);
//		content = content.replaceAll("\\$#(\\w+)#", "");
//		System.out.println(content);
		String split = "${";
		System.out.println(split + "   " + quoteSplit(split));
		split = "~!@#$%^&*()_+/{}|:<>?";
		System.out.println(split + "   " + quoteSplit(split));
		split = "$";
		System.out.println(split + "   " + quoteSplit(split));
	}
}
