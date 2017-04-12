package at.tuwien.CNL2ASP;

import at.tuwien.ASP.AspRule;
import at.tuwien.ASP.Literal;
import edu.stanford.nlp.ling.TaggedWord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tobiaskain on 26/03/2017.
 */
public class CnlToAspTranslator {

    private List<String> inputStrings = new ArrayList<>();

    public CnlToAspTranslator(List<String> inputStrings) {
        this.inputStrings = inputStrings;
    }

    public List<AspRule> translate()
    {
        List<AspRule> aspRules = new ArrayList<>();

        for (String sentence: inputStrings) {
            try {
                aspRules.add(translateSentence(sentence));
            } catch (SentenceValidationException e) {
                e.printStackTrace();
            }
        }

        return aspRules;
    }

    private AspRule translateSentence(String sentence) throws SentenceValidationException {

        AspRule aspRule = null;

        sentence = addWhitespanceBeforDot(sentence);
        ArrayList<TaggedWord> taggedWords = StanfordParser.getInstance().parse(sentence).taggedYield();

        sentence = sentence.toLowerCase();

        if(sentence.matches("a .* is a .* of a .*\\.$" ))
        {
            aspRule = aCNounVariableIsACNounOfACNounVariable(taggedWords);
        }
        else if (sentence.matches("if .* then .*\\.$")){
            aspRule = ifThen(taggedWords);
        }
        else if (sentence.matches("exclude that .*\\.$")){
            aspRule = excludeThat(taggedWords);
        }
        else if(sentence.matches("the .* is .*\\.$")) {
            aspRule = thePNounIsAdjective(taggedWords);
        }
        else if(sentence.matches(".* [a-z] is a .*\\.$")){
            aspRule = cNounVariableIsACNoun(taggedWords);
        }
        else if(sentence.matches("there is a .* [a-z] \\.$"))
        {
            aspRule = thereIsACNounVariable(taggedWords);
        }
        else if(sentence.matches(".* is a .*\\.$")) {
            aspRule = pNounIsACNoun(taggedWords);
        }
        else if(sentence.matches(".* [a-z] is .*\\.$")){
            aspRule = cNounVariableIsAdjective(taggedWords);
        }
        else if(sentence.matches(".* is .*\\.$")){
            aspRule = pNounIsAdjective(taggedWords);
        }
        else if(sentence.matches(".* [a-z] .* a .* as .*\\.$")){
            aspRule = aCNounVariableVerbACNounAsPNoun(taggedWords);
        }
        else if(sentence.matches(".* a .* as .*\\.$")){
            aspRule = aPNounVerbACNounAsPNoun(taggedWords);
        }
        else if (sentence.matches(".* (more|less) than .*\\.$")){
            aspRule = cNounVariableVerbMoreThanNumberCNounVariable(taggedWords);
        }

        return aspRule;
    }



    private AspRule excludeThat(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        AspRule aspRule = new AspRule();

        removeFirstWord(taggedWords);
        removeFirstWord(taggedWords);

        while (!taggedWords.get(0).value().equals(".")){
            String sentence = "";

            if(taggedWords.get(0).value().equals("and") && taggedWords.get(1).value().equals("that"))
            {
                removeFirstWord(taggedWords);
                removeFirstWord(taggedWords);
            }

            while (!taggedWords.get(0).value().equals(".") && (!taggedWords.get(0).value().equals("and") && !taggedWords.get(1).value().equals("that"))){
                sentence += taggedWords.get(0).value() + " ";
                taggedWords.remove(0);
            }
            sentence = sentence.trim() + ".";
            aspRule.getBody().addAll(translateSentence(sentence).getHead());
        }

        return aspRule;
    }

    private AspRule ifThen(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        AspRule aspRule = new AspRule();

        removeFirstWord(taggedWords);

        /* Body */
        while (!taggedWords.get(0).value().equals("then")){
            String sentence = "";
            if(taggedWords.get(0).value().equals("and"))
            {
                removeFirstWord(taggedWords);
            }
            while (!taggedWords.get(0).value().equals("and") && !taggedWords.get(0).value().equals("then")){
                sentence += taggedWords.get(0).value() + " ";
                taggedWords.remove(0);
            }
            sentence = sentence.trim() + ".";
            aspRule.getBody().addAll(translateSentence(sentence).getHead());
        }

        taggedWords.remove(0);

        /* Head */
        String sentence = "";
        while (!taggedWords.get(0).value().equals(".")){
            sentence += taggedWords.get(0).value() + " ";
            taggedWords.remove(0);
        }
        sentence = sentence.trim() + ".";
        aspRule.getHead().add(translateSentence(sentence).getHead().get(0));

        return aspRule;
    }



