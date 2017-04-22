package at.tuwien.CNL2ASP;

import at.tuwien.CNL2ASP.sentences.ComplexSentences;
import at.tuwien.CNL2ASP.sentences.DefaultSentences;
import at.tuwien.CNL2ASP.sentences.SimpleSentences;
import at.tuwien.entity.Word;
import at.tuwien.entity.asp.AspRule;
import at.tuwien.entity.asp.Translation;
import edu.stanford.nlp.ling.TaggedWord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tobiaskain on 26/03/2017.
 */
public class CnlToAspTranslator {

    private List<String> inputStrings = new ArrayList<>();
    private SimpleSentences simpleSentences;
    private ComplexSentences complexSentences;
    private DefaultSentences defaultSentences;

    public CnlToAspTranslator(List<String> inputStrings, List<Word> directory) {
        this.inputStrings = inputStrings;

        WordDetector wordDetector = new WordDetector(directory);

        simpleSentences = new SimpleSentences(wordDetector);
        complexSentences = new ComplexSentences(wordDetector,this);
        defaultSentences = new DefaultSentences(wordDetector);
    }

    public Translation translate()
    {
        Translation translation = new Translation();

        List<AspRule> aspRules = new ArrayList<>();
        translation.setAspRules(aspRules);

        List<String> errors = new ArrayList<>();
        translation.setErrors(errors);

        for (String sentence: inputStrings) {
            try {
                if(!sentence.trim().startsWith("//") &&
                        !sentence.trim().startsWith("%")) {     // check if sentence is a comment
                    aspRules.add(translateSentence(sentence));
                }
            } catch (SentenceValidationException e) {
                errors.add(String.format("Error in sentence \"%s\": %s",sentence, e.getMessage()));
            }
        }

        return translation;
    }


    public AspRule translateSentence(String sentence) throws SentenceValidationException {
        return translateSentence(sentence,null);
    }

    public AspRule translateSentence(String sentence, ArrayList<TaggedWord> parentSentence) throws SentenceValidationException {

        String error = null;
        AspRule aspRule = null;

        sentence = addWhitespacesBeforeDot(sentence);
        sentence = sentence.trim();
        ArrayList<TaggedWord> taggedWords = StanfordParser.getInstance().parse(sentence).taggedYield();

        sentence = sentence.toLowerCase();

        if (aspRule == null && sentence.matches("if .* then .*\\.$")){
            try {
                aspRule = complexSentences.ifThen((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches("exclude that .*\\.$")){
            try {
                aspRule = complexSentences.excludeThat((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* or .*\\.$")){
            try {
                aspRule = complexSentences.or((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* is abnormal with respect to .*\\.$" ))
        {
            try {
                aspRule = defaultSentences.pNounIsAbnormalWithRespectToDefaultRuleTag((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* normally are(n't | n't | not | ).*\\.$" ))
        {
            try {
                aspRule = defaultSentences.cNounNormallyAreAdjective((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* normally are(n't | n't | not | ).*\\.$" ))
        {
            try {
                aspRule = defaultSentences.cNounNormallyAreAdjectivePrepositionCNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* normally .*\\.$" ))
        {
            try {
                aspRule = defaultSentences.cNounNormallyVerb((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches("a .* is(n't | n't | not | )a .* of a .*\\.$" ))
        {
            try {
                aspRule = simpleSentences.aCNounVariableIsACNounOfACNounVariable((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* is(n't | n't | not | ).* of .* [a-z] \\.$" ))
        {
            try {
                aspRule = simpleSentences.pNounIsCNounOfCNounVariable((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* is(n't | n't | not | ).* of .*\\.$" ))
        {
            try {
                aspRule = simpleSentences.pNounIsCNounOfPNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] is(n't | n't | not | ).* of .*\\.$" ))
        {
            try {
                aspRule = simpleSentences.cNounVariableIsCNounOfPNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* (more|less) than .*\\.$")){       // documented
            try {
                aspRule = simpleSentences.cNounVariableVerbMoreLessThanNumberCNounVariable((ArrayList<TaggedWord>) taggedWords.clone(), parentSentence);
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches("there is(n't | n't | not | )a .* [a-z] \\.$"))    // documented
        {
            try {
                aspRule = simpleSentences.thereIsACNounVariable((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] is(n't | n't | not | ).* [a-z] \\.$")){   // documented x2
            try {
                aspRule = simpleSentences.cNounVariableIsAdjectivePrepositionCNounVariable((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] is(n't | n't | not | )a .*\\.$")){   // documented x2
            try {
                aspRule = simpleSentences.cNounVariableIsACNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* is(n't | n't | not | ).* [a-z] \\.$")){   // documented x2
            try {
                aspRule = simpleSentences.pNounIsAdjectivePrepositionCNounVariable((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* is(n't | n't | not | )a .*\\.$")) {    // documented x4
            try {
                aspRule = simpleSentences.pNounIsACNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] is(n't | n't | not | ).*\\.$")){     // documented x2
            try {
                aspRule = simpleSentences.cNounVariableIsAdjective((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] is(n't | n't | not | ).*\\.$")){     // documented x2
            try {
                aspRule = simpleSentences.cNounVariableIsAdjectivePrepositionPNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] is(n't | n't | not | ).*\\.$")){     // documented x2
            try {
                aspRule = simpleSentences.cNounVariableIsVerb((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* is(n't | n't | not | ).*\\.$")){       // documented x4
            try {
                aspRule = simpleSentences.pNounIsAdjective((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* is(n't | n't | not | ).*\\.$")){
            try {
                aspRule = simpleSentences.pNounIsAdjectivePrepositionPNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* is(n't | n't | not | ).*\\.$")){
            try {
                aspRule = simpleSentences.pNounIsVerb((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] .* a .* as .*\\.$")){    // documented x2
            try {
                aspRule = simpleSentences.cNounVariableVerbACNounAsPNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] .* [a-z] \\.$")){   // documented
            try {
                aspRule = simpleSentences.cNounVariableVerbCNounVariable((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] \\.$")){    // documented
            try {
                aspRule = simpleSentences.pNounVerbCNounVariable((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] .*\\.$")){    // documented
            try {
                aspRule = simpleSentences.cNounVariableVerb((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] .*\\.$")){    // documented
            try {
                aspRule = simpleSentences.cNounVariableVerbPnoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* a .* as .*\\.$")){     // documented
            try {
                aspRule = simpleSentences.pNounVerbACNounAsPNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* .* .*\\.$")){    // documented
            try {
                aspRule = simpleSentences.pNounVerbPNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }
        if (aspRule == null && sentence.matches(".* .*\\.$")){    // documented
            try {
                aspRule = simpleSentences.pNounVerb((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }

        if(aspRule == null && error != null)
        {
            throw new SentenceValidationException(error);
        }

        if(aspRule == null){
            throw new SentenceValidationException("Sentence doesn't match any pattern.");
        }

        return aspRule;
    }

    /* Workaround for the following problem:
     * if a Variable is followed by a dot then
     * the parser tags the variable and the dot
     * together as NNP.
     * e.g "X." --> X./NNP
     */
    private String addWhitespacesBeforeDot(String sentence) {
        if(sentence.matches(".*\\.$"))
        {
            sentence = sentence.substring(0,sentence.lastIndexOf('.'));
            sentence = sentence + " .";
        }

        return sentence;
    }
}
