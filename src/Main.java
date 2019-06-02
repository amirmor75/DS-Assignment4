public class Main {
    public static void main(String[] args) {
        BTree bTree=new BTree("2");
        bTree.createFullTree("C:\\Users\\amirm_000\\Desktop\\Repositories\\DS-Assignment4\\requested_passwords.txt");
        System.out.println(bTree);
    }

}
