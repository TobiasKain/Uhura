package at.tuwien.CNL2ASP;

import edu.stanford.nlp.ling.TaggedWord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tobiaskain on 14/04/2017.
 */
public class TranslatorHelper {

    public void removeFirstWord(ArrayList<TaggedWord> taggedWords){
        taggedWords.remove(0);
    }

    public String getCNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String cNoun = "";

        while (taggedWords.get(0).tag().matches("(NN|NNS|NNP)"))
        {
            if(isVariable(taggedWords.get(0).value()) && !cNoun.equals(""))
            {
                return cNoun;
            }

            cNoun += taggedWords.get(0).value();
            if(!isVariable(cNoun)){
                cNoun = cNoun.toLowerCase();
            }

            removeFirstWord(taggedWords);
        }

        if(cNoun.equals(""))
        {
            /* Workaround for the following problem:
             * Sometimes if a cnoun is followed by a variable,
             * then the parser accidentally recognises the cnoun as pnoun.

            TODO REMOVE: as implimentation of CNOUN AND PNOUN IS THE SAME

            if(isVariable(taggedWords.get(1).value()))
            {
                try{
                    cNoun = getPNoun(taggedWords);
                }catch (SentenceValidationException e){}
            } */

            if(cNoun.equals("")) {
                throw new SentenceValidationException(String.format("\"%s\" is not a common noun.", taggedWords.get(0).value()));
            }
        }

        return cNoun;
    }

    public boolean isVariable(String str) {
        if(str.length() == 1 &&
                str.equals(str.toUpperCase()) && str.matches("[A-Z]")) {
            return true;
        }

        return false;
    }

    public String getVerb(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String verb = "";
        if(taggedWords.get(0).tag().matches("(VB|VBD|VBG|VBN|VBP|VBZ)"))
        {
            verb = StanfordParser.getInstance().getBaseFormOfWord(taggedWords.get(0).value());
            removeFirstWord(taggedWords);
        }
        if(taggedWords.get(0).tag().matches("(IN)"))
        {
            verb = String.format("%s_%s",verb, taggedWords.get(0).value());
            removeFirstWord(taggedWords);
        }
        if(verb == ""){
            throw new SentenceValidationException(String.format("\"%s\" is not a verb.", taggedWords.get(0).value()));
        }

        return verb;
    }

    public void removeWord(List<TaggedWord> taggedWords, String word) throws SentenceValidationException {

        if(taggedWords.get(0).value().toLowerCase().equals(word.toLowerCase()))
        {
            taggedWords.remove(0);
        }
        else {
            throw new SentenceValidationException(String.format("Expected \"%s\" instead of \"%s\".",word,taggedWords.get(0).value()));
        }
    }

    public String getPNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        String pNoun= "";

        while (taggedWords.get(0).tag().matches("(NN|NNS|NNP)"))
        {
            if(isVariable(taggedWords.get(0).value()) && !pNoun.equals(""))
            {
                return pNoun;
            }

            pNoun += taggedWords.get(0).value();
            if(!isVariable(pNoun)){
                pNoun = pNoun.toLowerCase();
            }

            taggedWords.remove(0);
        }

        if(pNoun.equals(""))
        {
            throw new SentenceValidationException(String.format("\"%s\" is not a proper noun.", taggedWords.get(0).value()));
        }

        return pNoun;
    }

    public String getAdjectiveAndOrPreposition(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        String adjective = "";

        try {
            adjective = getAdjective(taggedWords);
        }catch (SentenceValidationException e){}

        if(taggedWords.get(0).tag().matches("(IN|TO)"))
        {
            if(adjective.equals("")) {
                adjective = String.format("%s", taggedWords.get(0).value());
            }else {
                adjective = String.format("%s_%s", adjective, taggedWords.get(0).value());
            }

            removeFirstWord(taggedWords);
        }

        if(adjective.equals(""))
        {
            throw new SentenceValidationException(String.format("\"%s\" is not a adjective.", taggedWords.get(0).value()));
        }

        return adjective;
    }

    public String getAdjective(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String adjective = "";

        while (taggedWords.get(0).tag().matches("(JJ|VBN)"))
        {
            adjective += taggedWords.get(0).value().toLowerCase();
            taggedWords.remove(0);
        }

        if(adjective.equals(""))
        {
            throw new SentenceValidationException(String.format("\"%s\" is not a adjective.", taggedWords.get(0).value()));
        }

        return adjective;
    }

    public String getPreposition(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        String preposition = "";

        if(taggedWords.get(0).tag().matches("(IN|TO)"))
        {
            preposition = taggedWords.get(0).value();
            removeFirstWord(taggedWords);
        }

        if(preposition.equals(""))
        {
            throw new SentenceValidationException(String.format("\"%s\" is not a preposition.", taggedWords.get(0).value()));
        }

        return preposition;
    }

    public String getVariable(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String variable = "";

        if(isVariable(taggedWords.get(0).value())){
            variable = taggedWords.get(0).value();
            removeFirstWord(taggedWords);
            return variable;
        }

        throw new SentenceValidationException(String.format("\"%s\" is not a variable.", taggedWords.get(0).value()));
    }

    public String getNumber(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String number = "";

        try{
            number += Integer.parseInt(taggedWords.get(0).value());
        }catch (NumberFormatException e) { }

        if(number.equals(""))
        {
            switch (taggedWords.get(0).value()){
                case "zero":
                    number = "0";
                    break;
                case "one":
                    number = "1";
                    break;
                case "two":
                    number = "2";
                    break;
                case "three":
                    number = "3";
                    break;
                case "four":
                    number = "4";
                    break;
                case "five":
                    number = "5";
                    break;
                case "six":
                    number = "6";
                    break;
                case "seven":
                    number = "7";
                    break;
                case "eight":
                    number = "8";
                    break;
                case "nine":
                    number = "9";
                    break;
                case "ten":
                    number = "10";
                    break;
            }
        }

        if(number.equals("")){
            throw new SentenceValidationException(String.format("\"%s\" is not a number.", taggedWords.get(0).value()));
        }

        removeFirstWord(taggedWords);

        return number;
    }

    public boolean isNegation(ArrayList<TaggedWord> taggedWords) {
        if(taggedWords.get(0).value().matches("(not|n't)")){
            removeFirstWord(taggedWords);
            return true;
        }
        if(taggedWords.get(0).value().matches("(does|do|is|can)") &&
                taggedWords.get(1).value().matches("(not|n't)")){
            removeFirstWord(taggedWords);
            removeFirstWord(taggedWords);

            return true;
        }

        return false;
    }

    public String getSentence(ArrayList<TaggedWord> taggedWords) {
        String sentence = "";

        for (TaggedWord tw: taggedWords) {
            sentence += tw.value() + " ";
        }

        return sentence.trim();
    }

    public int getVariableCount(ArrayList<TaggedWord> taggedWords, String variable){
        int count = 0;

        if(taggedWords == null){
            return count;
        }

        for (TaggedWord tw: taggedWords) {
            if(tw.value().equals(variable))
            {
                count ++;
            }
        }

        return count;
    }

    /* Workaround for the following problem:
     * if a Variable is followed by a dot then
     * the parser tags the variable and the dot
     * together as NNP.
     * e.g "X." --> X./NNP
     */
    public String addWhitespacesBeforeDot(String sentence) {
        if(sentence.matches(".*\\.$"))
        {
            sentence = sentence.substring(0,sentence.lastIndexOf('.'));
            sentence = sentence + " .";
        }

        return sentence;
    }
}
