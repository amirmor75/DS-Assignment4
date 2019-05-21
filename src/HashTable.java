import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HashTable {

    private HashList[] table;

    public HashTable(String m2){
        table= new HashList[Integer.parseInt(m2)];
        for (int i = 0; i <table.length ; i++)
            table[i]=new HashList();
    }
    public void updateTable(String filePath){//updating the table with bad passwords
        try {//updating bad passwords document to our table
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String str;
            int passValue;
            while ((str = reader.readLine()) != null) {
                passValue=passwordValue(str);
                table[hashFunction(passValue)].addfirst(passValue);
            }
        }
        catch (IOException e){
            System.out.println("file not found");
        }
    }
    public int hashFunction(int key){
        return key%table.length;
    }

    public int passwordValue(String pass){
        int p=15486907;
        int passValue=0;
        for (int i = 0; i <pass.length() ; i++)
            passValue+= (((int)pass.charAt(i))*Math.pow(256,pass.length()-i-1))%p;
        passValue=passValue%p;
        return passValue;
    }

    public HashList[] getTable() {
        return table;
    }
    public boolean contains(int key){
        return table[hashFunction(key)].contains(key);
    }
}
