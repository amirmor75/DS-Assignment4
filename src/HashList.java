public class HashList {

    private HashListElement first;

    public HashList(){
        this.first=null;
    }

    public HashList(HashListElement first){
        this.first=first;
    }

    public void addfirst(int value){
        first=new HashListElement(value,first);
    }

    public HashListElement getFirst() {
        return first;
    }

    public void setFirst(HashListElement first) {
        this.first = first;
    }


}
