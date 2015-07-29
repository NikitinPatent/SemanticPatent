package org.parser;

public class Segment {
	
public static String segmentEng(String content){
    String[] sentencesIm = { "which", "who", "what", "that" };
    String[] sentencesDel = { ", and ", " if ", " else ", " thereby ", 
    		" such that ", " so that ", " where ", " whereby ", 
    		" wherein ", " when ", " while ", " but " };
    /*
	String[] sentencesDel ={
			" at least ", " further ", " however, " 
	};
    */
    content = segment(content, sentencesIm, sentencesDel);

    return content;
}

/** segment - производит замену слов, удаление слов и разбивает строки.
 *
 * Входные параметры:
 * String content - документ записи;
 * String[] sentences - массив слов замены;
 * String[] sentencesDel - массив удаляемых слов.
 *
 * Выходные данные:
 * String - преобразованный документ.
 */
private static String segment(String content, String[] sentencesIm, String[] sentencesDel){

	for (String s : sentencesDel){
        content = content.replace(s, ".\n" + s);
    }
	
	String[] sentencesIm1 = {"on", "at", "in", "to", ""};
    for (String s : sentencesIm)
    {
    	for (String s1 : sentencesIm1)
    	{
    		String s2 = " " + s1 + " " + s + " ";
    		if (content.indexOf(s2)!=-1)
    		{
    			content = content.replace(s2, ".\n " + s2);
    			break;
    		}
    	}
    }

    content = content.replace("?", " ");
    content = content.replace(":", ".\n");
    content = content.replace(";", ".\n");

    content = content.replaceAll("\\b[a-zA-Z]\\)\\.?", ""); //Убираем а) А).
    content = content.replaceAll("\\b[0-9]{1,3}\\)?\\.", ""); //Убираем 10. 1).
    
    content = content.replaceAll("\\,[,\\s?.]{2,}+",".\n"); //Убираем ,  ... .
    content = content.replaceAll("\\.[.\\s?]{1,}+", ".\n"); //Убираем ... .
    content = content.replaceAll("\\n[\\\\n\\.?\\s?]{1,}+", "\n"); //Убираем \n\n... . 
    content = content.replaceAll("\\n?\\s,", ""); // начало с запятой.
    
    return content;
}
}