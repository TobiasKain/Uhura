package at.tuwien.entity;

/**
 * Created by tobiaskain on 23/04/2017.
 */
public class TranslationPattern {
    private String nlSentence;
    private String regexPattern;
    private String translation;

    public String getNlSentence() {
        return nlSentence;
    }

    public void setNlSentence(String nlSentence) {
        this.nlSentence = nlSentence;
    }

    public String getRegexPattern() {
        return regexPattern;
    }

    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
