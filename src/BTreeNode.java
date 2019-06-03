

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
    public int searchAtNode(String element) {
        int i = 0;
        while (i < numOfKeys) {
            int cmp = element.compareTo(values[i]);
            if (cmp == 0) {
                return i;  // Key found
            } else if (cmp > 0)
                i++;
            else  // cmp < 0
                break;
        }
        return -i-1;  // didn't find, need to search in children
    }

    public void splitChild(int i){ //split the i'th child, meaning the i-1 in the array
        BTreeNode y=this.children[i-1]; //the one we want to split
        BTreeNode z=new BTreeNode(y.t); // the new son in the height of y, we are targeting to move nodes to z,
        // while raising one to "this".
        z.numOfKeys=t-1; // one up, y will have t-1 as well as z.
        for (int j=0;j<t-1;j=j+1){ // taking the last t-1 keys from y(the one we want to split) and move them to z.
            System.arraycopy(y.values,t,z.values,0,t-1);
        } //transfer done
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
    //deletion methods -start- ------------------------------------//
    public boolean delete(String key,BTree tree) {

        BTreeNode root=tree.getRoot();
        int index=root.searchAtNode(key); //if index>=0, we are at the target node.
        BTreeNode node = root; // the node we use to go sown the tree
        while (true) {
            if (node.isLeaf()) {
                if (index >= 0) {  // simple removal from leaf
                    node.removeKeyAndChild(index, -1);
                    tree.setSize(tree.getSize()-1);
                    tree.setRoot(root);
                    return true;
                } else
                    return false;

            } else {  // target is an internal node
                if (index >= 0) {  // key at current node
                    BTreeNode left  = node.children[index];
                    BTreeNode right = node.children[index + 1];
                    if (left.numOfKeys > t-1) {  // replace key with predecessor
                        node.values[index] = left.removeMaximumVal();
                        tree.setSize(tree.getSize()-1);
                        tree.setRoot(root);
                        return true;
                    } else if (right.numOfKeys > t-1) {  // replace key with successor
                        node.values[index] = right.removeMinimumVal();
                        tree.setSize(tree.getSize()-1);
                        tree.setRoot(root);
                        return true;
                    } else {  // merge key and right node into left node, then recurse
                        node.mergeChildrenAt(index);
                        if (node == root && root.numOfKeys == 0) {
                            root = root.children[0];  // tree height goes down
                        }
                        node = left;
                        index = t-1;  // we know the index because we merged
                    }

                } else {  // key can be found at children
                    BTreeNode child = node.safeChildRemove(-index-1);
                    if (node == root && root.numOfKeys == 0) {
                        root = root.children[0];  // tree height goes down
                    }
                    node = child;
                    index=node.searchAtNode(key);
                }
            }
        }
    }


    public String removeKeyAndChild(int keyIndex, int childIndex) {
        // deals with children
        if (!isLeaf()) {
            System.arraycopy(children, childIndex + 1, children, childIndex, numOfKeys - childIndex);
            children[numOfKeys] = null;
        }

        // deal values
        String output =values[keyIndex];
        System.arraycopy(values, keyIndex + 1,values, keyIndex, numOfKeys - 1 - keyIndex);
        values[numOfKeys - 1] = null;
        numOfKeys=numOfKeys-1;
        return output;
    }

    public BTreeNode safeChildRemove(int index) {

        BTreeNode child = children[index];
        if (child.numOfKeys > t-1)  //child satisfies condition
            return child;

        // take from siblings
        BTreeNode left=null;
        if (index>=1)
            left=this.children[index - 1];
        BTreeNode right =null;
        if (index<this.numOfKeys)
            right=this.children[index + 1];
        boolean internal = !child.isLeaf(); //indicates weather child is internal node

        if (left != null && left.numOfKeys > t-1) {  // steal rightmost item from left sibling
            return stealRightMost(child,internal,left,index);
        } else if (right != null && right.numOfKeys > t-1) {  // steal leftmost item from right sibling
            return stealLeftMost(child,internal,right,index);
        } else if (left != null) {  // merges child into left sibling
            this.mergeChildrenAt(index - 1);
            return left;
        } else if (right!= null) {  // merges right sibling into child
            this.mergeChildrenAt(index);
            return child;
        } else
            throw new RuntimeException("Something went wrong, case impossible");
    }

    public BTreeNode stealRightMost(BTreeNode child,boolean internal,BTreeNode left,int index){
        if (internal){
            child.insertKeyAndChildAt(0, this.values[index - 1],
                     0,left.children[left.numOfKeys]);
            this.values[index - 1] = left.removeKeyAndChild(left.numOfKeys - 1,left.numOfKeys);
        }
        else {
            child.insertKeyAndChildAt(0, this.values[index - 1],
                    -1, null);
            this.values[index - 1] = left.removeKeyAndChild(left.numOfKeys - 1, -1);
        }
        return child;
    }
    public BTreeNode stealLeftMost(BTreeNode child,boolean internal,BTreeNode right,int index){
        if (internal){
            child.insertKeyAndChildAt(child.numOfKeys, this.values[index],
                     child.numOfKeys + 1 , right.children[0]);
            this.values[index] = right.removeKeyAndChild(0,  0 );
        }
        else {
            child.insertKeyAndChildAt(child.numOfKeys, this.values[index],
                     -1, null);
            this.values[index] = right.removeKeyAndChild(0, -1);
        }

        return child;
    }

    // merges right into left, assumption : left and right have t keys.
    public void mergeChildrenAt(int index) {
        BTreeNode left  = children[index];
        BTreeNode right = children[index + 1];
        if (!left.isLeaf())
            System.arraycopy(right.children, 0, left.children, t, t);
        left.values[t-1] = removeKeyAndChild(index, index + 1);
        System.arraycopy(right.values, 0, left.values,  t, t-1);
        left.numOfKeys = 2*t-1;
    }


    // returns minimum value in the subtree, assumption : owns t keys.
    public String removeMinimumVal() {
        for (BTreeNode node = this; ; ) {
            if (node.isLeaf())
                return node.removeKeyAndChild(0, -1);
            else
                node = node.safeChildRemove(0);
        }
    }

    // returns maximum value in the subtree, assumption : owns t keys.
    public String removeMaximumVal() {
        for (BTreeNode node = this; ; ) {
            if (node.isLeaf())
                return node.removeKeyAndChild(node.numOfKeys - 1, -1);
            else
                node = node.safeChildRemove(node.numOfKeys);
        }
    }

    public void insertKeyAndChildAt(int keyIndex, String key, int childIndex, BTreeNode child) {
        // deal with children
        if (!isLeaf()){
            System.arraycopy(children, childIndex, children, childIndex + 1, numOfKeys + 1 - childIndex);
            children[childIndex] = child;
        }

        // deal with values
        System.arraycopy(values, keyIndex, values, keyIndex + 1, numOfKeys - keyIndex);
        values[keyIndex] = key;
        numOfKeys=numOfKeys+1;
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
