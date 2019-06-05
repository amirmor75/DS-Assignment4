

public class BTreeNode{
    private String [] values;
    private int t;
    private BTreeNode[] children;
    private int numOfKeys;

    public BTreeNode(int t){
        this.values=new String[2*t-1];
        for (int i=0;i<values.length;i=i+1)
            values[i]=null;
        this.t=t;
        numOfKeys=0;
        children=new BTreeNode[2*t];
        for (int i=0;i<children.length;i=i+1)
            children[i]=null;
    }

    public BTreeNode search(String element){
        int i=0;
        while (i<this.numOfKeys&&element.compareTo(values[i])>0){
            i=i+1;
        }
        if (i<numOfKeys&element.equals(values[i])){
            return this;
        }
        else if (isLeaf()){
            return null;
        }
        else return children[i].search(element);
    }

    public  boolean isLeaf(){

        for (BTreeNode bt : children){
            if (bt!=null)
                return false;
        }
        return true;
    }

    public void splitChild(int i){ //split the i'th child, meaning the i-1 in the array
        BTreeNode y=this.children[i-1]; //the one we want to split
        BTreeNode z=new BTreeNode(y.t); // the new son in the height of y, we are targeting to move nodes to z,
        // while raising one to "this".
        z.numOfKeys=t-1; // one up, y will have t-1 as well as z.
        // taking the last t-1 keys from y(the one we want to split) and move them to z.
        System.arraycopy(y.values,t,z.values,0,t-1);
        if (!y.isLeaf()){ //if y is not a leaf, we would like to copy his children to z as well.
            System.arraycopy(y.children,t,z.children,0,t);
        }// transfer done
        for (int j=this.numOfKeys;j>=i;j=j-1){ //now we want to update y's parent to point at the children we moved, by simply move each pointer to the next one.
            this.children[j+1]=this.children[j];
        }

        this.children[i]=z;
        for (int j=numOfKeys-1;j>=i-1;j=j-1) { //making room for the one extra node of y that went up
            values[j + 1] = values[j];
        }
        values[i-1]=y.values[t-1]; //inserting the node in the middle, the one we made room for.
        numOfKeys=numOfKeys+1; //updating this numofkeys.
        y.numOfKeys=t-1; //updating y numofkeys.
    }

    public void insertNonFull(String element){
        element=element.toLowerCase(); //considering small letters only
        int i=numOfKeys-1;
        if (isLeaf()){
            while (i>=0&&element.compareTo(values[i])<0){
                values[i+1]=values[i];
                i=i-1;
            }
            values[i+1]=element;
            numOfKeys=numOfKeys+1;
        }
        else {
            while (i>=0&&element.compareTo(values[i])<0)
                i=i-1;
            i=i+1;
            if (children[i].numOfKeys==2*t-1){
                this.splitChild(i+1);
                if (element.compareTo(this.values[i])>0)
                    i=i+1;
            }
            children[i].insertNonFull(element);
        }
    }

    public String inOrder(int depth){
        String result = "";
        for (int i=0;i<numOfKeys;i=i+1){
            if (children[i]!=null)
                result=result+children[i].inOrder(depth+1);
            result=result+values[i]+"_"+depth+",";
            if(i==numOfKeys-1&&children[i+1]!=null) {
                result = result + children[i + 1].inOrder(depth + 1);
            }
        }
        return result;
    }
    //deletion start//
    public void delete(String element,BTree tree){
        BTreeNode target=tree.search(element);
        if (target.isLeaf()&&target.numOfKeys>=t)
            target.safeDeletionLeaf(element);
        //couldn't implement correctly ..
    }

    public void safeDeletionLeaf(String key){
        int indexChanged=0;
        for (int i=0;i<numOfKeys;i=i+1){
            if (values[i].equals(key)){
                values[i]=null;
                indexChanged=i;
                break;
            }
        }
        for (int i=indexChanged;i<numOfKeys-1;i=i+1){
            values[i]=values[i+1];
        }
        values[numOfKeys]=null;

    }
    //---------------end-----------------//

    public int getNumOfKeys() {
        return numOfKeys;
    }

    public void setNumOfKeys(int numOfKeys) {
        this.numOfKeys = numOfKeys;
    }

    public BTreeNode[] getChildren() {
        return children;
    }

    public String[] getValues() {
        return values;
    }
}
