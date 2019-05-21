import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BloomFilter {

    private String[] hashFunctions;
    private boolean[] binaryArray;
    private static int p=15486907;



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

            int m1=binaryArray.length;
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String str;
            int passValue;
            int index;
            while ((str = reader.readLine()) != null) {
                passValue=passwordValue(str);
                for (int i = 0; i <hashFunctions.length ; i++) {
                    index=hashedValue(passValue,m1,hashFunctions[i]);
                    binaryArray[index]=true;
                }
            }
        }
        catch (IOException e){
            System.out.println("file not found");
        }
    }


    public String getFalsePositivePercentage(HashTable hashTable, String filePath){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String str;
            int unJustlyDitected=0;
            int goodPasswords=0;
            int badDitectedByBloom=0;
            int passValue;
            while ((str = reader.readLine()) != null) {
                passValue=hashTable.passwordValue(str);
                if (isBad(passValue) & !hashTable.contains(passValue))
                    unJustlyDitected=unJustlyDitected+1;
                if (!hashTable.contains(passValue))
                    goodPasswords=goodPasswords+1;
                if (isBad(passValue))
                    badDitectedByBloom=badDitectedByBloom+1;

            }
            double percentage=(double)(unJustlyDitected)/(double)(goodPasswords);
            return Double.toString(percentage);


        }
        catch (IOException e){
            return "file not found";
        }

    }
    public String getRejectedPasswordsAmount(String filePath){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String str;
            int passValue;
            int rejected=0;
            while ((str = reader.readLine()) != null) {
                passValue=passwordValue(str);
                if (isBad(passValue))
                    rejected=rejected+1;
            }
            return Integer.toString(rejected);
        }
        catch (IOException e){
            return "file not found";
        }
    }

    public boolean isBad(int passValue){
        for (int i = 0; i <hashFunctions.length ; i++) {
            if(!binaryArray[hashedValue(passValue,hashFunctions.length,hashFunctions[i])])
                return false;
        }
        return true;
    }

    public int passwordValue(String pass){
        int passValue=0;
        for (int i = 0; i <pass.length() ; i++)
            passValue+= (((int)pass.charAt(i))*Math.pow(256,pass.length()-i-1))%p;
        passValue=passValue%p;
        return passValue;
    }

    public int hashedValue(int passValue,int arrayLength,String hashFunction){

        String[] split= hashFunction.split("_");
        int alpha=Integer.parseInt(split[0]);
        int betta=Integer.parseInt(split[1]);
        int index=((alpha*passValue+betta)%p)%arrayLength;
        return index;
    }

}
