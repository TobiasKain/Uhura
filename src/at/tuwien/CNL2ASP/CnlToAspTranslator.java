package at.tuwien.CNL2ASP;

import at.tuwien.CNL2ASP.sentences.CategoricalPropositionSentences;
import at.tuwien.CNL2ASP.sentences.ComplexSentences;
import at.tuwien.CNL2ASP.sentences.DefaultSentences;
import at.tuwien.CNL2ASP.sentences.SimpleSentences;
import at.tuwien.entity.ManualTranslation;
import at.tuwien.entity.TranslationPattern;
import at.tuwien.entity.Word;
import at.tuwien.entity.asp.AspRule;
import at.tuwien.entity.asp.NewLine;
import at.tuwien.entity.asp.Translation;
import at.tuwien.nl2cln.NL2CNLTranslator;
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
    private CategoricalPropositionSentences categoricalPropositionSentences;
    private NL2CNLTranslator nl2CNLTranslator;
    private ManualTranslator manualTranslator;

    public CnlToAspTranslator(List<String> inputStrings, List<Word> directory, List<TranslationPattern> translationPatterns, List <ManualTranslation> manualTranslations) {
        this.inputStrings = inputStrings;

        WordDetector wordDetector = new WordDetector(directory);

        simpleSentences = new SimpleSentences(wordDetector);
        complexSentences = new ComplexSentences(wordDetector,this);
        defaultSentences = new DefaultSentences(wordDetector);
        categoricalPropositionSentences = new CategoricalPropositionSentences(wordDetector);
        nl2CNLTranslator = new NL2CNLTranslator(translationPatterns);
        manualTranslator = new ManualTranslator(manualTranslations);

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
                if(sentence.equals("\n")){
                    aspRules.add(new NewLine());
                } else if(!sentence.trim().startsWith("//") &&
                        !sentence.trim().startsWith("%")) {     // check if sentence is a comment

                    AspRule aspRule;
                    if((aspRule = manualTranslator.translate(sentence)) != null){
                        aspRules.add(aspRule);
                    }else {
                        aspRules.add(translateSentence(sentence));
                    }
                }
            } catch (SentenceValidationException e) {
                List<AspRule> result = null;
                try {
                    result = translateNlSentence(sentence);
                    if(result == null) {
                        errors.add(e.getMessage());
                    }else {
                        aspRules.addAll(result);
                    }
                } catch (SentenceValidationException e1) {
                    errors.add(String.format("Error in sentence \"%s\": %s", sentence, e1.getMessage()));
                }
            }
        }

        return translation;
    }

    private List<AspRule> translateNlSentence(String sentence) throws SentenceValidationException {
        List<AspRule> aspRules = new ArrayList<>();

        List<String> clnSentences = nl2CNLTranslator.translate(sentence);

        if(clnSentences.isEmpty())
        {
            return null;
        }

        for (String s: clnSentences) {
            try {
                aspRules.add(translateSentence(s));
                aspRules.add(new NewLine());
            } catch (SentenceValidationException e) {
                throw new SentenceValidationException(String.format("Error in translated CLN sentence \"%s\": %s",s,e.getMessage()));
            }
        }

        aspRules.remove(aspRules.size()-1); // remove last new line

        return aspRules;
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

        String originalSentence = sentence;
        sentence = sentence.toLowerCase();

        if (aspRule == null && sentence.matches("if .* then .*\\.$")){
            try {
                aspRule = complexSentences.ifThen((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"If SimpleSentence {and SimpleSentence} that SimpleSentence.");
            }
        }
        if (aspRule == null && sentence.matches("exclude that .*\\.$")){
            try {
                aspRule = complexSentences.excludeThat((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"Exclude that SimpleSentence {and that SimpleSentence}.");
            }
        }
        if (aspRule == null && sentence.matches(".* or .*\\.$")){
            try {
                aspRule = complexSentences.or((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"Fact {or Fact}.");
            }
        }
        /*if (aspRule == null && sentence.matches(".* is abnormal with respect to .*\\.$" ))
        {
            try {
                aspRule = defaultSentences.pNounIsAbnormalWithRespectToDefaultRuleTag((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = e.getMessage();
            }
        }*/
        if (aspRule == null && sentence.matches(".* normally are(n't | n't | not | ).*\\.$" ))
        {
            try {
                aspRule = defaultSentences.cNounNormallyAreAdjective((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"CNoun normally are [not] Adjective.");
            }
        }
        if (aspRule == null && sentence.matches(".* normally are(n't | n't | not | ).*\\.$" ))
        {
            try {
                aspRule = defaultSentences.cNounNormallyAreAdjectivePrepositionCNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"CNoun normally are [not] Adjective Preposition CNoun.");
            }
        }
        if (aspRule == null && sentence.matches(".* normally .*\\.$" ))
        {
            try {
                aspRule = defaultSentences.cNounNormallyVerb((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"CNoun normally [not] Verb.");
            }
        }
        if (aspRule == null && sentence.matches("all .* are .*\\.$" ))
        {
            try {
                aspRule = categoricalPropositionSentences.allCNounAreCNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"All CNoun are CNoun.");
            }
        }
        if (aspRule == null && sentence.matches("all .* are .*\\.$" ))
        {
            try {
                aspRule = categoricalPropositionSentences.allCNounAreAdjective((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"All CNoun are Adjective.");
            }
        }
        if (aspRule == null && sentence.matches("no .* are .*\\.$" ))
        {
            try {
                aspRule = categoricalPropositionSentences.noCNounAreCNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"No CNoun are CNoun.");
            }
        }
        if (aspRule == null && sentence.matches("no .* are .*\\.$" ))
        {
            try {
                aspRule = categoricalPropositionSentences.noCNounAreAdjective((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"No CNoun are Adjective.");
            }
        }
        if (aspRule == null && sentence.matches("some .* are(n't | n't | not | ).*\\.$" ))
        {
            try {
                aspRule = categoricalPropositionSentences.someCNounAreCNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"Some CNoun are [not] CNoun.");
            }
        }
        if (aspRule == null && sentence.matches("some .* are(n't | n't | not | ).*\\.$" ))
        {
            try {
                aspRule = categoricalPropositionSentences.someCNounAreAdjective((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"Some CNoun are [not] Adjective.");
            }
        }
        if (aspRule == null && sentence.matches("(a|an) .* is(n't | n't | not | )a .* of a .*\\.$" ))
        {
            try {
                aspRule = simpleSentences.aCNounVariableIsACNounOfACNounVariable((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"A CNoun Variable is [not] a CNoun of a Cnoun Variable.");
            }
        }
        if (aspRule == null && sentence.matches(".* is(n't | n't | not | ).* of .* [a-z] \\.$" ))
        {
            try {
                aspRule = simpleSentences.pNounIsCNounOfCNounVariable((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"PNoun is [not] [the] CNoun of [a] CNoun Variable.");
            }
        }
        if (aspRule == null && sentence.matches(".* is(n't | n't | not | ).* of .*\\.$" ))
        {
            try {
                aspRule = simpleSentences.pNounIsCNounOfPNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"PNoun is [not] [the] CNoun of [a] PNoun.");
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] is(n't | n't | not | ).* of .*\\.$" ))
        {
            try {
                aspRule = simpleSentences.cNounVariableIsCNounOfPNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"[A] CNoun Variable is [not] [the] CNoun of [a] PNoun.");
            }
        }
        if (aspRule == null && sentence.matches(".* (more|less) than .*\\.$")){       // documented
            try {
                aspRule = simpleSentences.cNounVariableVerbMoreLessThanNumberCNounVariable((ArrayList<TaggedWord>) taggedWords.clone(), parentSentence);
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"CNoun Variable Verb (more|less) than Number CNoun Variable.");
            }
        }
        if (aspRule == null && sentence.matches("there is(n't | n't | not | )(a|an) .* [a-z] \\.$"))    // documented
        {
            try {
                aspRule = simpleSentences.thereIsACNounVariable((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"There is [not] a CNoun Variable.");
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] is(n't | n't | not | ).* [a-z] \\.$")){   // documented x2
            try {
                aspRule = simpleSentences.cNounVariableIsAdjectivePrepositionCNounVariable((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"CNoun Variable is [not] [Adjective] Preposition CNoun Variable.");
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] is(n't | n't | not | )(a|an) .*\\.$")){   // documented x2
            try {
                aspRule = simpleSentences.cNounVariableIsACNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"A CNoun Variable is [not] a CNoun of a CNoun Variable.");
            }
        }
        if (aspRule == null && sentence.matches(".* is(n't | n't | not | ).* [a-z] \\.$")){   // documented x2
            try {
                aspRule = simpleSentences.pNounIsAdjectivePrepositionCNounVariable((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"PNoun is [not] [Adjective] Preposition CNoun Variable.");
            }
        }
        if (aspRule == null && sentence.matches(".* is(n't | n't | not | )(a|an) .*\\.$")) {    // documented x4
            try {
                aspRule = simpleSentences.pNounIsACNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"PNoun is [not] a CNoun.");
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] is(n't | n't | not | ).*\\.$")){     // documented x2
            try {
                aspRule = simpleSentences.cNounVariableIsAdjective((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"CNoun Variable is [not] Adjective.");
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] is(n't | n't | not | ).*\\.$")){     // documented x2
            try {
                aspRule = simpleSentences.cNounVariableIsAdjectivePrepositionPNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"CNoun Variable is [not] [Adjective] Preposition PNoun.");
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] is(n't | n't | not | ).*\\.$")){     // documented x2
            try {
                aspRule = simpleSentences.cNounVariableIsVerb((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"CNoun Variable is [not] Verb.");
            }
        }
        if (aspRule == null && sentence.matches(".* is(n't | n't | not | ).*\\.$")){       // documented x4
            try {
                aspRule = simpleSentences.pNounIsAdjective((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"(PNoun|Variable) is [not] Adjective.");
            }
        }
        if (aspRule == null && sentence.matches(".* is(n't | n't | not | ).*\\.$")){
            try {
                aspRule = simpleSentences.pNounIsAdjectivePrepositionPNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"PNoun is [not] [Adjective] Preposition PNoun.");
            }
        }
        if (aspRule == null && sentence.matches(".* is(n't | n't | not | ).*\\.$")){
            try {
                aspRule = simpleSentences.pNounIsVerb((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"PNoun is [not] Verb.");
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] .* (a|an) .* as .*\\.$")){    // documented x2
            try {
                aspRule = simpleSentences.cNounVariableVerbACNounAsPNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"[A] CNoun Variable [not] Verb a CNoun as [a] Pnoun.");
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] .* [a-z] \\.$")){   // documented
            try {
                aspRule = simpleSentences.cNounVariableVerbCNounVariable((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"[A] CNoun Variable [not] Verb [Preposition] [a] CNoun Variable.");
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] \\.$")){    // documented
            try {
                aspRule = simpleSentences.pNounVerbCNounVariable((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"PNoun [not] Verb [Preposition] CNoun Variable.");
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] .*\\.$")){    // documented
            try {
                aspRule = simpleSentences.cNounVariableVerb((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"CNoun Variable [not] Verb.");
            }
        }
        if (aspRule == null && sentence.matches(".* [a-z] .*\\.$")){    // documented
            try {
                aspRule = simpleSentences.cNounVariableVerbPnoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"CNoun Variable [not] Verb [Preposition] PNoun.");
            }
        }
        if (aspRule == null && sentence.matches(".* (a|an) .* as .*\\.$")){     // documented
            try {
                aspRule = simpleSentences.pNounVerbACNounAsPNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"[A] Pnoun [not] Verb a CNoun as [a] Pnoun.");
            }
        }
        if (aspRule == null && sentence.matches(".* .* .*\\.$")){    // documented
            try {
                aspRule = simpleSentences.pNounVerbPNoun((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"PNoun [not] Verb [Preposition] PNoun.");
            }
        }
        if (aspRule == null && sentence.matches(".* .*\\.$")){    // documented
            try {
                aspRule = simpleSentences.pNounVerb((ArrayList<TaggedWord>) taggedWords.clone());
            } catch (SentenceValidationException e) {
                if(error == null)
                    error = createErrorMessage(originalSentence,e.getMessage(),"PNoun [not] Verb.");
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

    private String createErrorMessage(String sentence, String errorMessage, String patternName) {
        return String.format("Error in sentence \"%s\":%n\t%s%n\t(detected sentence-pattern: '%s')", sentence, errorMessage.replaceAll("\\t","\t\t"), patternName);
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
