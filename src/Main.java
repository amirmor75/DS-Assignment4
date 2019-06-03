public class Main {
    public static void main(String[] args) {
        BTree bTree=new BTree("2");
        bTree.createFullTree("C:\\Users\\amitc\\Desktop\\Degree\\Second Semester\\Data Structures\\Assigments To Submit\\Assignment4\\requested_passwords.txt");
        System.out.println(bTree);
        bTree.deleteKeysFromTree("C:\\Users\\amitc\\Desktop\\Degree\\Second Semester\\Data Structures\\Assigments To Submit\\Assignment4\\bad_passwords.txt");
        System.out.println(bTree);
    }
}
