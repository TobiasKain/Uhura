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
    private TranslatorHelper translatorHelper;

    public CnlToAspTranslator(List<String> inputStrings) {
        this.inputStrings = inputStrings;
    }

    public Translation translate()
    {
        translatorHelper = new TranslatorHelper();
        Translation translation = new Translation();

        List<AspRule> aspRules = new ArrayList<>();
        translation.setAspRules(aspRules);

        List<String> errors = new ArrayList<>();
        translation.setErrors(errors);

        for (String sentence: inputStrings) {
            try {
                aspRules.add(translateSentence(sentence));
            } catch (SentenceValidationException e) {
                errors.add(String.format("Error in sentence \"%s\": %s",sentence, e.getMessage()));
            }
        }

        return translation;
    }


    private AspRule translateSentence(String sentence) throws SentenceValidationException {
        return translateSentence(sentence,null);
    }

    private AspRule translateSentence(String sentence, ArrayList<TaggedWord> parentSentence) throws SentenceValidationException {

        AspRule aspRule = null;

        sentence = translatorHelper.addWhitespacesBeforeDot(sentence);
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
        else if(sentence.matches(".* or .*\\.$")){
            aspRule = or(taggedWords);
        }
        else if (sentence.matches(".* (more|less) than .*\\.$")){
            aspRule = cNounVariableVerbMoreLessThanNumberCNounVariable(taggedWords, parentSentence);
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
        else if(sentence.matches(".* is(n't | n't | not | )a .*\\.$")) {
            aspRule = pNounIsACNoun(taggedWords);
        }
        else if(sentence.matches(".* [a-z] is .*\\.$")){
            aspRule = cNounVariableIsAdjective(taggedWords);
        }
        else if(sentence.matches(".* is(n't | n't | not | ).*\\.$")){
            aspRule = pNounIsAdjective(taggedWords);
        }
        else if(sentence.matches(".* [a-z] .* a .* as .*\\.$")){
            aspRule = aCNounVariableVerbACNounAsPNoun(taggedWords);
        }
        else if(sentence.matches(".* [a-z] .* [a-z] \\.$")){
            aspRule = cNounVariableVerbCNounVariable(taggedWords);
        }
        else if(sentence.matches(".* a .* as .*\\.$")){
            aspRule = aPNounVerbACNounAsPNoun(taggedWords);
        }

        if(aspRule == null){
            throw new SentenceValidationException("Sentence doesn't match any pattern.");
        }

        return aspRule;
    }


    private AspRule excludeThat(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        ArrayList<TaggedWord> parentTaggedWords = new ArrayList<>();
        parentTaggedWords.addAll(taggedWords);

        AspRule aspRule = new AspRule();

        translatorHelper.removeFirstWord(taggedWords);
        translatorHelper.removeFirstWord(taggedWords);

        while (!taggedWords.get(0).value().equals(".")){
            String sentence = "";

            if(taggedWords.get(0).value().equals("and") && taggedWords.get(1).value().equals("that"))
            {
                translatorHelper.removeFirstWord(taggedWords);
                translatorHelper.removeFirstWord(taggedWords);
            }

            while (!taggedWords.get(0).value().equals(".") && (!taggedWords.get(0).value().equals("and") && !taggedWords.get(1).value().equals("that"))){
                sentence += taggedWords.get(0).value() + " ";
                taggedWords.remove(0);
            }
            sentence = sentence.trim() + ".";
            aspRule.getBody().addAll(translateSentence(sentence, parentTaggedWords).getHead());
        }

        return aspRule;
    }

    private AspRule ifThen(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        ArrayList<TaggedWord> parentTaggedWords = new ArrayList<>();
        parentTaggedWords.addAll(taggedWords);

        AspRule aspRule = new AspRule();

        translatorHelper.removeFirstWord(taggedWords);

        /* Body */
        while (!taggedWords.get(0).value().equals("then")){
            String sentence = "";
            if(taggedWords.get(0).value().equals("and"))
            {
                translatorHelper.removeFirstWord(taggedWords);
            }
            while (!taggedWords.get(0).value().equals("and") && !taggedWords.get(0).value().equals("then")){
                sentence += taggedWords.get(0).value() + " ";
                taggedWords.remove(0);
            }
            sentence = sentence.trim() + ".";
            aspRule.getBody().addAll(translateSentence(sentence, parentTaggedWords).getHead());
        }

        taggedWords.remove(0);

        /* Head */
        String sentence = "";
        while (!taggedWords.get(0).value().equals(".")){
            sentence += taggedWords.get(0).value() + " ";
            taggedWords.remove(0);
        }
        sentence = sentence.trim() + ".";

        AspRule headRule = translateSentence(sentence, parentTaggedWords);
        aspRule.getHead().addAll(headRule.getHead());
        aspRule.setOr(headRule.isOr());

        return aspRule;
    }

    private AspRule or(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        ArrayList<TaggedWord> parentTaggedWords = new ArrayList<>();
        parentTaggedWords.addAll(taggedWords);

        AspRule aspRule = new AspRule();
        aspRule.setOr(true);

        while (!taggedWords.get(0).value().equals(".")){
            String sentence = "";

            if(taggedWords.get(0).value().equals("or"))
            {
                translatorHelper.removeFirstWord(taggedWords);
            }

            while (!taggedWords.get(0).value().equals(".") && !taggedWords.get(0).value().equals("or")){
                sentence += taggedWords.get(0).value() + " ";
                taggedWords.remove(0);
            }
            sentence = sentence.trim() + ".";

            // TODO check if head is null
            aspRule.getHead().add(translateSentence(sentence, parentTaggedWords).getHead().get(0));
        }

        return aspRule;
    }

    private AspRule aCNounVariableIsACNounOfACNounVariable(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        translatorHelper.removeFirstWord(taggedWords);
        String cNoun1 = translatorHelper.getCNoun(taggedWords);
        String variable1 = translatorHelper.getVariable(taggedWords);
        translatorHelper.removeWord(taggedWords,"is");
        translatorHelper.removeWord(taggedWords,"a");
        String cNoun2 = translatorHelper.getCNoun(taggedWords);
        translatorHelper.removeWord(taggedWords,"of");
        translatorHelper.removeWord(taggedWords,"a");
        String cNoun3 = translatorHelper.getCNoun(taggedWords);
        String variable2 = translatorHelper.getVariable(taggedWords);

        translatorHelper.removeWord(taggedWords,".");

        Literal literal1 = new Literal(cNoun2);
        literal1.getTerms().add(variable1);
        literal1.getTerms().add(variable2);

        Literal literal2 = new Literal(cNoun1);
        literal2.getTerms().add(variable1);

        Literal literal3 = new Literal(cNoun3);
        literal3.getTerms().add(variable2);

        AspRule aspRule = new AspRule();

        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);
        aspRule.getHead().add(literal3);

        return aspRule;
    }

    private AspRule aCNounVariableVerbACNounAsPNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        if(taggedWords.get(0).value().equals("a")) {
            translatorHelper.removeFirstWord(taggedWords);
        }

        String cNoun1 = translatorHelper.getCNoun(taggedWords);

        String variable = translatorHelper.getVariable(taggedWords);

        String verb = translatorHelper.getVerb(taggedWords);

        translatorHelper.removeWord(taggedWords,"a");

        String cNoun2 = translatorHelper.getCNoun(taggedWords);

        translatorHelper.removeWord(taggedWords,"as");

        String pNoun = translatorHelper.getPNoun(taggedWords);

        translatorHelper.removeWord(taggedWords,".");

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
            translatorHelper.removeFirstWord(taggedWords);
        }

        String pNoun1 = translatorHelper.getPNoun(taggedWords);

        String verb = translatorHelper.getVerb(taggedWords);

        translatorHelper.removeWord(taggedWords,"a");

        String cNoun = translatorHelper.getCNoun(taggedWords);

        translatorHelper.removeWord(taggedWords,"as");

        String pNoun2 = translatorHelper.getPNoun(taggedWords);

        translatorHelper.removeWord(taggedWords,".");

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

        String cNoun = translatorHelper.getCNoun(taggedWords);

        String variable = translatorHelper.getVariable(taggedWords);

        translatorHelper.removeWord(taggedWords,"is");

        String adjective = translatorHelper.getAdjective(taggedWords);

        translatorHelper.removeWord(taggedWords,".");

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

        String pNoun = translatorHelper.getPNoun(taggedWords);

        translatorHelper.removeWord(taggedWords,"is");

        boolean negated = translatorHelper.isNegation(taggedWords);

        String adjective = translatorHelper.getAdjective(taggedWords);

        translatorHelper.removeWord(taggedWords,".");

        Literal literal = new Literal(adjective,negated);
        literal.getTerms().add(pNoun);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    private AspRule thePNounIsAdjective(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        translatorHelper.removeFirstWord(taggedWords);

        String pNoun = translatorHelper.getPNoun(taggedWords);

        translatorHelper.removeWord(taggedWords,"is");

        String adjective = translatorHelper.getAdjective(taggedWords);

        translatorHelper.removeWord(taggedWords,".");

        Literal literal = new Literal(adjective);
        literal.getTerms().add("X");

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    private AspRule pNounIsACNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String pNoun = translatorHelper.getPNoun(taggedWords);

        translatorHelper.removeWord(taggedWords,"is");

        boolean negated = translatorHelper.isNegation(taggedWords);

        translatorHelper.removeWord(taggedWords,"a");

        String cNoun = translatorHelper.getCNoun(taggedWords);


        translatorHelper.removeWord(taggedWords,".");


        Literal literal = new Literal(cNoun,negated);
        literal.getTerms().add(pNoun);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    private AspRule cNounVariableIsACNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String cNoun1 = translatorHelper.getCNoun(taggedWords);

        String variable = translatorHelper.getVariable(taggedWords);

        translatorHelper.removeWord(taggedWords,"is");

        translatorHelper.removeWord(taggedWords,"a");

        String cNoun2 = translatorHelper.getCNoun(taggedWords);


        translatorHelper.removeWord(taggedWords,".");


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

        translatorHelper.removeFirstWord(taggedWords);
        translatorHelper.removeFirstWord(taggedWords);
        translatorHelper.removeFirstWord(taggedWords);

        String cNoun = translatorHelper.getCNoun(taggedWords);
        String variable = translatorHelper.getVariable(taggedWords);

        translatorHelper.removeWord(taggedWords,".");

        Literal literal = new Literal(cNoun);
        literal.getTerms().add(variable);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    private AspRule cNounVariableVerbMoreLessThanNumberCNounVariable(ArrayList<TaggedWord> taggedWords, ArrayList<TaggedWord> parentSentence) throws SentenceValidationException {

        String cNoun1 = translatorHelper.getCNoun(taggedWords);
        String variable1 = translatorHelper.getVariable(taggedWords);

        String verb = translatorHelper.getVerb(taggedWords);

        boolean more = false;

        try {
            translatorHelper.removeWord(taggedWords, "more");
            more = true;
        }catch (SentenceValidationException e){
            translatorHelper.removeWord(taggedWords, "less");
        }

        translatorHelper.removeWord(taggedWords, "than");

        String number = translatorHelper.getNumber(taggedWords);

        String cNoun2 = translatorHelper.getCNoun(taggedWords);
        String variable2 = translatorHelper.getVariable(taggedWords);
        translatorHelper.removeWord(taggedWords,".");

        String countVariable;
        if(translatorHelper.getVariableCount(parentSentence,variable2) > translatorHelper.getVariableCount(parentSentence,variable1))
        {
            countVariable = variable1;
        }
        else {
            countVariable = variable2;
        }

        Literal literal;
        if(more) {
            literal = new Literal(String.format("#count{%s : %s(%s,%s)} > %s", countVariable, verb, variable1, variable2, number));
        } else {
            literal = new Literal(String.format("#count{%s : %s(%s,%s)} < %s", countVariable, verb, variable1, variable2, number));
        }



        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    private AspRule cNounVariableVerbCNounVariable(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        if(taggedWords.get(0).value().equals("a")) {
            translatorHelper.removeFirstWord(taggedWords);
        }

        String cNoun1 = translatorHelper.getCNoun(taggedWords);

        String variable1 = translatorHelper.getVariable(taggedWords);

        boolean negated = translatorHelper.isNegation(taggedWords);

        String verb = translatorHelper.getVerb(taggedWords);

        if(taggedWords.get(0).value().equals("a")) {
            translatorHelper.removeFirstWord(taggedWords);
        }

        String cNoun2 = translatorHelper.getCNoun(taggedWords);

        String variable2 = translatorHelper.getVariable(taggedWords);

        translatorHelper.removeWord(taggedWords,".");

        Literal literal1 = new Literal(verb, negated);
        literal1.getTerms().add(variable1);
        literal1.getTerms().add(variable2);

        Literal literal2 = new Literal(cNoun1);
        literal2.getTerms().add(variable1);

        Literal literal3 = new Literal(cNoun2);
        literal3.getTerms().add(variable2);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);
        aspRule.getHead().add(literal3);

        return aspRule;
    }
}
