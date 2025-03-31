

import java.io.*;
import java.util.*;


public class Apriori {
    public static int datasize = 833;

    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();

        Set<String> uniqueItems = new HashSet<>();
        //store unique items in the dataset

        List<String> buckets;
        //a collection of all items in one line

        Hashtable<String, Double> database = new Hashtable<>();
        //a collection of all items with their support #

        //File input/output stream
        BufferedReader in = new BufferedReader(new FileReader("data/retail.txt"));
        BufferedWriter output = new BufferedWriter(new FileWriter("data/results.txt"));

        String currentLine; //current line scanned by BufferedReader
        Integer counter = 0;
        double support;

        counter = 0;
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

            counter++;
        }

        support = generateDynamicSupportThreshhold(database);
        System.out.println("Support: " + support);
        //generate 1st sequences
        Hashtable<String, Double> frequentItems = new Hashtable<>();
        for (String k : database.keySet()) {
            Double v = database.get(k);
            if(v >= support){
                frequentItems.put(k, v);
            }
        }

        //second pass
        Hashtable<String,Double> secondPass = new Hashtable<>();

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
                            secondPass.put(key ,1.0);
                        }

                    }

                }

            }
        }

        support = generateDynamicSupportThreshhold(secondPass);
        System.out.println("Support: " + support);
        Hashtable<String,Double> frequentPairs = new Hashtable<>();

        for (String k : secondPass.keySet()) {
            Double v = secondPass.get(k);
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

    public static double generateDynamicSupportThreshhold(Hashtable<String,Double> database){
        double support = 0;
        for (String k : database.keySet()) {
            Double v = database.get(k);
            support += v;
        }

        return support / database.size();
    }
}
