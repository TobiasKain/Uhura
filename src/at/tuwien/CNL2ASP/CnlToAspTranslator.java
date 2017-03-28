package at.tuwien.CNL2ASP;

import at.tuwien.ASP.AspRule;
import at.tuwien.ASP.Literal;
import edu.stanford.nlp.ling.TaggedWord;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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
        } else if (sentence.matches("if .* then .*\\.$")){
            aspRule = ifThen(taggedWords);
        }
        else if(sentence.matches("the .* is .*\\.$")) {
            aspRule = thePNounIsAdjective(taggedWords);
        }
        else if(sentence.matches(".* [a-z] is a .*\\.$")){
            aspRule = cNounVariableIsACNoun(taggedWords);
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
        else if(sentence.matches("there is a .*\\.$"))
        {
            aspRule = thereIsACNoun(taggedWords);
        }
        else if(sentence.matches("a .* [a-z] .* a .* as .*\\.$")){
            aspRule = aCNounVariableVerbACNounAsPNoun(taggedWords);
        }

        return aspRule;
    }

    private AspRule aCNounVariableIsACNounOfACNounVariable(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        removeFirstWord(taggedWords);
        String cNoun1 = getCNoun(taggedWords);
        String varialbe1 = getVarialbe(taggedWords);
        removeWord(taggedWords,"is");
        removeWord(taggedWords,"a");
        String cNoun2 = getCNoun(taggedWords);
        removeWord(taggedWords,"of");
        removeWord(taggedWords,"a");
        String cNoun3 = getCNoun(taggedWords);
        String varialbe2 = getVarialbe(taggedWords);

        removeWord(taggedWords,".");

        Literal literal1 = new Literal(cNoun1);
        literal1.getTerms().add(varialbe1);

        Literal literal2 = new Literal(cNoun3);
        literal2.getTerms().add(varialbe2);

        Literal literal3 = new Literal(cNoun2);
        literal3.getTerms().add(varialbe1);
        literal3.getTerms().add(varialbe2);

        AspRule aspRule = new AspRule();

        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);
        aspRule.getHead().add(literal3);

        return aspRule;
    }

    private AspRule ifThen(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        AspRule aspRule = new AspRule();

        removeFirstWord(taggedWords);

        while (!taggedWords.get(0).value().equals("then")){
            String sentence = "";
            while (!taggedWords.get(0).value().equals("then") || !taggedWords.get(0).value().equals("then")){
                sentence += taggedWords.get(0).value() + " ";
                taggedWords.remove(0);
            }
            sentence = sentence.trim() + ".";
            aspRule.getBody().addAll(translateSentence(sentence).getHead());
        }

        taggedWords.remove(0);

        String sentence = "";
        while (!taggedWords.get(0).value().equals(".")){
            sentence += taggedWords.get(0).value() + " ";
            taggedWords.remove(0);
        }
        sentence = sentence.trim() + ".";
        aspRule.getHead().addAll(translateSentence(sentence).getHead());

        return aspRule;
    }

    private AspRule aCNounVariableVerbACNounAsPNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        removeFirstWord(taggedWords);

        String cNoun1 = getCNoun(taggedWords);

        String variable = getVarialbe(taggedWords);

        String verb = getVerb(taggedWords);

        removeWord(taggedWords,"a");

        String cNoun2 = getCNoun(taggedWords);

        removeWord(taggedWords,"as");

        String pNoun = getPNoun(taggedWords);

        removeWord(taggedWords,".");

        Literal literal1 = new Literal(cNoun1);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(cNoun2);
        literal2.getTerms().add(pNoun);

        Literal literal3 = new Literal(verb);
        literal3.getTerms().add(variable);
        literal3.getTerms().add(pNoun);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);
        aspRule.getHead().add(literal3);

        return aspRule;
    }

    private AspRule cNounVariableIsAdjective(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String cNoun = getCNoun(taggedWords);

        String variable = getVarialbe(taggedWords);

        removeWord(taggedWords,"is");

        String adjective = getAdjective(taggedWords);

        removeWord(taggedWords,".");

        Literal literal1 = new Literal(cNoun);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(adjective);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();
      //  aspRule.getHead().add(literal1);
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

        String variable = getVarialbe(taggedWords);

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

    private AspRule thereIsACNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        removeFirstWord(taggedWords);
        removeFirstWord(taggedWords);
        removeFirstWord(taggedWords);

        String cNoun = getCNoun(taggedWords);

        removeWord(taggedWords,".");

        Literal literal = new Literal(cNoun);
        literal.getTerms().add("X");

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

            taggedWords.remove(0);
        }

        if(cNoun.equals(""))
        {
            throw new SentenceValidationException();
        }

        return cNoun;
    }

    private boolean isVariable(String str) {
        if(str.length() == 1 &&
                str.equals(str.toUpperCase())) {
            return true;
        }

        return false;
    }

    private String getVerb(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String verb = "";
        if(taggedWords.get(0).tag().matches("(VB|VBD|VBG|VBN|VBP|VBZ)"))
        {
            verb = StanfordParser.getInstance().getBaseFormOfWord(taggedWords.get(0).value());
            taggedWords.remove(0);
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

        while (taggedWords.get(0).tag().equals("JJ"))
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

    private String getVarialbe(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        return getCNoun(taggedWords);
    }

    /* Workaround for the following problem:
     * if a Variable is followed by a dot then
     * the parser tags the variable and the dot
     * together as NNP.
     * e.g "X." --> X./NNP
     */
    private String addWhitespanceBeforDot(String sentence)
    {
        if(sentence.matches(".*\\.$"))
        {
            sentence = sentence.substring(0,sentence.lastIndexOf('.'));
            sentence = sentence + " .";
        }

        return sentence;
    }
}
