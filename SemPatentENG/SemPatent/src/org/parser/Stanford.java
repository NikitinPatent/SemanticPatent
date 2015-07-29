package org.parser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Stanford {
	private static MaxentTagger tagger = null;
    public static String StanfordTagger(String content){
        String model = "english-left3words-distsim.tagger";
        String result = "";
        if(tagger == null){
            tagger = new MaxentTagger(model);
        }
        List<List<HasWord>> sentences = null;
        sentences = MaxentTagger.tokenizeText(new StringReader(content));//new BufferedReader(new FileReader(fileName)));

        for (List<HasWord> sentence : sentences) {
            ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);
            for(TaggedWord taggedWord : tSentence){
                String tag = taggedWord.tag();
                if(tag.equals(".") || tag.equals("!") || tag.equals("?")){
                    tag = "SENT";
                }
                result += taggedWord.word() + "\t" + tag + "\t" + Morphology.stemStaticSynchronized(taggedWord.word(), taggedWord.tag()).word() + "\n";
            }
        }
        return result;
    }
   }