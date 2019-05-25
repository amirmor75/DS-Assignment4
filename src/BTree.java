import java.io.BufferedReader;
import java.io.FileReader;

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
        if (root.getValues()[0]!=null)
            return root.getValues()[0]+"_0, "+root.inOrder(0);
        return "";
    }

    public String getSearchTime(String filePath){
        double start=System.nanoTime()/1000000.0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String str;
            while ((str=reader.readLine())!=null){
                this.search(str);
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        double end=System.nanoTime()/1000000.0;
        String output=String.valueOf(end-start);
        int indexOfDot=output.indexOf('.');
        output=output.substring(0,indexOfDot+5);
        return output;
    }

    public  void createFullTree(String target){

    }

    public void  deleteKeysFromTree(String target){

    }

    @Override
    public String toString() {
        return inOrder();
    }
}
