import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BloomFilter {

    private String[] hashFunctions;
    private boolean[] binaryArray;



    public BloomFilter(String m1,String txtFilePath){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(txtFilePath));
            int lines = 0;
            while (reader.readLine() != null) lines++;
            reader.close();
            hashFunctions=new String[lines];

            reader = new BufferedReader(new FileReader(txtFilePath));
            int counter=0;
            String str;
            while ((str=reader.readLine()) != null) {
                hashFunctions[counter] = str;
                counter++;
            }
        }
        catch (IOException e) {
            System.out.println("file not found");
        }
        binaryArray=new boolean[Integer.parseInt(m1)];
    }

    public void updateTable(String path){
        try {
            int p=15486907;
            int m1=binaryArray.length;
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String str;
            int passValue;
            int index;
            while ((str = reader.readLine()) != null) {
                passValue=passwordValue(str,p);
                for (int i = 0; i <hashFunctions.length ; i++) {
                    index=hashedValue(passValue,p,m1,hashFunctions[i]);
                    binaryArray[index]=true;
                }
            }
        }
        catch (IOException e){
            System.out.println("file not found");
        }
    }

    public int passwordValue(String pass,int p){
        int passValue=0;
        for (int i = 0; i <pass.length() ; i++)
            passValue+= (((int)pass.charAt(i))*Math.pow(256,pass.length()-i))%p;
        passValue=passValue%p;
        return passValue;
    }
    public int hashedValue(int passValue,int p,int m1,String hashFunction){
            String[] split= hashFunction.split("_");
            int alpha=Integer.parseInt(split[0]);
            int betta=Integer.parseInt(split[1]);
            int index=((alpha*passValue+betta)%p)%m1;
            return index;
    }
}
