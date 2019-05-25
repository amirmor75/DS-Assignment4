public class BTree{

    private int t;
    BTreeNode root;

    public BTree(java.lang.String tVal){
        this.t=Integer.parseInt(tVal);
        root=new BTreeNode(t);
        root.setNumOfKeys(0);
    }

    public BTreeNode search(String element){
        return root.search(element);
    }

    public void insert(String element){
        BTreeNode r=this.root;
        if (r.getNumOfKeys()==2*t-1){ //root is full
            BTreeNode s=new BTreeNode(t);
            root=s;
            s.setNumOfKeys(0);
            s.getChildren()[0]=r;
            s.splitChild(1);
            s.insertNonFull(element);
        }
        else r.insertNonFull(element);
    }

    public String inOrder(){
        return root.getValues()[0]+"_0, "+root.inOrder(0);
    }



}
