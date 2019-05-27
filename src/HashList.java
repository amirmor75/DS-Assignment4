public class HashList {

    private HashListElement first;

    public HashList(){
        this.first=null;
    }

    public void addfirst(int value){
        HashListElement temp=first;
        first=new HashListElement(value,temp);
    }

    public boolean contains(int key){
        HashListElement pos=first;
        while (pos!=null) {
            if (pos.getValue() == key)
                return true;
            pos=pos.getNext();

        }
        return false;
    }


}
