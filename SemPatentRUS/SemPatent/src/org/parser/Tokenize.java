package org.parser;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenize {

    //characters which have to be cut off at the beginning of a word
    public String PChar = "\\[¿¡{(\\`\"‚„†‡‹‘’“”•–—›'.'";

    // characters which have to be cut off at the end of a word
    public String FChar = "]}\'" + "`\")" + ",;:!?%‚„…†‡‰‹‘’“”•–—›";

    //character sequences which have to be cut off at the beginning of a word
    public String PClitic = "";

    //character sequences which have to be cut off at the end of a word
    public String FClitic = "";

    public static String S1 = "";
    public static String suffix = "";

    
    public static String tokenRus(String str) {
    	
    	String tokenStr = org.parser.Tokenize.tokenize(str);
    	return tokenStr;

    }

    public static void main(String[] args) throws IOException {
        Tokenize token = new Tokenize();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String line = "";
        while ((line = reader.readLine()) != null) {
            token.convert(line);
            System.out.println(S1);
            System.out.println(suffix);
        }
        reader.close();

    }

    private boolean isTrue(String s, String str) {
        Pattern pattern = Pattern.compile(s);
        Matcher m1 = pattern.matcher(str);
        return m1.matches();
    }
    public static String tokenize(String str)
    {
        S1 = "";
        suffix = "";
        Tokenize token = new Tokenize();
        token.convert(str);
        return S1;
    }
    private void convert(String str) {
        boolean check = false;
        int first_line = -1;
        Matcher matcher = null;
        Pattern pattern = Pattern.compile("^\uFEFF");
        str = pattern.matcher(str).replaceFirst("");

        //replace newlines and tab characters with blanks
        str = str.replace('\n', ' ');
        str = str.replace('\t', ' ');

        //replace blanks within SGML tags
        /*while (s/(<[^<> ]*) ([^<>]*>)/$1\377$2/g) */
        pattern = Pattern.compile("(<[^<> ]*) ([^<>]*>)");
        matcher = pattern.matcher(str);
        str = matcher.replaceAll("$1\u0377$2");

        //tr/ /\376/;
        str = str.replaceAll(" ", "\u0376");

        //restore SGML tags
        //tr/\377\376/ \377/;
        str = str.replaceAll("\u0377\u0376", " \u0377");

        //prepare SGML-Tags for tokenization
        //s/(<[^<>]*>)/\377$1\377/g;
        pattern = Pattern.compile("(<[^<>]*>)");
        matcher = pattern.matcher(str);
        check = matcher.matches();
        str = matcher.replaceAll("\u0377$1\u0377");

        //s/^\377//;
        pattern = Pattern.compile("^\u0377");
        str = pattern.matcher(str).replaceFirst("");

        //s/\377$//;
        pattern = Pattern.compile("\u0377$");
        str = pattern.matcher(str).replaceFirst("");

        //s/\377\377\377*/\377/g;
        pattern = Pattern.compile("\u0377\u0377\u0377*");
        str = pattern.matcher(str).replaceAll("\u0377");

        String[] S = str.split("\u0376");

        for (int i = 0; i < S.length; i++) {
            String local = S[i];

            if (isTrue("^<.*>$", local)) {
                //SGML tag
                //$S1 = $S1 . $_,"\n";
                S1 = S1 + local + "\n";
            } else {
                //add a blank at the beginning and the end of each segment
                //$_ = ' '.$_.' ';
                local = " " + local + " ";

                //insert missing blanks after punctuation
                //s/(\.\.\.)/ ... /g;
                pattern = Pattern.compile("(\\.\\.\\.)");
                local = pattern.matcher(local).replaceAll(" ... ");

                //s/([;\!\?])([^ ])/$1 $2/g;
                pattern = Pattern.compile("([;\\!\\?])([^ ])");
                local = pattern.matcher(local).replaceAll("$1 $2");

                //s/([.,:])([^ 0-9.])/$1 $2/g;
                pattern = Pattern.compile("([.,:])([^ 0-9.])");
                local = pattern.matcher(local).replaceAll("$1 $2");

                String[] F = local.split(" ");
                for (int j = 0; j < F.length; j++) {
                    if (F[j].length() > 0) {
                        firstStage(F[j]);
                    }
                }
            }
        }
    }

    private void firstStage(String str) {
        Pattern pattern = null;
        Matcher matcher = null;

        int finished = 1;
        int tt =0;
        suffix = "";
        do {
        	tt++;
        	if(tt == 10000)
        	{
        		tt++;
        	}
            finished = 1; //for loop cycle

//	    cut off preceding punctuation
//	  if (s/^([$PChar])(.)/$2/) {
//	    $S1 = $S1 . $1,"\n";
//	    $finished = 0;
//	  }
            pattern = Pattern.compile("^([" + PChar + "])(.+)");
            matcher = pattern.matcher(str);
            if (matcher.matches()) {
                S1 = S1 + matcher.group(1) + "\n";
                str = matcher.replaceAll("$2");
                finished = 0;
            }

            //cut off trailing punctuation
//	  if (s/(.)([$FChar])$/$1/) {
//	    $suffix = "$2\n$suffix";
//	    $finished = 0;
//	  }
            pattern = Pattern.compile("(.+)([" + FChar + "])$");
            matcher = pattern.matcher(str);
            if (matcher.matches()) {
                suffix = matcher.group(2) + "\n" + suffix;
                str = matcher.replaceAll("$1");
                finished = 0;
            }

            //cut off trailing periods if punctuation precedes
            //if (s/([$FChar])\.$//) {
            pattern = Pattern.compile("([" + FChar + "])\\.$");
            matcher = pattern.matcher(str);
            if (matcher.matches()) {
            	String grp = matcher.group(1);
            	S1 = S1 + grp + "\n";
            	str = matcher.replaceAll("");
                suffix = ".\n" + suffix;
                if (str.equalsIgnoreCase("")) {
                    str = grp;
                } else {
                    suffix = grp + "\n" + suffix;
                }

                finished = 0;
            }
        } while (!(finished == 1));


        //handle explicitly listed tokensopen
        //$Token alwayas undef
//	if (defined($Token{$_})) {
//	  S1 = S1 +  "$_\n$suffix";
//	  next;
//	}

        //abbreviations of the form A. or U.S.A.
        pattern = Pattern.compile("^([A-Za-z-]\\.)+$");
        matcher = pattern.matcher(str);
        if (matcher.matches()) {
            S1 = S1 + str + "\n" + suffix;
            return;
        }

        //disambiguate periods
        if (isTrue("^(..*)\\.$", str) && str != "...") {
            //if (/^(..*)\.$/ && $_ ne "..." && )
            //always true = !($opt_g && /^[0-9]+\.$/)) {
            pattern = Pattern.compile("^(..*)\\.$");
            matcher = pattern.matcher(str);
            if (matcher.matches()) {
                str = matcher.group(1);
                suffix = ".\n" + suffix;
            }
            /*if (defined($Token{$_})) {
            $S1 = $S1 . "$_\n$suffix";
	        next;
	        } */
        }

        //cut off clitics
        while (isTrue("^(--)(.)", str)) {
            pattern = Pattern.compile("^(--)(.)");
            matcher = pattern.matcher(str);
            if (matcher.matches()) {
	            S1 = S1 + matcher.group(1) + "\n";
	            str = matcher.replaceAll("$2");
            }
        }

        if (PClitic != "") {
            while (isTrue("^(" + PClitic + ")(.)", str)) 
            {
                pattern = Pattern.compile("^(\"+PClitic+\")(.)");
                matcher = pattern.matcher(str);
                if (matcher.matches()) 
                {
	                S1 = S1 + matcher.group(1) + "\n";
	                str = matcher.replaceAll("$2");
                }
            }
        }

        while (isTrue("(.)(--)$", str)) {
            pattern = Pattern.compile("(.)(--)$");
            matcher = pattern.matcher(str);
            if (matcher.matches()) 
            {
	            suffix = matcher.group(2) + "\n" + suffix;
	            str = matcher.replaceAll("$2");
            }
        }

        if (FClitic != "") {
            while (isTrue("(.)(" + FClitic + ")$", str)) {
                pattern = Pattern.compile("(.)(" + FClitic + ")$");
                matcher = pattern.matcher(str);
                if (matcher.matches()) 
                {
	                suffix = matcher.group(2) + "\n" + suffix;
	                str = matcher.replaceAll("$2");
                }
            }
        }

        S1 = S1 + str + "\n" + suffix;
    }
}