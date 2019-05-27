public class HashListElement {

    private HashListElement next;
    private int value;

    public HashListElement(int value,HashListElement next){
        this.value=value;
        this.next=next;
    }

    public int getValue() {
        return value;
    }

    public HashListElement getNext() {
        return next;
    }




}

