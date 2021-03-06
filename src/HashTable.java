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

    public boolean contains(int key){
        return table[hashFunction(key)].contains(key);
    }

    public String getSearchTime(String filePath) {
        double start=System.nanoTime()/1000000.0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String str;
            while ((str=reader.readLine())!=null){
                contains(passwordValue(str));
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        double end=System.nanoTime()/1000000.0;
        String output=String.valueOf(end-start);
        int indexOfDot=output.indexOf('.');
        output=output.substring(0,indexOfDot+4);
        return output;
    }
}
