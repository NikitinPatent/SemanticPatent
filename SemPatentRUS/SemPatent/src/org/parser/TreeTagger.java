package org.parser;

import org.annolab.tt4j.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class TreeTagger {
	
	private static String symbolTab = "\t";
	private static String magSymbol = "\n";
	
	public static String treeTaggerRus(String pathToTreeTagger, String pathToModelPar, String content){
    	String[] words = content.split(magSymbol);

    	String result="";
        List<String> listTreeTagger = treeTagger(asList(words), pathToModelPar, pathToTreeTagger);//asList(words)
        for(String tmp : listTreeTagger){
            result += tmp + magSymbol;
        }
        return result;
    }
	
	/**
     * смотрите документацию по Треетаггер здесь:
     * https://code.google.com/p/tt4j/wiki/Usage
     *
     * Некоторые решения проблем здесь:
     * http://www.developpez.net/forums/d881817-2/java/general-java/apis/integrer-treetagger-java/
     */
    private static List<String> treeTagger(List<String> content, String pathToModelPar, final String pathToTreeTagger){
        final List<String> result = new ArrayList<String>();
        TreeTaggerWrapper tt = new TreeTaggerWrapper<String>();
        try {
            tt.setModel(pathToModelPar);
            tt.setArguments(new String[] {"-lemma","-token","-sgml"});
            tt.setExecutableProvider(new ExecutableResolver(){
                @Override
                public void setPlatformDetector(PlatformDetector arg0) {}

                @Override
                public String getExecutable() throws IOException
                {
                    return pathToTreeTagger;
                }
                @Override
                public void destroy() {}
            });

            tt.setHandler(new TokenHandler<String>() {
                public void token(String token, String pos, String lemma) {
                    result.add(token + "\t" + pos + "\t" + lemma);
                }
            });
            tt.process(content);
        } catch (TreeTaggerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        tt.destroy();
        return result;
    }

}
