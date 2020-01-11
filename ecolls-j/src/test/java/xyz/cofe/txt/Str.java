package xyz.cofe.txt;

public class Str {
    public static String repeat(String str,int cnt){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<cnt; i++ ){
            sb.append(str);
        }
        return sb.toString();
    }
}
