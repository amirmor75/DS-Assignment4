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
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String str;
            while ((str = reader.readLine()) != null) {
                
            }
        }
        catch (IOException e){
            System.out.println("file not found");
        }
    }
}
