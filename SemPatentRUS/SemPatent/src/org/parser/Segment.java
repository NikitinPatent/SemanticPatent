package org.parser;

public class Segment {
	
public static String segmentRus(String content){
    	
    	String[] sentencesDel = {
         		", � ", ", �� ", " ��� "        
        };
    	 
    	String[] sentencesPr = {
                 "������������ ���, ���", "������������� ���, ���",
                 "������������� ���, ���", "������������ ���, ���", 
                 "������������ ���, ���", "������������ ���, ���",
                 "������������ ���, ���", 
                 "������������ ���, ���", "������������ ���, ���",
                 "������������ ���, ���", "������������� ���, ���"        
        };
    	 
        String[] sentences = {
        		" ������ ", " ��� ���� ", " ����� ", " ���, ��� ", " ����� ���� ",
                " ����� ", " ����� ",
                " ����������������� ", " ����������������� ", " ����������������� ",
                " ����������������� ", " ����������������� ", " ������������������ ",
                " ������������������ ", " ����������������� ", " ����������������� ",
                " ������������������ ",
                " �������������� ", " �������������� ", " �������������� ",
                " ��������������", " �������������� ", " ��������������� ",
                " ��������������� ", " �������������� ", " �������������� ",
                " ��������������� ",
                " ���������� ", " ���������� ", " ���������� ",
                " ���������� ", " ���������� ", " ����������� ",
                " ����������� ", " ���������� ", " ���������� ",
                " ����������� "
        };
        
        String[] sentencesIm = {
        "���", "���", "���", "���", "����", "����",
        "�������", "�������", "�������",
        "�������", "�������", "��������",
        "��������", "�������", "�������", "�������"
        };
        
        content = segment(content, sentences, sentencesPr, sentencesDel, sentencesIm);

        return content;
    }

    /** segment - ���������� ������ ����, �������� ���� � ��������� ������.
     *
     * ������� ���������:
     * String content - �������� ������;
     * String[] sentences - ������ ���� ������;
     * String[] sentencesDel - ������ ��������� ����.
     *
     * �������� ������:
     * String - ��������������� ��������.
     */
    private static String segment(String content, String[] sentences, String[] sentencesPr, String[] sentencesDel, String[] sentencesIm){
    	
    	content = content.replace(" �� �.", " �� ������ ");
    	
    	for (String s : sentencesDel){
            content = content.replace(s, ".\n");
        }
    	
    	for (String s : sentencesPr){
            content = content.replace(s, ".\n" + s + ".\n");
        }
        for (String s : sentences){
            content = content.replace(s, ".\n " + s);
        }
        
        String[] sentencesIm1 = {"�", "��", "���", "�", "�", ""};
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

        content = content.replaceAll("\\b[�-��-�]\\)\\.?", ""); //������� �) �).
        content = content.replaceAll("\\b[a-zA-Z]\\)\\.?", ""); //������� b) B).
        content = content.replaceAll("\\b[0-9]{1,3}\\)?\\.", ""); //������� 10. 1).
        
        content = content.replaceAll("\\,[,\\s?.]{2,}+",".\n"); //������� ,  ... .
        content = content.replaceAll("\\.[.\\s?]{1,}+", ".\n"); //������� ... .
        content = content.replaceAll("\\n[\\\\n\\.?\\s?]{1,}+", "\n"); //������� \n\n... . 
        content = content.replaceAll("\\n?\\s,", ""); // ������ � �������.
        
        return content;
    }
}
