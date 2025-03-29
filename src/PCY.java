

import java.io.*;
import java.util.*;


public class PCY {
    public static int datasize = 88000;
    double support;
    static double weightImpact = 0.5;

    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();

        BitSet bitSet = new BitSet(88000);

        Set<String> uniqueItems = new HashSet<>();
        //store unique items in the dataset

        List<String> buckets;
        //a collection of all items in one line

        Hashtable<String,Integer> database = new Hashtable<>();
        //a collection of all items with their support #

        Hashtable<String,Integer> firstBucket = new Hashtable<>();

        //File input/output stream
        BufferedReader in = new BufferedReader(new FileReader("data/retail.txt"));
        BufferedWriter output = new BufferedWriter(new FileWriter("data/PCY_results.txt"));

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

            //What PCY improved from Apriori
            for(String item_1 : buckets){

                int index = buckets.indexOf(item_1);

                for(String item_2: buckets.subList(index+1,buckets.size())){
                    //skip if 2 items are the same
                    if(item_1.equals(item_2)){
                        continue;
                    }

                    String items = item_1 + " "+ item_2;

                    if(firstBucket.containsKey(items)){
                        firstBucket.put(items, firstBucket.get(items) + 1);
                    }else{
                        firstBucket.put(items,1);
                    }

                }

            }

        }

        support = generateDynamicSupportThreshhold(firstBucket);

        for(int i = 0; i < firstBucket.size(); i++){
            if(firstBucket[i].getValue() >= getWeightedFrequency(firstBucket[i].getValue(), i, firstBucket.size())){
                bitSet.set(hashFunction(firstBucket[i].getKey()));
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
        //Hashtable<String,Integer> secondPass = new Hashtable<>();
        LinkedHashMap<String, Integer> frequentBucket = new LinkedHashMap<>();
        in = new BufferedReader(new FileReader("data/retail.txt"));

        while ((currentLine = in.readLine()) != null) {

            buckets = Arrays.asList(currentLine.split(" "));

            for(String item_1 : buckets){

                int index = buckets.indexOf(item_1);

                for(String item_2: buckets.subList(index+1,buckets.size())){
                    //skip if 2 items are the same
                    if(item_1.equals(item_2)){
                        continue;
                    }

                    String items = item_1 + " "+ item_2;

                    //check if the bit vector is set OR not set
                    if(!bitSet.get(hashFunction(items))){
                        continue;
                    }
                    //check if each of the items is a frequent item
                    if(frequentBucket.containsKey(items)){

                        frequentBucket.put(items,frequentBucket.get(items) + 1);

                    }else{
                        frequentBucket.put(items,1);
                    }

                }

            }
        }

        Hashtable<String,Integer> frequentPairs = new Hashtable<>();
        support = generateDynamicSupportThreshhold(frequentBucket);

        for(int i = 0; i < frequentBucket.size(); i++){
            if(frequentBucket[i].getValue() >= getWeightedFrequency(frequentBucket[i].getValue(), i, frequentBucket.size())){
                frequentPairs.put(frequentBucket[i].getKey(), frequentBucket[i].getValue());
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

    public static Integer hashFunction(String key){
        return Math.abs(key.hashCode() % datasize);
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
