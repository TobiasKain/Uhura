package at.tuwien.nl2cln;

import at.tuwien.entity.TranslationPattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tobiaskain on 23/04/2017.
 */
public class NL2CNLTranslator {

    private List<TranslationPattern> translationPatterns;

    public NL2CNLTranslator(List<TranslationPattern> translationPatterns) {
        this.translationPatterns = translationPatterns;
    }

    public List<String> translate(String sentence){
        List<String> translations = new ArrayList<>();

        sentence = sentence.replaceAll("\\."," .");

        for (TranslationPattern tp:translationPatterns) {
            if(sentence.toLowerCase().matches(tp.getRegexPattern().toLowerCase()))
            {
                translations = splitSentences(translateSentence(sentence,tp));
                return translations;
            }
        }
        return translations;
    }

    private String translateSentence(String sentence, TranslationPattern translationPattern){

        String translatedSentence = translationPattern.getTranslation();

        String pattern = translationPattern.getNlSentence();
        pattern = pattern.replace(",", " , ");
        pattern = pattern.replace(".", " . ");

        List<String> patternParts = new LinkedList<String>(Arrays.asList(pattern.split("\\s+")));


        sentence = sentence.replace(",", " , ");
        sentence = sentence.replace(".", " . ");

        List<String> sentenceParts = new LinkedList<String>(Arrays.asList(sentence.split("\\s+")));


        while (patternParts.size() > 0)
        {
            if(isVariable(patternParts.get(0))){
                String variable = patternParts.get(0);
                String nextWord = patternParts.get(1).toLowerCase();
                String replaceSequence = "";

                while (!sentenceParts.get(0).toLowerCase().equals(nextWord)){
                    replaceSequence += sentenceParts.get(0) + " ";
                    sentenceParts.remove(0);
                }
                patternParts.remove(0);

                translatedSentence = translatedSentence.replaceAll(variable,replaceSequence.trim());
            }
            else {
                if(patternParts.get(0).toLowerCase().equals(sentenceParts.get(0).toLowerCase())) {
                    patternParts.remove(0);
                    sentenceParts.remove(0);
                }
                else {
                    // TODO EXCEPTION
                }
            }
        }

        return translatedSentence;
    }

    private boolean isVariable(String str) {
        if(str.length() == 1 &&
                str.equals(str.toUpperCase()) && str.matches("[A-Z]")) {
            return true;
        }

        return false;
    }

    private List<String> splitSentences(String sentence)
    {
        sentence = sentence.replaceAll("\n"," ");
        List<String> sentenceList = new LinkedList<String>(Arrays.asList(sentence.split("\\.")));

        if(sentence.lastIndexOf('.') != sentence.length()-1) // check if last character is .
        {
            sentenceList.remove(sentenceList.size()-1);
        }

        for (int i = 0; i < sentenceList.size(); i++) {
            sentenceList.set(i,sentenceList.get(i) + ".");
        }

        return sentenceList;
    }

}
