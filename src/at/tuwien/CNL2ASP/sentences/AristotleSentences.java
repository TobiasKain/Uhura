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
public class AristotleSentences {

    private WordDetector wordDetector;

    public AristotleSentences(WordDetector wordDetector) {
        this.wordDetector = wordDetector;
    }

    public AspRule allCNounAreCNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        wordDetector.removeWord(taggedWords,"all");

        String cNoun1 = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"are");

        String cNoun2 = wordDetector.getCNoun(taggedWords);

        String variable = "X";

        Literal literal1 = new Literal(cNoun2);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(cNoun1);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();

        aspRule.getHead().add(literal1);
        aspRule.getBody().add(literal2);

        return aspRule;
    }

    public AspRule allCNounAreAdjective(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        wordDetector.removeWord(taggedWords,"all");

        String cNoun = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"are");

        String adjective = wordDetector.getAdjectiveAndOrPreposition(taggedWords);

        String variable = "X";

        Literal literal1 = new Literal(adjective);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(cNoun);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();

        aspRule.getHead().add(literal1);
        aspRule.getBody().add(literal2);

        return aspRule;
    }

    public AspRule noCNounAreCNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        wordDetector.removeWord(taggedWords,"no");

        String cNoun1 = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"are");

        String cNoun2 = wordDetector.getCNoun(taggedWords);

        String variable = "X";

        Literal literal1 = new Literal(cNoun2,true);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(cNoun1);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();

        aspRule.getHead().add(literal1);
        aspRule.getBody().add(literal2);

        return aspRule;
    }

    public AspRule noCNounAreAdjective(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        wordDetector.removeWord(taggedWords,"no");

        String cNoun = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"are");

        String adjective = wordDetector.getAdjectiveAndOrPreposition(taggedWords);

        String variable = "X";

        Literal literal1 = new Literal(adjective,true);
        literal1.getTerms().add(variable);

        Literal literal2 = new Literal(cNoun);
        literal2.getTerms().add(variable);

        AspRule aspRule = new AspRule();

        aspRule.getHead().add(literal1);
        aspRule.getBody().add(literal2);

        return aspRule;
    }

    public AspRule someCNounAreCNoun(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        wordDetector.removeWord(taggedWords,"some");

        String cNoun1 = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"are");

        boolean negated = wordDetector.isNegation(taggedWords);

        String cNoun2 = wordDetector.getCNoun(taggedWords);

        Literal literal;
        if(!negated) {
             literal = new Literal(String.format("#count{X:%s(X),%s(X)} = 0", cNoun1, cNoun2));
        }
        else {
            literal = new Literal(String.format("#count{X:%s(X),not %s(X)} = 0", cNoun1, cNoun2));
        }

        AspRule aspRule = new AspRule();

        aspRule.getBody().add(literal);

        return aspRule;
    }

    public AspRule someCNounAreAdjective(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {
        wordDetector.removeWord(taggedWords,"some");

        String cNoun = wordDetector.getCNoun(taggedWords);

        wordDetector.removeWord(taggedWords,"are");

        boolean negated = wordDetector.isNegation(taggedWords);

        String adjective = wordDetector.getAdjectiveAndOrPreposition(taggedWords);

        Literal literal;
        if(!negated) {
            literal = new Literal(String.format("#count{X:%s(X),%s(X)} = 0", cNoun, adjective));
        }
        else {
            literal = new Literal(String.format("#count{X:%s(X),not %s(X)} = 0", cNoun, adjective));
        }

        AspRule aspRule = new AspRule();

        aspRule.getBody().add(literal);

        return aspRule;
    }
}
