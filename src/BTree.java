public class BTree<String>{

    private int t;
    BTreeNode<String> root;

    public BTree(int t){
        this.t=t;
        root=null;
    }

    public String search(String element){
        if (root==null)
            return null;
        return root.search(element);
    }

    public void insert(String element){
        if (root!=null)
            root.insert(element);
        root=new BTreeNode<>(element,null,t);
    }

}
