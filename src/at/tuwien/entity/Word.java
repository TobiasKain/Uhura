package at.tuwien.entity;

/**
 * Created by tobiaskain on 21/04/2017.
 */
public class Word {
    private long wordId;
    private String word;
    private WordType wordType;

    public long getWordId() {
        return wordId;
    }

    public void setWordId(long wordId) {
        this.wordId = wordId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public WordType getWordType() {
        return wordType;
    }

    public void setWordType(WordType wordType) {
        this.wordType = wordType;
    }
}
