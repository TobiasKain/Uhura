package at.tuwien.CNL2ASP.sentences;

import at.tuwien.CNL2ASP.SentenceValidationException;
import at.tuwien.CNL2ASP.WordDetector;
import at.tuwien.entity.Word;
import at.tuwien.entity.asp.AspRule;
import at.tuwien.entity.asp.Literal;
import edu.stanford.nlp.ling.TaggedWord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tobiaskain on 22/04/2017.
 */
public class SimpleSentences {

    private WordDetector wordDetector;

    public SimpleSentences(WordDetector wordDetector) {
        this.wordDetector = wordDetector;
    }

    public AspRule aCNounVariableIsACNounOfACNounVariable(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        wordDetector.removeFirstWord(taggedWords);

        String cNoun1 = wordDetector.getCNoun(taggedWords);

        String variable1 = wordDetector.getVariable(taggedWords);

        wordDetector.removeWord(taggedWords,"is");
        boolean negated = wordDetector.isNegation(taggedWords);
        wordDetector.removeWord(taggedWords,"(a|an)");

        String cNoun2 = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"of");
        wordDetector.removeWord(taggedWords,"(a|an)");

        String cNoun3 = wordDetector.getCNoun(taggedWords);

        String variable2 = wordDetector.getVariable(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal1 = new Literal(cNoun2, negated);
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

    public AspRule pNounIsCNounOfCNounVariable(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String pNoun = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"is");
        boolean negated = wordDetector.isNegation(taggedWords);

        if(taggedWords.get(0).value().equals("the")) {
            wordDetector.removeFirstWord(taggedWords);
        }

        String cNoun1 = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"of");

        if(taggedWords.get(0).value().equals("(a|an)")) {
            wordDetector.removeFirstWord(taggedWords);
        }

        String cNoun2 = wordDetector.getCNoun(taggedWords);

        String variable = wordDetector.getVariable(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal1 = new Literal(cNoun1, negated);
        literal1.getTerms().add(pNoun);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(cNoun2);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();

        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);

