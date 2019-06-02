public class Main {
    public static void main(String[] args) {
        BTree tree =new BTree("2");
        tree.createFullTree("C:\\Users\\amirm_000\\Desktop\\Repositories\\DS-Assignment4\\requested_passwords.txt");
        System.out.println(tree);
    }
}
