

public class BTreeNode{
    private String [] values;
    private int t;
    private BTreeNode[] children;
    private int numOfKeys;

    public BTreeNode(int t){
        if (t<0)
            throw new RuntimeException("t value must be positive!");
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
        if (element==null)
            throw new RuntimeException("null element entered");
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
    public int searchAtNode(String obj) {
        if (obj==null)
            throw new RuntimeException("null element entered");
        int i = 0;
        while (i < numOfKeys) {
            int cmp = obj.compareTo(values[i]);
            if (cmp == 0) {
                assert 0 <= i && i < numOfKeys;
                return i;  // Key found
            } else if (cmp > 0)
                i++;
            else  // cmp < 0
                break;
        }
        assert 0 <= i && i <= numOfKeys;
        return ~i;  // Not found, caller should recurse on child
    }

    public void splitChild(int i){ //split the i'th child, meaning the i-1 in the array
        BTreeNode y=this.children[i-1]; //the one we want to split
        BTreeNode z=new BTreeNode(y.t); // the new son in the height of y, we are targeting to move nodes to z,
        // while raising one to "this".
        z.numOfKeys=t-1; // one up, y will have t-1 as well as z.
        for (int j=0;j<t-1;j=j+1){ // taking the last t-1 keys from y(the one we want to split) and move them to z.
            z.values[j]=y.values[j+t];
        } //transfer done
        if (!y.isLeaf()){ //if y is not a leaf, we would like to copy his children to z as well.
            for (int j=0;j<t;j=j+1)
                z.children[j]=y.children[j+t];
        }// transfer done
        for (int j=this.numOfKeys;j>=i+1;j=j-1){ //now we want to update y's parent to point at the children we moved, by simply move each pointer to the next one.
            this.children[j+1]=this.children[j];
        }
        this.children[i]=z;
        for (int j=numOfKeys-1;j>=i;j=j-1) { //making room for the one extra node of y that went up
            values[j + 1] = values[j];
        }
        values[i-1]=y.values[t-1]; //inserting the node in the middle, the one we made room for.
        numOfKeys=numOfKeys+1; //updating this numofkeys.
        y.numOfKeys=t-1; //updating y numofkeys.
    }

    public void insertNonFull(String element){
        if (element==null||element.length()==0)
            throw new RuntimeException("null element entered or empty password");
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
        if (key==null)
            throw new RuntimeException("null element entered, or not String type");
        // Walk down the tree
        int size=tree.getSize();
        BTreeNode root=tree.getRoot();
        int index=root.searchAtNode(key);
        BTreeNode node = root;
        while (true) {
            assert node.numOfKeys<= 2*t-1;
            assert node == root || node.numOfKeys > t-1;
            if (node.isLeaf()) {
                if (index >= 0) {  // Simple removal from leaf
                    node.removeKeyAndChild(index, -1);
                    assert size > 0;
                    tree.setSize(tree.getSize()-1);
                    tree.setRoot(root);
                    return true;
                } else
                    return false;

            } else {  // Internal node
                if (index >= 0) {  // Key is stored at current node
                    BTreeNode left  = node.children[index];
                    BTreeNode right = node.children[index + 1];
                    assert left != null && right != null;
                    if (left.numOfKeys > t-1) {  // Replace key with predecessor
                        node.values[index] = left.removeMaximumVal();
                        assert size > 0;
                        tree.setSize(tree.getSize()-1);
                        tree.setRoot(root);
                        return true;
                    } else if (right.numOfKeys > t-1) {  // Replace key with successor
                        node.values[index] = right.removeMinimumVal();
                        assert size > 0;
                        tree.setSize(tree.getSize()-1);
                        tree.setRoot(root);
                        return true;
                    } else {  // Merge key and right node into left node, then recurse
                        node.mergeChildrenAt(index);
                        if (node == root && root.numOfKeys == 0) {
                            root = root.children[0];  // Decrement tree height
                            assert root != null;
                        }
                        node = left;
                        index = t-1;  // Index known due to merging; no need to search
                    }

                } else {  // Key might be found in some child
                    BTreeNode child = node.safeChildRemove(~index);
                    if (node == root && root.numOfKeys == 0) {
                        root = root.children[0];  // Decrement tree height
                        assert root != null;
                    }
                    node = child;
                    index=node.searchAtNode(key);
                }
            }
        }
    }

    public String removeKeyAndChild(int keyIndex, int childIndex) {
        assert 1 <= numOfKeys && numOfKeys <= values.length;
        assert 0 <= keyIndex && keyIndex < numOfKeys;

        // Handle children array
        if (isLeaf())
            assert childIndex == -1;
        else {
            assert 0 <= childIndex && childIndex <= numOfKeys;
            assert children[childIndex] != null;
            System.arraycopy(children, childIndex + 1, children, childIndex, numOfKeys - childIndex);
            children[numOfKeys] = null;
        }

        // Handle keys array
        String result =values[keyIndex];
        assert result != null;
        System.arraycopy(values, keyIndex + 1,values, keyIndex, numOfKeys - 1 - keyIndex);
        values[numOfKeys - 1] = null;
        numOfKeys=numOfKeys-1;
        return result;
    }

    public BTreeNode safeChildRemove(int index) {
        // Preliminaries
        assert !this.isLeaf() && 0 <= index && index <= this.numOfKeys;
        BTreeNode child = children[index];
        if (child.numOfKeys > t-1)  // Already satisfies the condition
            return child;
        assert child.numOfKeys == t-1;

        // Get siblings
        BTreeNode left = index >= 1 ? this.children[index - 1] : null;
        BTreeNode right = index < this.numOfKeys ? this.children[index + 1] : null;
        boolean internal = !child.isLeaf();
        assert left != null || right != null;  // At least one sibling exists because degree >= 2
        assert left  == null || left .isLeaf() != internal;  // Sibling must be same type (internal/leaf) as child
        assert right == null || right.isLeaf() != internal;  // Sibling must be same type (internal/leaf) as child

        if (left != null && left.numOfKeys > t-1) {  // Steal rightmost item from left sibling
            child.insertKeyAndChildAt(0, this.values[index - 1],
                    (internal ? 0 : -1), (internal ? left.children[left.numOfKeys] : null));
            this.values[index - 1] = left.removeKeyAndChild(left.numOfKeys - 1, (internal ? left.numOfKeys : -1));
            return child;
        } else if (right != null && right.numOfKeys > t-1) {  // Steal leftmost item from right sibling
            child.insertKeyAndChildAt(child.numOfKeys, this.values[index],
                    (internal ? child.numOfKeys + 1 : -1), (internal ? right.children[0] : null));
            this.values[index] = right.removeKeyAndChild(0, (internal ? 0 : -1));
            return child;
        } else if (left != null) {  // Merge child into left sibling
            this.mergeChildrenAt(index - 1);
            return left;  // This is the only case where the return value is different
        } else if (right!= null) {  // Merge right sibling into child
            this.mergeChildrenAt(index);
            return child;
        } else
            throw new AssertionError("Impossible condition");
    }


    // Merges the child node at index+1 into the child node at index,
    // assuming the current node is not empty and both children have minkeys.
    public void mergeChildrenAt(int index) {
        assert !this.isLeaf() && 0 <= index && index < this.numOfKeys;
        BTreeNode left  = children[index];
        BTreeNode right = children[index + 1];
        assert left.numOfKeys == t-1 && right.numOfKeys == t-1;
        if (!left.isLeaf())
            System.arraycopy(right.children, 0, left.children, t, t);
        left.values[t-1] = removeKeyAndChild(index, index + 1);
        System.arraycopy(right.values, 0, left.values,  t, t-1);
        left.numOfKeys = 2*t-1;
    }


    // Removes and returns the minimum key among the whole subtree rooted at this node.
    // Requires this node to be preprocessed to have at least minKeys+1 keys.
    public String removeMinimumVal() {
        for (BTreeNode node = this; ; ) {
            assert node.numOfKeys > t-1;
            if (node.isLeaf())
                return node.removeKeyAndChild(0, -1);
            else
                node = node.safeChildRemove(0);
        }
    }


    // Removes and returns the maximum key among the whole subtree rooted at this node.
    // Requires this node to be preprocessed to have at least minKeys+1 keys.
    public String removeMaximumVal() {
        for (BTreeNode node = this; ; ) {
            assert node.numOfKeys > t-1;
            if (node.isLeaf())
                return node.removeKeyAndChild(node.numOfKeys - 1, -1);
            else
                node = node.safeChildRemove(node.numOfKeys);
        }
    }
    public void insertKeyAndChildAt(int keyIndex, String key, int childIndex, BTreeNode child) {
        assert 0 <= numOfKeys && numOfKeys < 2*t-1 && key != null;
        assert 0 <= keyIndex && keyIndex <= numOfKeys;

        // Handle children array
        if (isLeaf())
            assert childIndex == -1 && child == null;
        else {
            assert 0 <= childIndex && childIndex <= numOfKeys + 1 && child != null;
            System.arraycopy(children, childIndex, children, childIndex + 1, numOfKeys + 1 - childIndex);
            children[childIndex] = child;
        }

        // Handle keys array
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
