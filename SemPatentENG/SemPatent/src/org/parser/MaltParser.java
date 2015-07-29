package org.parser;
	
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.maltparser.MaltParserService;
import org.maltparser.core.exception.MaltChainedException;

public class MaltParser {
	
	private static String symbolTab = "\t";
	private static String magSymbol = "\n";
	public static String maltParserEng(String modelName, String content){
        String out = "";
        String[] sentences = content.split(magSymbol + magSymbol);// "\n"
        ArrayList<String[]> alltokens = new ArrayList<String[]>();
        for(int i = 0; i< sentences.length;i++)
        {
            String[] tokens = sentences[i].split(magSymbol);// "\n"
            alltokens.add(tokens);
        }
        List<String> result = maltParser(modelName, alltokens);
        for(String tmp : result)
        {
            if(!tmp.equalsIgnoreCase(magSymbol))
            {
                out += tmp + magSymbol;
            }
            else
            {
                out += tmp;
            }
        }
        return out;
    }

    public static Map<String, MaltParserService> MaltParserServices = new HashMap<String, MaltParserService>();

    private static List<String> maltParser(String modelName, ArrayList<String[]> tokens){
        List<String> result = new ArrayList<String>();
        try {
            if(!MaltParserServices.containsKey(modelName))
            {
                MaltParserServices.put(modelName, new MaltParserService());
                MaltParserServices.get(modelName).initializeParserModel("-c " + modelName + " -m parse -w . -lfi parser.log");
            }
            MaltParserService service = MaltParserServices.get(modelName);
// Inititalize the parser model 'model' and sets the working directory to '.' and sets the logging file to 'parser.log'

            for(int j = 0;j<tokens.size(); j++)
            {
                String[] outputTokens = service.parseTokens(tokens.get(j));
// Outputs the with the head index and dependency type information
                int i = 1;
                for (String outputToken : outputTokens) {
                    String[] mass = outputToken.split("\t");
                    String ns = String.valueOf(i) + "\t" + mass[1] + "\t" + mass[2] + "\t" + mass[3] + "\t" + mass[4] + "\t" + mass[5] + "\t" + mass[6] + "\t" + mass[7] + "\t_\t_";
                    i++;
                    if(outputToken.contains("SENT")){
                        i=1;
                    }
                    result.add(ns);
                }
                if(j != tokens.size() - 1)
                {
                    result.add(magSymbol);
                }
            }
// Terminates the parser model
//service.terminateParserModel();
        } catch (MaltChainedException e) {
            System.err.println("MaltParser exception: " + e.getMessage());
        }
        return result;
    	}
    }