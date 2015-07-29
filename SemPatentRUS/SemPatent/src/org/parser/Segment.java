package org.parser;

public class Segment {
	
public static String segmentRus(String content){
    	
    	String[] sentencesDel = {
         		", а ", ", но ", " где "        
        };
    	 
    	String[] sentencesPr = {
                 "отличающийс€ тем, что", "отличающегос€ тем, что",
                 "отличающемус€ тем, что", "отличающемс€ тем, что", 
                 "отличающа€с€ тем, что", "отличающейс€ тем, что",
                 "отличающуюс€ тем, что", 
                 "отличающиес€ тем, что", "отличающихс€ тем, что",
                 "отличающимс€ тем, что", "отличающимис€ тем, что"        
        };
    	 
        String[] sentences = {
        		" причем ", " при этом ", " путем ", " так, что ", " кроме того ",
                " перед ", " после ",
                " предусматривающий ", " предусматривающа€ ", " предусматривающие ",
                " предусматривающим ", " предусматривающей ", " предусматривающими ",
                " предусматривающего ", " предусматривающую ", " предусматривающих ",
                " предусматривающему ",
                " обеспечивающий ", " обеспечивающа€ ", " обеспечивающие ",
                " обеспечивающим", " обеспечивающей ", " обеспечивающими ",
                " обеспечивающего ", " обеспечивающую ", " обеспечивающих ",
                " обеспечивающему ",
                " включающий ", " включающа€ ", " включающие ",
                " включающим ", " включающей ", " включающими ",
                " включающего ", " включающую ", " включающих ",
                " включающему "
        };
        
        String[] sentencesIm = {
        "чей", "чь€", "чье", "чьи", "чьей", "чьих",
        "который", "котора€", "которые",
        "которым", "которой", "которыми",
        "которого", "которую", "которых", "котором"
        };
        
        content = segment(content, sentences, sentencesPr, sentencesDel, sentencesIm);

        return content;
    }

    /** segment - производит замену слов, удаление слов и разбивает строки.
     *
     * ¬ходные параметры:
     * String content - документ записи;
     * String[] sentences - массив слов замены;
     * String[] sentencesDel - массив удал€емых слов.
     *
     * ¬ыходные данные:
     * String - преобразованный документ.
     */
    private static String segment(String content, String[] sentences, String[] sentencesPr, String[] sentencesDel, String[] sentencesIm){
    	
    	content = content.replace(" по п.", " по пункту ");
    	
    	for (String s : sentencesDel){
            content = content.replace(s, ".\n");
        }
    	
    	for (String s : sentencesPr){
            content = content.replace(s, ".\n" + s + ".\n");
        }
        for (String s : sentences){
            content = content.replace(s, ".\n " + s);
        }
        
        String[] sentencesIm1 = {"в", "на", "при", "о", "с", ""};
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

        content = content.replaceAll("\\b[а-€ј-я]\\)\\.?", ""); //”бираем а) ј).
        content = content.replaceAll("\\b[a-zA-Z]\\)\\.?", ""); //”бираем b) B).
        content = content.replaceAll("\\b[0-9]{1,3}\\)?\\.", ""); //”бираем 10. 1).
        
        content = content.replaceAll("\\,[,\\s?.]{2,}+",".\n"); //”бираем ,  ... .
        content = content.replaceAll("\\.[.\\s?]{1,}+", ".\n"); //”бираем ... .
        content = content.replaceAll("\\n[\\\\n\\.?\\s?]{1,}+", "\n"); //”бираем \n\n... . 
        content = content.replaceAll("\\n?\\s,", ""); // начало с зап€той.
        
        return content;
    }
}
