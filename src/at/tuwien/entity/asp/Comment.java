package at.tuwien.entity.asp;

/**
 * Created by tobiaskain on 19/05/2017.
 */
public class Comment extends AspRule {
    private String comment;

    public Comment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