        return aspRule;
    }

    public AspRule pNounIsCNounOfPNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String pNoun1 = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"is");
        boolean negated = wordDetector.isNegation(taggedWords);

        if(taggedWords.get(0).value().equals("the")) {
            wordDetector.removeFirstWord(taggedWords);
        }

        String cNoun1 = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"of");

        String pNoun2 = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal1 = new Literal(cNoun1, negated);
        literal1.getTerms().add(pNoun1);
        literal1.getTerms().add(pNoun2);

        AspRule aspRule = new AspRule();

        aspRule.getHead().add(literal1);

        return aspRule;
    }

    public AspRule cNounVariableIsCNounOfPNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        if(taggedWords.get(0).value().equals("(a|an)")) {
            wordDetector.removeFirstWord(taggedWords);
        }

        String cNoun1 = wordDetector.getCNoun(taggedWords);

        String variable = wordDetector.getVariable(taggedWords);

        wordDetector.removeWord(taggedWords,"is");
        boolean negated = wordDetector.isNegation(taggedWords);

        if(taggedWords.get(0).value().equals("the")) {
            wordDetector.removeFirstWord(taggedWords);
        }

        String cNoun2 = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"of");

        String pNoun = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal1 = new Literal(cNoun2, negated);
        literal1.getTerms().add(variable);
        literal1.getTerms().add(pNoun);

        Literal literal2 = new Literal(cNoun1);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();

        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);

        return aspRule;
    }

    public AspRule cNounVariableVerbACNounAsPNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        if(taggedWords.get(0).value().equals("(a|an)")) {
            wordDetector.removeFirstWord(taggedWords);
        }

        String cNoun1 = wordDetector.getCNoun(taggedWords);

        String variable = wordDetector.getVariable(taggedWords);

        boolean negated = wordDetector.isNegation(taggedWords);

        String verb = wordDetector.getVerb(taggedWords);

        wordDetector.removeWord(taggedWords,"(a|an)");

        String cNoun2 = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"as");

        if(taggedWords.get(0).value().equals("(a|an)")) {
            wordDetector.removeFirstWord(taggedWords);
        }

        String pNoun = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal1 = new Literal(verb, negated);
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

    public AspRule pNounVerbACNounAsPNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        if(taggedWords.get(0).value().equals("(a|an)")) {
            wordDetector.removeFirstWord(taggedWords);
        }

        String pNoun1 = wordDetector.getPNoun(taggedWords);

        boolean negated = wordDetector.isNegation(taggedWords);

        String verb = wordDetector.getVerb(taggedWords);

        wordDetector.removeWord(taggedWords,"(a|an)");

        String cNoun = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"as");

        if(taggedWords.get(0).value().equals("(a|an)")) {
            wordDetector.removeFirstWord(taggedWords);
        }

        String pNoun2 = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal1 = new Literal(verb,negated);
        literal1.getTerms().add(pNoun1);
        literal1.getTerms().add(pNoun2);

        Literal literal2 = new Literal(cNoun);
        literal2.getTerms().add(pNoun2);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);

        return aspRule;
    }

    public AspRule cNounVariableIsAdjective(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String cNoun = wordDetector.getCNoun(taggedWords);

        String variable = wordDetector.getVariable(taggedWords);

        wordDetector.removeWord(taggedWords,"is");

        boolean negated = wordDetector.isNegation(taggedWords);

        String adjective = wordDetector.getAdjective(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal1 = new Literal(adjective,negated);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(cNoun);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);

        return aspRule;
    }

    public AspRule cNounVariableIsAdjectivePrepositionPNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String cNoun = wordDetector.getCNoun(taggedWords);

        String variable = wordDetector.getVariable(taggedWords);

        wordDetector.removeWord(taggedWords,"is");

        boolean negated = wordDetector.isNegation(taggedWords);

        String adjective = wordDetector.getAdjectiveAndOrPreposition(taggedWords);

        String pNoun = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal1 = new Literal(adjective,negated);
        literal1.getTerms().add(variable);
        literal1.getTerms().add(pNoun);

        Literal literal2 = new Literal(cNoun);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);

        return aspRule;
    }

    public AspRule pNounIsAdjectivePrepositionCNounVariable(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        String pNoun = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"is");

        boolean negated = wordDetector.isNegation(taggedWords);

        String adjective = wordDetector.getAdjectiveAndOrPreposition(taggedWords);

        String cNoun = wordDetector.getCNoun(taggedWords);

        String variable = wordDetector.getVariable(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal1 = new Literal(adjective,negated);
        literal1.getTerms().add(pNoun);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(cNoun);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);

        return aspRule;
    }

    public AspRule cNounVariableIsAdjectivePrepositionCNounVariable(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        String cNoun1 = wordDetector.getCNoun(taggedWords);

        String variable1 = wordDetector.getVariable(taggedWords);

        wordDetector.removeWord(taggedWords,"is");

        boolean negated = wordDetector.isNegation(taggedWords);

        String adjective = wordDetector.getAdjectiveAndOrPreposition(taggedWords);

        String cNoun2 = wordDetector.getCNoun(taggedWords);

        String variable2 = wordDetector.getVariable(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal1 = new Literal(adjective,negated);
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

    public AspRule pNounIsAdjective(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String pNoun = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"is");

        boolean negated = wordDetector.isNegation(taggedWords);

        String adjective = wordDetector.getAdjective(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal = new Literal(adjective,negated);
        literal.getTerms().add(pNoun);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    public AspRule pNounIsAdjectivePrepositionPNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String pNoun1 = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"is");

        boolean negated = wordDetector.isNegation(taggedWords);

        String adjective = wordDetector.getAdjectiveAndOrPreposition(taggedWords);

        String pNoun2 = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal = new Literal(adjective,negated);
        literal.getTerms().add(pNoun1);
        literal.getTerms().add(pNoun2);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    public AspRule pNounIsACNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String pNoun = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"is");

        boolean negated = wordDetector.isNegation(taggedWords);

        wordDetector.removeWord(taggedWords,"(a|an)");

        String cNoun = wordDetector.getCNoun(taggedWords);


        wordDetector.removeWord(taggedWords,".");


        Literal literal = new Literal(cNoun,negated);
        literal.getTerms().add(pNoun);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    public AspRule cNounVariableIsACNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String cNoun1 = wordDetector.getCNoun(taggedWords);

        String variable = wordDetector.getVariable(taggedWords);

        wordDetector.removeWord(taggedWords,"is");

        boolean negated = wordDetector.isNegation(taggedWords);

        wordDetector.removeWord(taggedWords,"(a|an)");

        String cNoun2 = wordDetector.getCNoun(taggedWords);


        wordDetector.removeWord(taggedWords,".");


        Literal literal1 = new Literal(cNoun2, negated);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(cNoun1);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);

        return aspRule;

    }

    public AspRule thereIsACNounVariable(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        wordDetector.removeWord(taggedWords,"there");
        wordDetector.removeWord(taggedWords,"is");
        boolean negated = wordDetector.isNegation(taggedWords);
        wordDetector.removeWord(taggedWords, "a");

        String cNoun = wordDetector.getCNoun(taggedWords);
        String variable = wordDetector.getVariable(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal = new Literal(cNoun, negated);
        literal.getTerms().add(variable);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    public AspRule cNounVariableVerbMoreLessThanNumberCNounVariable(ArrayList<TaggedWord> taggedWords, ArrayList<TaggedWord> parentSentence) throws SentenceValidationException {

        String cNoun1 = wordDetector.getCNoun(taggedWords);
        String variable1 = wordDetector.getVariable(taggedWords);

        String verb = wordDetector.getVerb(taggedWords);

        boolean more = false;

        try {
            wordDetector.removeWord(taggedWords, "more");
            more = true;
        }catch (SentenceValidationException e){
            wordDetector.removeWord(taggedWords, "less");
        }

        wordDetector.removeWord(taggedWords, "than");

        String number = wordDetector.getNumber(taggedWords);

        String cNoun2 = wordDetector.getCNoun(taggedWords);
        String variable2 = wordDetector.getVariable(taggedWords);
        wordDetector.removeWord(taggedWords,".");

        String countVariable;
        if(wordDetector.getVariableCount(parentSentence,variable2) > wordDetector.getVariableCount(parentSentence,variable1))
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

    public AspRule cNounVariableVerbCNounVariable(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        if(taggedWords.get(0).value().equals("(a|an)")) {
            wordDetector.removeFirstWord(taggedWords);
        }

        String cNoun1 = wordDetector.getCNoun(taggedWords);

        String variable1 = wordDetector.getVariable(taggedWords);

        boolean negated = wordDetector.isNegation(taggedWords);

        String verb = wordDetector.getVerb(taggedWords);

        if(taggedWords.get(0).value().equals("(a|an)")) {
            wordDetector.removeFirstWord(taggedWords);
        }

        String cNoun2 = wordDetector.getCNoun(taggedWords);

        String variable2 = wordDetector.getVariable(taggedWords);

        wordDetector.removeWord(taggedWords,".");

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

    public AspRule cNounVariableVerbPnoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        if(taggedWords.get(0).value().equals("(a|an)")) {
            wordDetector.removeFirstWord(taggedWords);
        }

        String cNoun = wordDetector.getCNoun(taggedWords);

        String variable = wordDetector.getVariable(taggedWords);

        boolean negated = wordDetector.isNegation(taggedWords);

        String verb = wordDetector.getVerb(taggedWords);

        if(taggedWords.get(0).value().equals("(a|an)")) {
            wordDetector.removeFirstWord(taggedWords);
        }

        String pNoun = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal1 = new Literal(verb, negated);
        literal1.getTerms().add(variable);
        literal1.getTerms().add(pNoun);

        Literal literal2 = new Literal(cNoun);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);

        return aspRule;
    }

    public AspRule pNounVerbCNounVariable(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        String pNoun = wordDetector.getPNoun(taggedWords);

        boolean negated = wordDetector.isNegation(taggedWords);

        String verb = wordDetector.getVerb(taggedWords);

        String cNoun = wordDetector.getCNoun(taggedWords);

        String variable = wordDetector.getVariable(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal1 = new Literal(verb,negated);
        literal1.getTerms().add(pNoun);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(cNoun);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);

        return aspRule;
    }

    public AspRule cNounVariableVerb(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        String cNoun = wordDetector.getCNoun(taggedWords);

        String variable = wordDetector.getVariable(taggedWords);

        boolean negated = wordDetector.isNegation(taggedWords);

        String verb = wordDetector.getVerb(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal1 = new Literal(verb,negated);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(cNoun);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);

        return aspRule;
    }

    public AspRule cNounVariableIsVerb(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        String cNoun = wordDetector.getCNoun(taggedWords);

        String variable = wordDetector.getVariable(taggedWords);

        wordDetector.removeWord(taggedWords,"is");

        boolean negated = wordDetector.isNegation(taggedWords);

        String verb = wordDetector.getVerb(taggedWords);

        Literal literal1 = new Literal(verb,negated);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(cNoun);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);
        aspRule.getHead().add(literal2);

        return aspRule;
    }

    public AspRule pNounVerbPNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        String pNoun1 = wordDetector.getPNoun(taggedWords);

        boolean negated = wordDetector.isNegation(taggedWords);

        String verb = wordDetector.getVerb(taggedWords);

        String pNoun2 = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal1 = new Literal(verb,negated);
        literal1.getTerms().add(pNoun1);
        literal1.getTerms().add(pNoun2);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal1);

        return aspRule;
    }

    public AspRule pNounVerb(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        String pNoun = wordDetector.getPNoun(taggedWords);

        boolean negated = wordDetector.isNegation(taggedWords);

        String verb = wordDetector.getVerb(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal = new Literal(verb,negated);
        literal.getTerms().add(pNoun);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }

    public AspRule pNounIsVerb(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        String pNoun = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"is");

        boolean negated = wordDetector.isNegation(taggedWords);

        String verb = wordDetector.getVerb(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal = new Literal(verb,negated);
        literal.getTerms().add(pNoun);

        AspRule aspRule = new AspRule();
        aspRule.getHead().add(literal);

        return aspRule;
    }
}
