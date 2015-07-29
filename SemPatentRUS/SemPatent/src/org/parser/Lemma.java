package org.parser;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.lang.ProcessBuilder.Redirect;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Lemma {

    public static int maxlength = 1024; // to fight problems with tokenisation
    
    public static String separator = File.separator;
    public static String pathT= System.getProperty("user.dir").toString() + separator;
    private static final Object lock = new Object();
    public static String cstlemmaexecutable = "cstlemma";
    public static String patternsFileName = "wform2011.ptn1";
    public static String archivelemmaFileName = pathT + "Lemmatiser/msd-ru-lemma.lex.gz";
    public static String lemmaParseFileResult = "result_cstlemma.txt";
    public static String lexFileName = pathT + "Lemmatiser/msd-ru-lemma.lex";
    public static String tempUnarhFilename = "temp_archive.txt";
    private static Map<String, Map<String, String>> lex = null;
    static
    {
    	try {
			lex = readLexicon(lexFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static String lemmaRus(String str, String inumber1) throws IOException {
    	String tokenStr = org.parser.Lemma.lemmaStart(str, inumber1);
    	return tokenStr;
    }
    
    private static boolean isGzip(String str) {
        Pattern p1 = Pattern.compile("(.)*gz$");
        Matcher m1 = p1.matcher(str);
        return m1.matches();
    }

    private static boolean isBzip2(String str) {
        Pattern p1 = Pattern.compile("(.)*gz$");
        Matcher m1 = p1.matcher(str);
        return m1.matches();
    }

    private static Map<String, Map<String, String>> readLexicon(String lexfile) throws IOException {
        //#read the lexicon
        BufferedReader bufferedReader = null;        
        
        bufferedReader = getFileStream(lexfile);
        //if (isGzip(lexfile)) {
        //    bufferedReader = getGzipStream(lexfile);
        //} else if (isBzip2(lexfile)) {
        //    bufferedReader = getBzip2Stream(lexfile);
        //}

        String line = "";
        String oldlex = "";
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();

        int hh = 0;
        while ((line = bufferedReader.readLine()) != null) {
            //next unless /^$goodchars/;
            Pattern pattern = Pattern.compile("^[а-яА-Я]+(.+)$");
            Matcher m1 = pattern.matcher(line);
            if (m1.matches()) {

                //$tabpos=index($_,"\t");
                int tabpos = line.indexOf("\t");

                //unless ($tabpos == -1) {
                if (tabpos != -1) {
                    String w = line.substring(0, tabpos); //substr($_,0,$tabpos);

                  
                    String iterateString = line.substring(tabpos, line.length());
                    //foreach (split /\t/,substr($_,$tabpos)) {
                    String[] array = iterateString.split("\t");

                    for (int i = 0; i < array.length; i++) {
                        String localStr = array[i];

                        pattern = Pattern.compile("^([A-Za-z0-9-]+?) (.+)");
                        m1 = pattern.matcher(localStr);
                        if(w.equalsIgnoreCase("Способ"))
                        {
                        	hh++;
                        }
                        
                        if (m1.matches()) {
                            //if (($posgr,$l)=/^([A-Z0-9-]+?) (.+)/i) {

                            String posgr = m1.group(1);
                            String l = m1.group(2);
                            //if( m1.matches() ){

                            l = l.toLowerCase();

                            //if (exists $lex{w}{posgr}) {


                            if (map.containsKey(w) && map.get(w).containsKey(posgr) && map.get(w).containsValue(posgr)) {
                                //if (exists $lex{w}{posgr}) {

                                oldlex = map.get(w).get(posgr);
                                //$lex{w}{posgr};

                                if (oldlex != l) {
                                    if (l.length() > oldlex.length()) {
                                        map.get(w).put(posgr, l);// =l;
                                    }
                                }
                            } else {
                                if (!map.containsKey(w)) {
                                    Map<String, String> tMap = new HashMap<String, String>();
                                    tMap.put(posgr, l);
                                    map.put(w, tMap);

                                } else if (map.containsKey(w) && !map.get(w).containsKey(posgr)) {
                                    map.get(w).put(posgr, l);
                                } else {
                                    map.get(w).put(posgr, l);// = l;
                                    //$lex{$w}{$posgr}=l;
                                }
                            }
                        }
                    }
                }
            }
        }
        bufferedReader.close();
        return map;
    }

    private static boolean isTrue(String regexp, String str) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher m1 = pattern.matcher(str);
        return m1.matches();
    }
    private static BufferedReader getFileStream(String lexfile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(lexfile));
        return reader;
    }
    private static BufferedReader getBzip2Stream(String lexfile) throws IOException {
        ProcessBuilder builder = new ProcessBuilder("bzip -cd " + lexfile);
        builder.redirectOutput(new File(tempUnarhFilename));
        Process process = builder.start();
        BufferedReader reader = new BufferedReader(new FileReader(tempUnarhFilename));
        return reader;
    }

    private static BufferedReader getGzipStream(String lexfile) throws IOException {
        ProcessBuilder builder = new ProcessBuilder("gzip -cd " + lexfile);
        builder.redirectOutput(new File(tempUnarhFilename));
        Process process = builder.start();
        BufferedReader reader = new BufferedReader(new FileReader(tempUnarhFilename));
        return reader;
    }

    private static ValPos getNextLemPosVal(List<String> lemmaResult) throws IOException {
        //my $fh=shift;
        ValPos value = new ValPos();
        String pos = null;
        String l = null;
        String posline = null;
        String lastValue = "";
       
        while (lemmaResult.size() > 0)
        {
            posline = lemmaResult.get(0);
            lemmaResult.remove(0);
            Pattern pattern = Pattern.compile("^(\\d+)\\s+(.+)");
            if(posline!=null)
            {
	            Matcher m = pattern.matcher(posline);
	
	            if (m.matches()) {
	                lastValue = m.group(1);
	                break;
	            }
            }
        } 

        if (lastValue.length() > 0)
            pos = lastValue;
        else
            pos = "0";

        String lline = "";
        if (lemmaResult.size() > 0 ) 
        {
        	lline = lemmaResult.get(0);
        	lemmaResult.remove(0);
            //#read the next line for the lemma
            Pattern pattern = Pattern.compile("^(.+?)\\t(.+?)\\t(.+?)");
            Matcher m = pattern.matcher(lline);

            if (m.matches()) {
                //$l=$2  if ($lline=~/^(.+?)\t(.+?)\t/);
                l = m.group(2);
            }

        }
        value.lempos = pos;
        value.lemval = l;

        return value;
    }

    public static String lemmaStart(String inputData, String inumber) throws IOException {

        StringBuilder result = new StringBuilder();
        
        //only Russian words to lemmatise automatically by default
        String goodChars = "[а-яА-Я]";
        //utf8::decode($goodchars);

        //only postags starting from goodpos
        String goodPos = "[ANV]";

        //a known pos that can do no harm (cst lemmatises everything and dies on unknown tags)
        String cstFool = "Afp";

        //for storing the corpus
        //my ($corpusfh, $corpusfile) = tempfile(DIR => $Bin."/Temp");

        List<String> corpus = new ArrayList<String>();

        //for storing unknown lemmas
        //my ($lemmafh, $lemmafile) = tempfile(DIR => $Bin."/Temp");
        FileOutputStream lemmaFile = new FileOutputStream("lemma"+inumber);
        //StringBuilder lemmaText = new StringBuilder();
        

        if (patternsFileName == null) {
            //corpusFile.close();
            //corpusFile = new FileOutputStream(System.out);
            //corpusFileHandle = new  FileOutputStream( System.out );
        }

        int i = 1;
        String inputStr = "";

        String[] callData = inputData.split("\n");
        for( int index=0; index<callData.length; index++){
            inputStr = callData[index];

            String word = null;
            String pos = null;
            String lemma = null;

            if (inputStr.length() > Lemma.maxlength) {
                inputStr = inputStr.substring(0, Lemma.maxlength);

                if (Lemma.isTrue("^<", inputStr))
                    inputStr = inputStr + ">";
            }

            //utf8::decode($_);
            Pattern pattern = Pattern.compile("^([а-яА-Я]+?)\\s+(\\S+)\\s+(\\S+)");
            Matcher matcher = pattern.matcher(inputStr);
            //if (($word,$pos)=/^($goodchars+?)\s+(\S+)/) {            
            if (matcher.matches()) {
                word = matcher.group(1);
                pos = matcher.group(2);

                if (lex.containsKey(word) && lex.get(word).containsKey(pos) && lex.get(word).get(pos).length() > 0) {
                    lemma = lex.get(word).get(pos);
                } else if (lex.containsKey(word.toLowerCase()) && lex.get(word.toLowerCase()).containsKey(pos)) {
                    lemma = lex.get(word.toLowerCase()).get(pos);

                } else {

                    if (isTrue("[а-яА-Я]+$", word) && isTrue("^[ANV].*", pos)) {
                        //$word=~/$goodchars$/) and ($pos=~/^$goodpos/)) {
                        String value = i + "/" + cstFool + "\n" + word.toLowerCase() + "/" + pos + "\n";
                        lemmaFile.write(value.getBytes(/*"windows-1251"*/));
                        //lemmaText.append(value);
                        lemma = "<unknown>";
                    } else {
                        //no need to signal for non-inflected forms

                        lemma = word.toLowerCase(); // ."\t<unknown>";

                        //если word из русских букав то оно, иначе -
                        pattern = Pattern.compile("[а-яА-Я]+");
                        Matcher m = pattern.matcher(word);
                        if (m.matches())
                            pos = word;
                        else
                            pos = "-";

                        //pos='-' unless $word=~/$goodchars/;
                    }
                }
                inputStr = word + "\t" + pos + "\t" + lemma;
            } else if (inputStr.length() > 0 && inputStr.substring(0, 1) != "<") { //# a non-Russian word
                //($word,$pos)=split(/\s+/,$_); //#tnt often leaves useless extra tab chars

                String[] temp = inputStr.split("\\s+");
                word = temp[0];
                lemma = word.toLowerCase();
                pos = "";
                if (temp.length >= 2) {
                    pos = temp[1];
                }
                inputStr = word + "\t" + pos + "\t" + lemma;
            }
            corpus.add(inputStr);
            i++;
        }
        lemmaFile.close();
        if (patternsFileName != null) {
            String lempos = null;
            String lemval = null;
            String line = null;

            //System.out.println("Guessing unknown lemmas in " + lemmaFile);
            String curDir = System.getProperty("user.dir");    
            String curDirL =System.getProperty("user.dir")+"/Lemmatiser"; // System.getProperty("user.dir")+"/Lemmatiser";
            
            ProcessBuilder pb = new ProcessBuilder(curDirL + "/" + cstlemmaexecutable, "-eU", "-t", "-f", curDirL + "/" + patternsFileName);
            pb.redirectInput(new File("lemma"+inumber));
            pb.redirectOutput();    			
			Process p = null;
			try {
				p = pb.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		  /*  OutputStream stdin = p.getOutputStream();
		    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
            writer.write(lemmaText.toString() + "\n");		     
		    writer.flush();
		    writer.close();*/
		        
			List<String> lemmaResult = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuilder builder = new StringBuilder();
			try {
				while ( (line = br.readLine()) != null) {
					lemmaResult.add(line);  
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            i = 1;
            ValPos val = getNextLemPosVal(lemmaResult);
            for(int ii = 0;ii<corpus.size();ii++)
            {
            	line = corpus.get(ii);
                String check_i = "";
                check_i = "" + i;
                if (val.lempos.equalsIgnoreCase(check_i)) {
                    //$l=lemval;
                    Pattern pattern = Pattern.compile("\t<unknown>");
                    //s/\t<unknown>/\t$l\t<guessed>/;
                    line = pattern.matcher(line).replaceAll("\t" + val.lemval + "\t<guessed>");
                    val = getNextLemPosVal(lemmaResult);
                    result.append(line);
                    result.append("\n");
                }
                else
                {
                    result.append(line);
                    result.append("\n");
                }
                Pattern pattern = Pattern.compile("<unknown>");
                Matcher matcher = pattern.matcher(inputStr);
                if (matcher.matches()) {
                    System.err.println(i + " " + val.lempos + "; " + val.lemval + "; " + line);
                }
                i++;
            } 
            File file1 = new File("lemma"+inumber);
            file1.delete();

        }
        String str = result.toString();
        return str;
    }
}
