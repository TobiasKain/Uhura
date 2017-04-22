package at.tuwien.CNL2ASP.sentences;

import at.tuwien.CNL2ASP.SentenceValidationException;
import at.tuwien.CNL2ASP.WordDetector;
import at.tuwien.entity.asp.AspRule;
import at.tuwien.entity.asp.Literal;
import edu.stanford.nlp.ling.TaggedWord;

import java.util.ArrayList;

/**
 * Created by tobiaskain on 22/04/2017.
 */
public class DefaultSentences {

    private static String ABNORMAL_TAG = "ab";

    private WordDetector wordDetector;
    int defaultTagCounter;

    public DefaultSentences(WordDetector wordDetector) {
        this.wordDetector = wordDetector;
        defaultTagCounter = 1;
    }

    private String getNextDefaultTag(){
        return "d" + (defaultTagCounter++);
    }

    public AspRule cNounNormallyAreAdjective(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        String cNoun = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"normally");

        wordDetector.removeWord(taggedWords,"are");

        boolean negated = wordDetector.isNegation(taggedWords);

        String adjective = wordDetector.getAdjectiveAndOrPreposition(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        String variable = "X";

        Literal literal1 = new Literal(adjective, negated);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(cNoun);
        literal2.getTerms().add(variable);

        Literal literal3 = new Literal(ABNORMAL_TAG,true);
        literal3.getTerms().add(getNextDefaultTag());
        literal3.getTerms().add(variable);

        Literal literal4 = new Literal(adjective, true);
        literal4.setStrongNegated(!negated);
        literal4.getTerms().add(variable);

        AspRule aspRule = new AspRule();

        aspRule.getHead().add(literal1);
        aspRule.getBody().add(literal2);
        aspRule.getBody().add(literal3);
        aspRule.getBody().add(literal4);

        return aspRule;
    }


    public AspRule cNounNormallyAreAdjectivePrepositionCNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        String cNoun1 = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"normally");

        wordDetector.removeWord(taggedWords,"are");

        boolean negated = wordDetector.isNegation(taggedWords);

        String adjective = wordDetector.getAdjectiveAndOrPreposition(taggedWords);

        String cNoun2 = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        String variable = "X";

        Literal literal1 = new Literal(adjective, negated);
        literal1.getTerms().add(variable);
        literal1.getTerms().add(cNoun2);


        Literal literal2 = new Literal(cNoun1);
        literal2.getTerms().add(variable);

        Literal literal3 = new Literal(ABNORMAL_TAG,true);
        literal3.getTerms().add(getNextDefaultTag());
        literal3.getTerms().add(variable);

        Literal literal4 = new Literal(adjective, true);
        literal4.setStrongNegated(!negated);
        literal4.getTerms().add(variable);
        literal4.getTerms().add(cNoun2);

        AspRule aspRule = new AspRule();

        aspRule.getHead().add(literal1);
        aspRule.getBody().add(literal2);
        aspRule.getBody().add(literal3);
        aspRule.getBody().add(literal4);

        return aspRule;
    }

    public AspRule cNounNormallyVerb(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        String cNoun = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"normally");

        boolean negated = wordDetector.isNegation(taggedWords);

        String verb = wordDetector.getVerb(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        String variable = "X";

        Literal literal1 = new Literal(verb, negated);
        literal1.getTerms().add(variable);


        Literal literal2 = new Literal(cNoun);
        literal2.getTerms().add(variable);

        Literal literal3 = new Literal(ABNORMAL_TAG,true);
        literal3.getTerms().add(getNextDefaultTag());
        literal3.getTerms().add(variable);

        Literal literal4 = new Literal(verb, true);
        literal4.setStrongNegated(!negated);
        literal4.getTerms().add(variable);

        AspRule aspRule = new AspRule();

        aspRule.getHead().add(literal1);
        aspRule.getBody().add(literal2);
        aspRule.getBody().add(literal3);
        aspRule.getBody().add(literal4);

        return aspRule;
    }

    public AspRule pNounIsAbnormalWithRespectToDefaultRuleTag(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        String pNoun = wordDetector.getPNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"is");
        wordDetector.removeWord(taggedWords,"abnormal");
        wordDetector.removeWord(taggedWords,"with");
        wordDetector.removeWord(taggedWords,"respect");
        wordDetector.removeWord(taggedWords,"to");

        String defaultTag = wordDetector.getDefaultTag(taggedWords);

        wordDetector.removeWord(taggedWords,".");

        Literal literal = new Literal(ABNORMAL_TAG);
        literal.getTerms().add(defaultTag);
        literal.getTerms().add(pNoun);

        AspRule aspRule = new AspRule();

        aspRule.getHead().add(literal);

        return aspRule;
    }
}
