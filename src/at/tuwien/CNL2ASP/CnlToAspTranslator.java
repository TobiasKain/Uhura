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

        ArrayList<TaggedWord> taggedWords = StanfordParser.getInstance().parse(sentence).taggedYield();

        // PNoun is a CNoun.
        if (sentence.matches("If .* then .*\\.$")){
            aspRule = ifThen(taggedWords);
        }
        else if(sentence.matches("the .* is a .*\\.$")) {
            aspRule = thePNounIsAAdjective(taggedWords);
        }
        else if(sentence.matches(".* is a .*\\.$")) {
            aspRule = pNounIsACNoun(taggedWords);
        }
        else if(sentence.matches(".* is .*\\.$")){
            aspRule = pNounIsAAdjective(taggedWords);
        }
        else if(sentence.matches("There is a .*\\.$"))
        {
            aspRule = thereIsACNoun(taggedWords);
        }
        else if(sentence.matches("A .* a .* as .*\\.$")){
            aspRule = aCNounVerbACNounAsPNoun(taggedWords);
        }

        return aspRule;
    }

    private AspRule ifThen(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        AspRule aspRule = new AspRule();

        taggedWords.remove(0);

        while (!taggedWords.get(0).value().equals("then")){
            String sentence = "";
            while (!taggedWords.get(0).value().equals("then") || !taggedWords.get(0).value().equals("then")){
                sentence += taggedWords.get(0).value() + " ";
                taggedWords.remove(0);
            }
            sentence = sentence.trim() + ".";
            aspRule.getHead().addAll(translateSentence(sentence).getHead());
        }

        taggedWords.remove(0);

        String sentence = "";
        while (!taggedWords.get(0).value().equals(".")){
            sentence += taggedWords.get(0).value() + " ";
            taggedWords.remove(0);
        }
        sentence = sentence.trim() + ".";
        aspRule.getBody().addAll(translateSentence(sentence).getHead());

        return aspRule;
    }

    private AspRule aCNounVerbACNounAsPNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        taggedWords.remove(0);

        String cNoun1 = "";

        while (taggedWords.get(0).tag().equals("NN"))
        {
            cNoun1 += taggedWords.get(0).value().toLowerCase();
            taggedWords.remove(0);
        }

        if(cNoun1.equals(""))
        {
            throw new SentenceValidationException();
        }

        String verb = "";
        if(taggedWords.get(0).tag().matches("(VB|VBD|VBG|VBN|VBP|VBZ)"))
        {
            verb = taggedWords.get(0).value();
            taggedWords.remove(0);
        }

        if(taggedWords.get(0).value().equals("a"))
        {
            taggedWords.remove(0);
        }
        else {
            throw new SentenceValidationException();
        }

        String cNoun2 = "";

        while (taggedWords.get(0).tag().equals("NN"))
        {
            cNoun2 += taggedWords.get(0).value().toLowerCase();
            taggedWords.remove(0);
        }

        if(cNoun2.equals(""))
        {
            throw new SentenceValidationException();
        }

        String pNoun = "";

        while (taggedWords.get(0).tag().matches("(NN|NNP)"))
        {
            pNoun += taggedWords.get(0).value().toLowerCase();
            taggedWords.remove(0);
        }

        if(pNoun.equals(""))
        {
            throw new SentenceValidationException();
        }

        if(taggedWords.get(0).value().equals("."))
        {
            taggedWords.remove(0);
        }
        else {
            throw new SentenceValidationException();
        }

        Literal literal1 = new Literal(cNoun1);
        literal1.getTerms().add("X");

        Literal literal2 = new Literal(cNoun2);
        literal2.getTerms().add(pNoun);

        Literal literal3 = new Literal(verb);
        literal3.getTerms().add("X");
        literal3.getTerms().add(pNoun);


        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);
        aspRule.getHead().add(literal3);

        return null;
    }

    private AspRule pNounIsAAdjective(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String pNoun = "";

        while (taggedWords.get(0).tag().equals("NNP"))
        {
            pNoun += taggedWords.get(0).value().toLowerCase();
            taggedWords.remove(0);
        }

        if(pNoun.equals(""))
        {
            throw new SentenceValidationException();
        }

        if(taggedWords.get(0).value().equals("is"))
        {
            taggedWords.remove(0);
        }
        else {
            throw new SentenceValidationException();
        }

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


        if(taggedWords.get(0).value().equals("."))
        {
            taggedWords.remove(0);
        }
        else {
            throw new SentenceValidationException();
        }

        Literal literal = new Literal(adjective);
        literal.getTerms().add(pNoun);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    private AspRule thePNounIsAAdjective(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String pNoun = "";

        taggedWords.remove(0);

        while (taggedWords.get(0).tag().equals("NNP"))
        {
            pNoun += taggedWords.get(0).value().toLowerCase();
            taggedWords.remove(0);
        }

        if(pNoun.equals(""))
        {
            throw new SentenceValidationException();
        }

        if(taggedWords.get(0).value().equals("is"))
        {
            taggedWords.remove(0);
        }
        else {
            throw new SentenceValidationException();
        }

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


        if(taggedWords.get(0).value().equals("."))
        {
            taggedWords.remove(0);
        }
        else {
            throw new SentenceValidationException();
        }

        Literal literal = new Literal(adjective);
        literal.getTerms().add("X");

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    public AspRule pNounIsACNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String pNoun = "";

        while (taggedWords.get(0).tag().equals("NNP") || taggedWords.get(0).tag().equals("NN"))
        {
            pNoun += taggedWords.get(0).value().toLowerCase();
            taggedWords.remove(0);
        }

        if(pNoun.equals(""))
        {
            throw new SentenceValidationException();
        }

        if(taggedWords.get(0).value().equals("is"))
        {
            taggedWords.remove(0);
        }
        else {
            throw new SentenceValidationException();
        }

        if(taggedWords.get(0).value().equals("a"))
        {
            taggedWords.remove(0);
        }
        else {
            throw new SentenceValidationException();
        }

        String cNoun = "";

        while (taggedWords.get(0).tag().equals("NN"))
        {
            cNoun += taggedWords.get(0).value().toLowerCase();
            taggedWords.remove(0);
        }

        if(cNoun.equals(""))
        {
            throw new SentenceValidationException();
        }


        if(taggedWords.get(0).value().equals("."))
        {
            taggedWords.remove(0);
        }
        else {
            throw new SentenceValidationException();
        }


        Literal literal = new Literal(cNoun);
        literal.getTerms().add(pNoun);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    private AspRule thereIsACNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        /* remove "There is a" */
        for (int i = 0 ; i < 2; i++)
        {
            taggedWords.remove(0);
        }

        String cNoun = "";

        while (taggedWords.get(0).tag().equals("JJ"))
        {
            cNoun += taggedWords.get(0).value().toLowerCase();
            taggedWords.remove(0);
        }

        if(cNoun.equals(""))
        {
            throw new SentenceValidationException();
        }


        if(taggedWords.get(0).value().equals("."))
        {
            taggedWords.remove(0);
        }
        else {
            throw new SentenceValidationException();
        }

        Literal literal = new Literal(cNoun);
        literal.getTerms().add("X");

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }
}
