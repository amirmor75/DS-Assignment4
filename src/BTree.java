import java.io.BufferedReader;
import java.io.FileReader;

public class BTree{

    private int size;
    private int t;
    private BTreeNode root;

    public BTree(String tVal){
        this.t=Integer.parseInt(tVal);
        root=new BTreeNode(t);
        root.setNumOfKeys(0);
        size=0;
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
        size=size+1;
    }

    public String inOrder(){
        String inOrder="";
        if (root.getValues()[0]!=null)
            inOrder= root.inOrder(0);
        if (inOrder.length()>0&&inOrder.charAt(inOrder.length()-1)==',')
            inOrder=inOrder.substring(0,inOrder.length()-1);
        return inOrder;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public BTreeNode getRoot() {
        return root;
    }
    public void setRoot(BTreeNode root){
        this.root=root;
    }

    public void delete(String element){
        if (root.getNumOfKeys()!=0) {
            root.delete(element, this);
            //size=size-1;
        }
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

    public  void createFullTree(String filePath){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String str;
            while ((str=reader.readLine())!=null){
                this.insert(str);
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void  deleteKeysFromTree(String filePath){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String str;
            while ((str=reader.readLine())!=null){
                this.delete(str);
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return inOrder();
    }
}
