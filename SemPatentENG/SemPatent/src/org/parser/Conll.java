package org.parser;

import static java.util.Arrays.asList;
import java.util.ArrayList;
import java.util.List;

public class Conll {

	private static String symbolTab = "\t";
	private static String magSymbol = "\n";
	
	public static String conllEng(String content){
    String[] a = content.split(magSymbol);
    List<String> listConll = convertToConllEN(asList(a));
    String  result = "";
    for(String tmp : listConll)
    {
        if(!tmp.equalsIgnoreCase(magSymbol))
        {
            result += tmp + magSymbol;
        }
        else
        {
            result += tmp;
        }
    }
    return result;
	}

/** ‘ункци€ преобразовани€ из текстового списка в список conll дл€ јнглийского текста
 * ¬ходные параметры:
 * List<String> listTagger - список преобразуемых строк, где строка это три колонки слов разделенных через табул€цию
 *
 * ¬ыходные данные:
 * List<String> listConll - список преобразованных строк дл€ строк содержащих јнглийский текст
 */
	private static List<String> convertToConllEN(List<String> listTagger){
    List<String> listConll = new ArrayList<String>();
    for (String line : listTagger) {
        String words[] = line.split(symbolTab);
        if(words.length >= 3){
            String conll = "";

            conll += "1"; //1 колонка -"1"
            conll += symbolTab;

            conll += words[0];//2 - 1 из lemmatizer
            conll += symbolTab;

            conll += words[2];//3- 3 из lemmatizer
            conll += symbolTab;

            conll += words[1];//4 - 2 из lemmatizer
            conll += symbolTab;

            conll += words[1];//5 - 2 из lemmatizer
            conll += symbolTab;

            conll += "_";//6- "_"

            listConll.add(conll);
            if(words[1].equalsIgnoreCase("SENT"))
            {
                listConll.add(magSymbol);
            }
        }
    }
    return listConll;
	}
}