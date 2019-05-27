public class Main {

    public static void main(String[] args) {
        System.out.println("vhvhvkh");
        BTree btree = new BTree("2");
        btree.createFullTree("C:\\Users\\amitc\\Desktop\\Degree\\Second Semester\\Data Structures\\Assigments To Submit\\Assignment4\\bad_passwords.txt");
        System.out.println(btree);
        //System.out.println(btree.getHeight());
        btree.deleteKeysFromTree("C:\\Users\\amitc\\Desktop\\Degree\\Second Semester\\Data Structures\\Assigments To Submit\\Assignment4\\delete_keys.txt");
        //btree.delete(null);
        System.out.println(btree);
    }

}
