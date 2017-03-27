package at.tuwien.ASP;

import java.util.ArrayList;

/**
 * Created by tobiaskain on 26/03/2017.
 */
public class AspRule {

    private ArrayList<Literal> head;
    private ArrayList<Literal> body;


    public AspRule() {
        head = new ArrayList<>();
        body = new ArrayList<>();
    }

    public AspRule(ArrayList<Literal> head, ArrayList<Literal> body) {
        this.head = head;
        this.body = body;
    }

    public ArrayList<Literal> getHead() {
        return head;
    }

    public void setHead(ArrayList<Literal> head) {
        this.head = head;
    }

    public ArrayList<Literal> getBody() {
        return body;
    }

    public void setBody(ArrayList<Literal> body) {
        this.body = body;
    }
}
