package at.tuwien.service.impl;

import at.tuwien.dao.DaoException;
import at.tuwien.dao.TranslationPatternDAO;
import at.tuwien.dao.impl.JDBCTranslationPatternDAO;
import at.tuwien.entity.TranslationPattern;
import at.tuwien.service.ITranslationPatternService;
import edu.stanford.nlp.ling.TaggedWord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tobiaskain on 23/04/2017.
 */
public class TranslationPatternService implements ITranslationPatternService {

    private TranslationPatternDAO translationPatternDAO;

    public TranslationPatternService() throws DaoException {
        translationPatternDAO = new JDBCTranslationPatternDAO();
    }

    @Override
    public void addTranslationPattern(TranslationPattern translationPattern) throws DaoException {

        translationPattern.setRegexPattern(createRegexPattern(translationPattern.getNlSentence()));

        translationPatternDAO.createTranslationPattern(translationPattern);
    }

    @Override
    public void deleteTranslationPattern(TranslationPattern translationPattern) throws DaoException {
        translationPatternDAO.deleteTranslationPattern(translationPattern);
    }

    @Override
    public List<TranslationPattern> getAllTranslationPatterns() throws DaoException {
        return translationPatternDAO.readAllTranslationPatterns();
    }

    private String createRegexPattern(String nlSentence){
        nlSentence = nlSentence.replace("."," . ");
        nlSentence = nlSentence.replace(","," , ");

        List<String> sentenceParts = new LinkedList<String>(Arrays.asList(nlSentence.split("\\s+")));
        List<String> regexParts = new ArrayList<>();

        for (String part:sentenceParts) {
            if(isVariable(part)){
                regexParts.add(".*");
            }
            else if(part.equals("."))
            {
                regexParts.add("\\.$");
            }
            else {
                regexParts.add(part);
            }
        }

        return getSentence(regexParts);
    }

    private boolean isVariable(String str) {
        if(str.length() == 1 &&
                str.equals(str.toUpperCase()) && str.matches("[A-Z]")) {
            return true;
        }

        return false;
    }

    public String getSentence(List<String> sentenceParts) {
        String sentence = "";

        for (String part: sentenceParts) {
            if(part.matches("(,|:|.)")){
                sentence +=  part;
            }
            else {
                sentence += " " + part;
            }
        }

        return sentence.trim();
    }
}