    private AspRule aCNounVariableIsACNounOfACNounVariable(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        removeFirstWord(taggedWords);
        String cNoun1 = getCNoun(taggedWords);
        String varialbe1 = getVariable(taggedWords);
        removeWord(taggedWords,"is");
        removeWord(taggedWords,"a");
        String cNoun2 = getCNoun(taggedWords);
        removeWord(taggedWords,"of");
        removeWord(taggedWords,"a");
        String cNoun3 = getCNoun(taggedWords);
        String varialbe2 = getVariable(taggedWords);

        removeWord(taggedWords,".");

        Literal literal1 = new Literal(cNoun2);
        literal1.getTerms().add(varialbe1);
        literal1.getTerms().add(varialbe2);

        Literal literal2 = new Literal(cNoun1);
        literal2.getTerms().add(varialbe1);

        Literal literal3 = new Literal(cNoun3);
        literal3.getTerms().add(varialbe2);

        AspRule aspRule = new AspRule();

        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);
        aspRule.getHead().add(literal3);

        return aspRule;
    }

    private AspRule aCNounVariableVerbACNounAsPNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        if(taggedWords.get(0).value().equals("a")) {
            removeFirstWord(taggedWords);
        }

        String cNoun1 = getCNoun(taggedWords);

        String variable = getVariable(taggedWords);

        String verb = getVerb(taggedWords);

        removeWord(taggedWords,"a");

        String cNoun2 = getCNoun(taggedWords);

        removeWord(taggedWords,"as");

        String pNoun = getPNoun(taggedWords);

        removeWord(taggedWords,".");

        Literal literal1 = new Literal(verb);
        literal1.getTerms().add(variable);
        literal1.getTerms().add(pNoun);

        Literal literal2 = new Literal(cNoun1);
        literal2.getTerms().add(variable);

        Literal literal3 = new Literal(cNoun2);
        literal3.getTerms().add(pNoun);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);
        aspRule.getHead().add(literal3);

        return aspRule;
    }

    private AspRule aPNounVerbACNounAsPNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        if(taggedWords.get(0).value().equals("a")) {
            removeFirstWord(taggedWords);
        }

        String pNoun1 = getPNoun(taggedWords);

        String verb = getVerb(taggedWords);

        removeWord(taggedWords,"a");

        String cNoun = getCNoun(taggedWords);

        removeWord(taggedWords,"as");

        String pNoun2 = getPNoun(taggedWords);

        removeWord(taggedWords,".");

        Literal literal1 = new Literal(verb);
        literal1.getTerms().add(pNoun1);
        literal1.getTerms().add(pNoun2);

        Literal literal2 = new Literal(cNoun);
        literal2.getTerms().add(pNoun2);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);

        return aspRule;
    }


    private AspRule cNounVariableIsAdjective(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String cNoun = getCNoun(taggedWords);

        String variable = getVariable(taggedWords);

        removeWord(taggedWords,"is");

        String adjective = getAdjective(taggedWords);

        removeWord(taggedWords,".");

        Literal literal1 = new Literal(adjective);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(cNoun);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);

        return aspRule;
    }

    private AspRule pNounIsAdjective(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String pNoun = getPNoun(taggedWords);

        removeWord(taggedWords,"is");

        String adjective = getAdjective(taggedWords);

        removeWord(taggedWords,".");

        Literal literal = new Literal(adjective);
        literal.getTerms().add(pNoun);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    private AspRule thePNounIsAdjective(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        removeFirstWord(taggedWords);

        String pNoun = getPNoun(taggedWords);

        removeWord(taggedWords,"is");

        String adjective = getAdjective(taggedWords);

        removeWord(taggedWords,".");

        Literal literal = new Literal(adjective);
        literal.getTerms().add("X");

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    private AspRule pNounIsACNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String pNoun = getPNoun(taggedWords);

        removeWord(taggedWords,"is");

        removeWord(taggedWords,"a");

        String cNoun = getCNoun(taggedWords);


        removeWord(taggedWords,".");


        Literal literal = new Literal(cNoun);
        literal.getTerms().add(pNoun);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    private AspRule cNounVariableIsACNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String cNoun1 = getCNoun(taggedWords);

        String variable = getVariable(taggedWords);

        removeWord(taggedWords,"is");

        removeWord(taggedWords,"a");

        String cNoun2 = getCNoun(taggedWords);


        removeWord(taggedWords,".");


        Literal literal1 = new Literal(cNoun1);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(cNoun2);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);

        return aspRule;

    }

    private AspRule thereIsACNounVariable(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        removeFirstWord(taggedWords);
        removeFirstWord(taggedWords);
        removeFirstWord(taggedWords);

        String cNoun = getCNoun(taggedWords);
        String variable = getVariable(taggedWords);

        removeWord(taggedWords,".");

        Literal literal = new Literal(cNoun);
        literal.getTerms().add(variable);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    private AspRule cNounVariableVerbMoreThanNumberCNounVariable(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String cNoun1 = getCNoun(taggedWords);
        String variable1 = getVariable(taggedWords);

        String verb = getVerb(taggedWords);

        boolean more = false;
        
        try {
            removeWord(taggedWords, "more");
            more = true;
        }catch (SentenceValidationException e){
            removeWord(taggedWords, "less");
        }

        removeWord(taggedWords, "than");

        String number = getNumber(taggedWords);

        String cNoun2 = getCNoun(taggedWords);
        String variable2 = getVariable(taggedWords);
        removeWord(taggedWords,".");

        Literal literal;
        if(more) {
            literal = new Literal(String.format("#count{%s : %s(%s,%s)} > %s", variable2, verb, variable1, variable2, number));
        } else {
            literal = new Literal(String.format("#count{%s : %s(%s,%s)} < %s", variable2, verb, variable1, variable2, number));
        }

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }





    private void removeFirstWord(ArrayList<TaggedWord> taggedWords){
        taggedWords.remove(0);
    }

    private String getCNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String cNoun = "";

        while (taggedWords.get(0).tag().matches("(NN|NNS)"))
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
            throw new SentenceValidationException();
        }

        return cNoun;
    }

    private boolean isVariable(String str) {
        if(str.length() == 1 &&
                str.equals(str.toUpperCase()) && str.matches("[A-Z]")) {
            return true;
        }

        return false;
    }

    private String getVerb(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String verb = "";
        if(taggedWords.get(0).tag().matches("(VB|VBD|VBG|VBN|VBP|VBZ)"))
        {
            verb = StanfordParser.getInstance().getBaseFormOfWord(taggedWords.get(0).value());
            removeFirstWord(taggedWords);
        }
        if(verb == ""){
            throw new SentenceValidationException();
        }

        return verb;
    }

    private void removeWord(List<TaggedWord> taggedWords, String word) throws SentenceValidationException {

        if(taggedWords.get(0).value().equals(word))
        {
            taggedWords.remove(0);
        }
        else {
            throw new SentenceValidationException();
        }
    }

    private String getPNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        String pNoun= "";

        while (taggedWords.get(0).tag().matches("(NN|NNP)"))
        {
            pNoun += taggedWords.get(0).value().toLowerCase();
            taggedWords.remove(0);
        }

        if(pNoun.equals(""))
        {
            throw new SentenceValidationException();
        }

        return pNoun;
    }

    private String getAdjective(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String adjective = "";

        while (taggedWords.get(0).tag().matches("(JJ|VBN)"))
        {
            adjective += taggedWords.get(0).value().toLowerCase();
            taggedWords.remove(0);
        }

        if(adjective.equals(""))
        {
            throw new SentenceValidationException();
        }

        return adjective;
    }

    private String getVariable(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String variable = "";

        if(isVariable(taggedWords.get(0).value())){
             variable = taggedWords.get(0).value();
             removeFirstWord(taggedWords);
             return variable;
        }

        throw new SentenceValidationException();
    }

    private String getNumber(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

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
            throw new SentenceValidationException();
        }

        removeFirstWord(taggedWords);

        return number;
    }

    /* Workaround for the following problem:
     * if a Variable is followed by a dot then
     * the parser tags the variable and the dot
     * together as NNP.
     * e.g "X." --> X./NNP
     */
    private String addWhitespanceBeforDot(String sentence) {
        if(sentence.matches(".*\\.$"))
        {
            sentence = sentence.substring(0,sentence.lastIndexOf('.'));
            sentence = sentence + " .";
        }

        return sentence;
    }
}
