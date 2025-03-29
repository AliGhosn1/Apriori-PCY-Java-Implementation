

import java.io.*;
import java.util.*;


public class Apriori {
    public static int datasize = 88000;
    public static double support;

    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();

        Set<String> uniqueItems = new HashSet<>();
        //store unique items in the dataset

        List<String> buckets;
        //a collection of all items in one line

        Hashtable<String,Integer> database = new Hashtable<>();
        //a collection of all items with their support #

        //File input/output stream
        BufferedReader in = new BufferedReader(new FileReader("data/retail.txt"));
        BufferedWriter output = new BufferedWriter(new FileWriter("data/results.txt"));

        String currentLine; //current line scanned by BufferedReader

        //first pass
        while ((currentLine = in.readLine()) != null) {
            //output.write(currentLine+"\n");
            buckets = Arrays.asList(currentLine.split(" "));
            uniqueItems.addAll(buckets); //get all unique items

            //remove duplicate items in one line
            Set<String> tempSet = new HashSet<>(buckets);
            // now tempSet contains items in one line with only unique items, duplicated values are removed
            for(String item: tempSet) {     //get the support of each item
                if (database.containsKey(item)) {
                    database.put(item, database.get(item) + 1);
                } else {
                    database.put(item, 1);
                }
            }
        }

        support = generateDynamicSupportThreshhold(database);

        //generate 1st sequences
        Hashtable<String,Integer> frequentItems = new Hashtable<>();
        
        for(int i = 0; i < database.size(); i++){
            if(database[i].getValue() >= getWeightedFrequency(database[i].getValue(), i, database.size())){
                frequentItems.put(database[i].getKey(), database[i].getValue());
            }
        }

        //second pass
        Hashtable<String,Integer> secondPass = new Hashtable<>();

        in = new BufferedReader(new FileReader("data/retail.txt"));

        while ((currentLine = in.readLine()) != null) {
            //output.write(currentLine+"\n");
            buckets = Arrays.asList(currentLine.split(" "));

            for(String item_1 : buckets){

                int index = buckets.indexOf(item_1);

                for(String item_2: buckets.subList(index+1,buckets.size())){
                    //skip if 2 items are the same
                    if(item_1.equals(item_2)){
                        continue;
                    }

                    //check if each of the items is a frequent item
                    if(frequentItems.containsKey(item_1) && frequentItems.containsKey(item_2)){
                        String key = item_1 +" "+ item_2;
                        if(secondPass.containsKey(key)){
                            secondPass.put(key, secondPass.get(key) + 1);
                        }else{
                            secondPass.put(key ,1);
                        }

                    }

                }

            }
        }

        support = generateDynamicSupportThreshhold(secondPass);

        Hashtable<String,Integer> frequentPairs = new Hashtable<>();

        for(int i = 0; i < secondPass.size(); i++){
            if(secondPass[i].getValue() >= getWeightedFrequency(secondPass[i].getValue(), i, secondPass.size())){
                frequentPairs.put(secondPass[i].getKey(), secondPass[i].getValue());
            }
        }

        //output frequent items to file
        frequentItems.forEach((k,v)->{
            try {
                output.write("Item: " + k + " Frequencies: " + v +"\n");
            }catch(Exception e){
                e.printStackTrace();
            }
        });

        frequentPairs.forEach((k,v)->{
            try {
                output.write("Item: " + k + " Frequencies: " + v +"\n");
            }catch(Exception e){
                e.printStackTrace();
            }
        });

        output.close();
        in.close();

        long endTime = System.currentTimeMillis();
        System.out.println("Datasize: " + datasize);
        System.out.println("Time elapsed: " + (endTime - startTime));

    }

    public static double generateDynamicSupportThreshhold(Hashtable<String,Integer> database){
        double support = 0;
        for(int i = 0; i < database.size(); i++){
            support += getWeightedFrequency(database[i].getValue(), i, database.size());
        }

        return support / database.size();
    }

    public static double getWeightedFrequency(double entryValue, int i, int size){
        return entryValue * (1 + (weightImpact - (weightImpact * 2 * i / dsize)));
    }

}
