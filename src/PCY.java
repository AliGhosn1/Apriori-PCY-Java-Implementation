

import java.io.*;
import java.util.*;


public class PCY {
    public static int datasize = 833;


    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();

        BitSet bitSet = new BitSet(833);

        Set<String> uniqueItems = new HashSet<>();
        //store unique items in the dataset

        List<String> buckets;
        //a collection of all items in one line

        Hashtable<String,Double> database = new Hashtable<>();
        //a collection of all items with their support #

        Hashtable<String,Double> firstBucket = new Hashtable<>();

        //File input/output stream
        BufferedReader in = new BufferedReader(new FileReader("data/retail.txt"));
        BufferedWriter output = new BufferedWriter(new FileWriter("data/PCY_results.txt"));

        String currentLine; //current line scanned by BufferedReader
        int counter = 0;
        double support;

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
                    database.put(item, 1.0);
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
                        firstBucket.put(items, 1.0);
                    }

                }

            }
            counter++;
        }

        support = generateDynamicSupportThreshhold(firstBucket);
        for (String k : firstBucket.keySet()) {
            Double v = firstBucket.get(k);
            if(v >= support){
                bitSet.set(hashFunction(k));
            }

        }


        support = generateDynamicSupportThreshhold(database);
        //generate 1st sequences
        Hashtable<String,Double> frequentItems = new Hashtable<>();
   
        for (String k : database.keySet()) {
            Double v = database.get(k);
            if(v >= support){
                frequentItems.put(k, v);
            }
        }

        //second pass
        //Hashtable<String,Integer> secondPass = new Hashtable<>();
        in = new BufferedReader(new FileReader("data/retail.txt"));
        Hashtable<String, Double> frequentBucket = new Hashtable<>();

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
                        frequentBucket.put(items,1.0);
                    }

                }

            }
        }

        Hashtable<String,Double> frequentPairs = new Hashtable<>();

        for (String k : frequentBucket.keySet()) {
            Double v = frequentBucket.get(k);
            if(v >= support){
                frequentPairs.put(k, v);
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

    public static double generateDynamicSupportThreshhold(Hashtable<String,Double> database){
        double support = 0;
        for (String k : database.keySet()) {
            Double v = database.get(k);
            support += v;
        }

        return support / database.size();
    }
}
