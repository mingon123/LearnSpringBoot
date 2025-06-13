package kr.spring.util;

public class StringUtil {
	//HTML을 허용하지 않으면서 줄바꿈
	public static String useBrNoHtml(String str){
		if(str == null) return null;
		
		return str.replaceAll("<", "&lt;")
				  .replaceAll(">", "&gt;")
				  .replaceAll("\r\n", "<br>")
				  .replaceAll("\r", "<br>")
				  .replaceAll("\n", "<br>");
	}
	//HTML을 허용하지 않음
	public static String useNoHtml(String str){
		if(str == null) return null;
		
		return str.replaceAll("<", "&lt;")
				  .replaceAll(">", "&gt;");
	}
	/**
	 *일정 문자열이후에 ...으로 처리 
	 *
	 */
	public static String shortWords(int length, String content){

		if(content.length() > length){
			return content.substring(0,length) + " ...";
		}
		return content;
	}
}
