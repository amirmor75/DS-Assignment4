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
    public void setValue(int value) {
        this.value = value;
    }

    public HashListElement getNext() {
        return next;
    }

    public void setNext(HashListElement next) {
        this.next = next;
    }



}

