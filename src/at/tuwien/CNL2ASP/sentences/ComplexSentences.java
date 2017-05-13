package at.tuwien.CNL2ASP.sentences;

import at.tuwien.CNL2ASP.CnlToAspTranslator;
import at.tuwien.CNL2ASP.SentenceValidationException;
import at.tuwien.CNL2ASP.WordDetector;
import at.tuwien.entity.Word;
import at.tuwien.entity.asp.AspRule;
import edu.stanford.nlp.ling.TaggedWord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tobiaskain on 22/04/2017.
 */
public class ComplexSentences {

    private WordDetector wordDetector;
    private CnlToAspTranslator cnlToAspTranslator;

    public ComplexSentences(WordDetector wordDetector, CnlToAspTranslator cnlToAspTranslator) {
        this.wordDetector = wordDetector;
        this.cnlToAspTranslator = cnlToAspTranslator;
    }

    public AspRule excludeThat(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        ArrayList<TaggedWord> parentTaggedWords = new ArrayList<>();
        parentTaggedWords.addAll(taggedWords);

        AspRule aspRule = new AspRule();

        wordDetector.removeFirstWord(taggedWords);
        wordDetector.removeFirstWord(taggedWords);

        while (!taggedWords.get(0).value().equals(".")){
            String sentence = "";

            if(taggedWords.get(0).value().equals("and") && taggedWords.get(1).value().equals("that"))
            {
                wordDetector.removeFirstWord(taggedWords);
                wordDetector.removeFirstWord(taggedWords);
            }

            while (!taggedWords.get(0).value().equals(".") && (!taggedWords.get(0).value().equals("and") && !taggedWords.get(1).value().equals("that"))){
                sentence += taggedWords.get(0).value() + " ";
                taggedWords.remove(0);
            }
            sentence = sentence.trim() + ".";
            aspRule.getBody().addAll(cnlToAspTranslator.translateSentence(sentence, parentTaggedWords).getHead());
        }

        return aspRule;
    }

    public AspRule ifThen(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        ArrayList<TaggedWord> parentTaggedWords = new ArrayList<>();
        parentTaggedWords.addAll(taggedWords);

        AspRule aspRule = new AspRule();

        wordDetector.removeWord(taggedWords,"if");
        /* Body */
        while (!taggedWords.get(0).value().equals("then")){
            String sentence = "";
            if(taggedWords.get(0).value().equals("and"))
            {
                wordDetector.removeFirstWord(taggedWords);
            }
            while (!taggedWords.get(0).value().equals("and") && !taggedWords.get(0).value().equals("then")){
                sentence += taggedWords.get(0).value() + " ";
                taggedWords.remove(0);
            }
            sentence = sentence.trim() + ".";
            aspRule.getBody().addAll(cnlToAspTranslator.translateSentence(sentence, parentTaggedWords).getHead());
        }

        taggedWords.remove(0);

        /* Head */
        String sentence = "";
        while (!taggedWords.get(0).value().equals(".")){
            sentence += taggedWords.get(0).value() + " ";
            taggedWords.remove(0);
        }
        sentence = sentence.trim() + ".";

        AspRule headRule = cnlToAspTranslator.translateSentence(sentence, parentTaggedWords);
        aspRule.getHead().addAll(headRule.getHead());
        aspRule.setOr(headRule.isOr());

        return aspRule;
    }

    public AspRule or(ArrayList<TaggedWord> taggedWords) throws SentenceValidationException {

        ArrayList<TaggedWord> parentTaggedWords = new ArrayList<>();
        parentTaggedWords.addAll(taggedWords);

        AspRule aspRule = new AspRule();
        aspRule.setOr(true);

        while (!taggedWords.get(0).value().equals(".")){
            String sentence = "";

            if(taggedWords.get(0).value().equals("or"))
            {
                wordDetector.removeFirstWord(taggedWords);
            }

            while (!taggedWords.get(0).value().equals(".") && !taggedWords.get(0).value().equals("or")){
                sentence += taggedWords.get(0).value() + " ";
                taggedWords.remove(0);
            }
            sentence = sentence.trim() + ".";

            // TODO check if head is null
            aspRule.getHead().add(cnlToAspTranslator.translateSentence(sentence, parentTaggedWords).getHead().get(0));
        }

        return aspRule;
    }
}
