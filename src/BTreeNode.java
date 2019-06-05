

public class BTreeNode {
    private String[] values;
    private int t;
    private BTreeNode[] children;
    private int numOfKeys;

    public BTreeNode(int t) {
        this.values = new String[2 * t - 1];
        for (int i = 0; i < values.length; i = i + 1)
            values[i] = null;
        this.t = t;
        numOfKeys = 0;
        children = new BTreeNode[2 * t];
        for (int i = 0; i < children.length; i = i + 1)
            children[i] = null;
    }

    public BTreeNode search(String element) {
        int i = 0;
        while (i < this.numOfKeys && element.compareTo(values[i]) > 0) {
            i = i + 1;
        }
        if (i < numOfKeys & element.equals(values[i])) {
            return this;
        } else if (isLeaf()) {
            return null;
        } else return children[i].search(element);
    }


    public boolean isLeaf() {

        for (BTreeNode bt : children) {
            if (bt != null)
                return false;
        }
        return true;
    }

    public int searchAtNode(String obj) {
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

    public void splitChild(int i) { //split the i'th child, meaning the i-1 in the array
        BTreeNode y = this.children[i - 1]; //the one we want to split
        BTreeNode z = new BTreeNode(y.t); // the new son in the height of y, we are targeting to move nodes to z,
        // while raising one to "this".
        z.numOfKeys = t - 1; // one up, y will have t-1 as well as z.
        for (int j = 0; j < t - 1; j = j + 1) { // taking the last t-1 keys from y(the one we want to split) and move them to z.
            z.values[j] = y.values[j + t];
        } //transfer done
        if (!y.isLeaf()) { //if y is not a leaf, we would like to copy his children to z as well.
            for (int j = 0; j < t; j = j + 1)
                z.children[j] = y.children[j + t];
        }// transfer done
        for (int j = this.numOfKeys; j >= i; j = j - 1) { //now we want to update y's parent to point at the children we moved, by simply move each pointer to the next one.
            this.children[j + 1] = this.children[j];
        }
        this.children[i] = z;
        for (int j = numOfKeys - 1; j >= i - 1; j = j - 1) { //making room for the one extra node of y that went up
            values[j + 1] = values[j];
        }
        values[i - 1] = y.values[t - 1]; //inserting the node in the middle, the one we made room for.
        numOfKeys = numOfKeys + 1; //updating this numofkeys.
        y.numOfKeys = t - 1; //updating y numofkeys.
    }

    public void insertNonFull(String element) {
        element = element.toLowerCase(); //considering small letters only
        int i = numOfKeys - 1;
        if (isLeaf()) {
            while (i >= 0 && element.compareTo(values[i]) < 0) {
                values[i + 1] = values[i];
                i = i - 1;
            }
            values[i + 1] = element;
            numOfKeys = numOfKeys + 1;
        } else {
            while (i >= 0 && element.compareTo(values[i]) < 0)
                i = i - 1;
            i = i + 1;
            if (children[i].numOfKeys == 2 * t - 1) {
                this.splitChild(i + 1);
                if (element.compareTo(this.values[i]) > 0)
                    i = i + 1;
            }
            children[i].insertNonFull(element);
        }
    }

    public String inOrder(int depth) {
        String result = "";
        for (int i = 0; i < numOfKeys; i = i + 1) {
            if (children[i] != null)
                result = result + children[i].inOrder(depth + 1);
            result = result + values[i] + "_" + depth + ",";
            if (i == numOfKeys - 1 && children[i + 1] != null) {
                result = result + children[i + 1].inOrder(depth + 1);
            }
        }
        return result;
    }
    //start of amir deletion

    public boolean delete(String key, BTree tree) {
        // Walk down the tree
        BTreeNode keyNode = tree.search(key);
        BTreeNode fatherOfNode=searchFather(key,tree.getRoot());
        if (keyNode == null)
            return false; //from now on we know key exists
        int keyIndex = keyNode.searchIndex(keyNode, key);
        if (keyNode.isLeaf()) {
            if (keyNode.numOfKeys >= t) {
                safeDeletion(keyNode,keyIndex);
            } else {// num of keys is t-1
                BTreeNode leftBro=getLeftBrother(fatherOfNode,keyNode);
                BTreeNode rightBro=getRightBrother(fatherOfNode,keyNode);
                boolean leftBroNull=leftBro==null;
                boolean rightBroNull=rightBro==null;
                if ((!rightBroNull && rightBro.numOfKeys>=t) | (!leftBroNull && leftBro.numOfKeys>=t) )//checks if one of brothers is >=t
                {//takes smallest of biggest and gives father or opposite
                    if(!rightBroNull && rightBro.numOfKeys>=t){
                        caseOneRightBrother(keyNode,fatherOfNode,rightBro,key);
                    }
                    else{//!leftBroNull && leftBro.numOfKeys>=t
                        caseOneLeftBrother(keyNode,fatherOfNode,leftBro,key);
                    }
                }
                else {
                    // both brothers are t-1 or one doesn't exist
                    //merges
                    if (!rightBroNull)
                        caseOneMergeRightBrother(keyNode,rightBro,fatherOfNode,keyIndex);
                    else
                        caseOneMergeLeftBrother(keyNode,leftBro,fatherOfNode,keyIndex);
                }
            }
        }
        else
        {//internal node
            //takes predecessor or successor and replace with key
            BTreeNode predecessor=predecessor(keyNode,keyIndex);
            BTreeNode successor=successor(keyNode,keyIndex);
            if (predecessor.numOfKeys>=t | successor.numOfKeys>=t) //checks if one of sons has >=t keys
            {
                if (predecessor.numOfKeys>=t)
                    caseTwoDeleteInternalPredecessor(keyNode,keyIndex,successor);
                else// successor has >=t keys
                    caseTwoDeleteInternalSuccessor(keyNode,keyIndex,successor);
            }
            else
            {// both predecessor and successor are t-1
                BTreeNode preFather=searchFather(predecessor.values[0],tree.getRoot());
                int rightPreFatherIndex=rightFatherKeyIndex(preFather,predecessor);
                if (preFather.numOfKeys>=t)
                {

                }
                else// father has t-1 keys
                {
                    BTreeNode leftPreBrother=getLeftBrother(preFather,predecessor);
                    if(leftPreBrother!=null) {
                        if (leftPreBrother.numOfKeys >= t) {

                        } else// left brother has t-1 keys
                        {

                        }
                    }
                    else//left brother is null
                    {
                        BTreeNode sucFather=searchFather(successor.values[0],tree.getRoot());
                        BTreeNode rightSucBrother=getRightBrother(sucFather,successor);
                        if (rightSucBrother!=null)
                        {
                            if(rightSucBrother.numOfKeys>=t)
                            {

                            }
                        }
                        else
                        {

                        }
                    }
                }
            }
        }
        return false;
    }
    public void caseTwoDeleteInternalSuccessor(BTreeNode keyNode,int keyIndex,BTreeNode successor){
        keyNode.values[keyIndex]=successor.values[0];
        successor.values[0]=null;
        for (int i = 0; i <successor.numOfKeys ; i++)
            successor.values[i]=successor.values[i+1];
    }
    public void caseTwoDeleteInternalPredecessor(BTreeNode keyNode,int keyIndex,BTreeNode predecessor){
        keyNode.values[keyIndex]=predecessor.values[predecessor.numOfKeys-1];
        predecessor.values[predecessor.numOfKeys-1]=null;
    }
    public BTreeNode predecessor(BTreeNode keyNode,int keyIndex){
        BTreeNode tempLeftSon=keyNode.children[keyIndex];//left son
        while (!tempLeftSon.isLeaf()) //goes to predecessor
            if (!tempLeftSon.isLeaf())
                tempLeftSon = tempLeftSon.children[tempLeftSon.numOfKeys];
            return tempLeftSon;

    }
    public BTreeNode successor(BTreeNode keyNode,int keyIndex){
        BTreeNode tempRightSon=keyNode.children[keyIndex+1];//right son
        while ( !tempRightSon.isLeaf())// goes to successor
            if (!tempRightSon.isLeaf())
                tempRightSon = tempRightSon.children[0];
        return  tempRightSon;
    }
    public void caseOneMergeRightBrother(BTreeNode keyNode,BTreeNode rightBro,BTreeNode fatherOfNode,int keyIndex){
        int rightFatherIndex = rightFatherKeyIndex(fatherOfNode, keyNode);
        keyNode.values[t] = fatherOfNode.values[rightFatherIndex];
        for (int i = 0; i < rightBro.numOfKeys & rightBro.numOfKeys == t - 1; i++) {
            keyNode.values[i + t + 1] = rightBro.values[i];
        }
        for (int i =rightFatherIndex ; i < fatherOfNode.numOfKeys; i++) {
            if(i!=fatherOfNode.numOfKeys-1)
                fatherOfNode.values[i]=fatherOfNode.values[i+1];
            else// i equals to fatherOfNode.numOfKeys-1 (last node doesn't have next)
                fatherOfNode.values[i]=null;
        }
        for (int i =rightFatherIndex+1 ; i < fatherOfNode.numOfKeys+1; i++) {
            if(i!=fatherOfNode.numOfKeys)
                fatherOfNode.children[i]=fatherOfNode.children[i+1];
            else// i equals to fatherOfNode.numOfKeys (last node doesn't have next)
                fatherOfNode.children[i]=null;
        }
        safeDeletion(keyNode,keyIndex);
    }
    public void caseOneMergeLeftBrother(BTreeNode keyNode,BTreeNode leftBro,BTreeNode fatherOfNode,int keyIndex){
        int leftFatherIndex = rightFatherKeyIndex(fatherOfNode, keyNode)-1;
        for (int i = 0; i <t-1 ; i++) {
            keyNode.values[keyNode.numOfKeys-1-i]=keyNode.values[i];
            keyNode.values[i]=null;
        }
        keyNode.values[keyNode.numOfKeys-1-t+1] = fatherOfNode.values[leftFatherIndex];
        for (int i = 0; i < leftBro.numOfKeys & leftBro.numOfKeys == t - 1; i++) {
            keyNode.values[i] = leftBro.values[i];
        }
        for (int i =leftFatherIndex ; i < fatherOfNode.numOfKeys; i++) {
            if(i!=fatherOfNode.numOfKeys-1)
                fatherOfNode.values[i]=fatherOfNode.values[i+1];
            else// i equals to fatherOfNode.numOfKeys-1 (last node doesn't have next)
                fatherOfNode.values[i]=null;
        }
        for (int i =leftFatherIndex+1 ; i < fatherOfNode.numOfKeys+1; i++) {
            if(i!=fatherOfNode.numOfKeys)
                fatherOfNode.children[i]=fatherOfNode.children[i+1];
            else// i equals to fatherOfNode.numOfKeys (last node doesn't have next)
                fatherOfNode.children[i]=null;
        }
        safeDeletion(keyNode,keyIndex);
    }
    public int rightFatherKeyIndex(BTreeNode father,BTreeNode middleSon){//finds the index of fathers node key
        for (int i = 0; i <=father.numOfKeys ; i++) {
            if(father.children[i].equals(middleSon)& i!=0)
                return i;
        }
        return -1;
    }
    public void caseOneRightBrother(BTreeNode keyNode,BTreeNode fatherOfNode,BTreeNode rightBro,String key){//puts values at relevant places and deletes key leaf, brother has t keys or more.
        keyNode.values[t]=fatherOfNode.values[rightFatherKeyIndex(fatherOfNode,keyNode)];//adds fathers right key to keynode
        fatherOfNode.values[rightFatherKeyIndex(fatherOfNode,keyNode)]=rightBro.values[0];//adds rightbro's key to father
        for (int i = 0; i < rightBro.numOfKeys-1 ; i++) {//shifts the places of right bro to leave no holes
            rightBro.values[i]=rightBro.values[i+1];
        }
        safeDeletion(keyNode,keyNodeIndex(keyNode,key));//deletes key
    }
    public void caseOneLeftBrother(BTreeNode keyNode,BTreeNode fatherOfNode,BTreeNode leftBro,String key){
        keyNode.values[t]=fatherOfNode.values[rightFatherKeyIndex(fatherOfNode,keyNode)-1];//adds fathers left key to keynode
        fatherOfNode.values[rightFatherKeyIndex(fatherOfNode,keyNode)-1]=leftBro.values[leftBro.numOfKeys-1];//adds leftbro's key to father
        for (int i = 0; i < leftBro.numOfKeys-1 ; i++) {//shifts the places of left bro to leave no holes
            leftBro.values[i]=leftBro.values[i+1];
        }
        safeDeletion(keyNode,keyNodeIndex(keyNode,key));//deletes key
    }
    public int keyNodeIndex(BTreeNode keyNode,String key){
        for (int i = 0; i < keyNode.numOfKeys ; i++) {
            if (keyNode.values[i].equals(key))
                return i;
        }
        return -1;

    }
    public BTreeNode getLeftBrother(BTreeNode father,BTreeNode middleSon){

        for (int i = 0; i <=father.numOfKeys ; i++) {
            if(father.children[i].equals(middleSon)& i!=0)
                return children[i-1];
        }
        return null;
    }
    public BTreeNode getRightBrother(BTreeNode father,BTreeNode middleSon){
        for (int i = 0; i <=father.numOfKeys ; i++) {
            if(father.children[i].equals(middleSon)& i!=father.numOfKeys)
                return children[i+1];
        }
        return null;
    }

    public void safeDeletion(BTreeNode target,int index){
        for (int i=index;i<numOfKeys;i=i+1){
            target.values[i]=target.values[i+1];
        }
    }
    public BTreeNode searchFather(String key,BTreeNode root) {//assumes child actually exists.
        int i = 0;
        if (root.isLeaf())
            throw new RuntimeException("leaf element entered to father search");
        while (i < root.numOfKeys && key.compareTo(root.values[i]) > 0) {
            i = i + 1;
        }
        if (root.children[i]==this) {
            return root;
        } else return children[i].searchFather(key,root.children[i]);
    }

    public int searchIndex(BTreeNode bt, String key) {
        for (int i = 0; i < bt.numOfKeys; i = i + 1) {
            if (bt.values[i].equals(key))
                return i;
        }
        return -1;
    }

    /*
    //deletion methods -start- ------------------------------------//
    public boolean delete(String key,BTree tree) {
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
    */


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


