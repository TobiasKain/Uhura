package at.tuwien.entity;

/**
 * Created by tobiaskain on 23/04/2017.
 */
public class ManualTranslation {
    private long manualTranslationId;
    private String cnlSentence;
    private String aspRule;

    public long getManualTranslationId() {
        return manualTranslationId;
    }

    public void setManualTranslationId(long manualTranslationId) {
        this.manualTranslationId = manualTranslationId;
    }

    public String getCnlSentence() {
        return cnlSentence;
    }

    public void setCnlSentence(String cnlSentence) {
        this.cnlSentence = cnlSentence;
    }

    public String getAspRule() {
        return aspRule;
    }

    public void setAspRule(String aspRule) {
        this.aspRule = aspRule;
    }
}
