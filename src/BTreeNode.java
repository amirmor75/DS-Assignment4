public class BTreeNode<String> {
    private String value;
    private BTreeNode<String> parent;
    private BTreeNode<String> left;
    private BTreeNode<String> right;
    private int t;
    private String[] keys;
    private int numOfKeys;


    public BTreeNode(String value,BTreeNode<String> parent,int t){
        this.value=value;
        this.parent=parent;
        left=null;
        right=null;
        this.t=t;
        numOfKeys=0;
    }

    public String search(String element){
        if (this.value.equals(element))
            return value;
        if (left!=null) {
            String leftSearch=left.search(element);
            if (leftSearch!=null)
                return leftSearch;
        }
        if (right!=null)
            return right.search(element);
        return null;
    }
    public  boolean isLeaf(){
        return left==null&right==null;
    }
    public void insert(String element){
        if (isLeaf()&numOfKeys<keys.length) {
            keys[numOfKeys] = element;
            numOfKeys=numOfKeys+1;
        }
        else {
            if (left!=null){

            }

        }

    }

}
