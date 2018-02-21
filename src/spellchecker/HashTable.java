package spellchecker;

import java.util.ArrayList;
import java.util.LinkedList;

public class HashTable {
    
    public static LinkedList<String>[] table;
    
    public static final int HASH_MAX = SpellChecker.dictionary.size();
    public static int index;
    public static int collisions   = 0;
    public static int longestChain = 0;
    public static int tempindex;
    
    HashTable (int n){
        
        while (isPrime(n)){
            n++;
        }
        table = new LinkedList[n];
        this.table = table;
    }
    
    public void putDictionary(ArrayList<String> a){
        for (String s : a){
            s = s.toLowerCase();
            int x;
            
            x = getHashIndex(s);
            
            if (table[x] == null){
                table[x] = new LinkedList<String>();
                table[x].add(s);
            } else {
                table[x].add(s);
                collisions++;
                
                if (table[x].size() > longestChain){
                    longestChain = table[x].size();
                    tempindex = x;
                }
            }
        }
        /*
        DEBUG
        
        System.out.println("Number of collisions: " + collisions);
        System.out.println("Longest Chain: " + longestChain);
        System.out.println(tempindex);
        */
    }
    
    public static void putNewWord(String s){
        s = s.toLowerCase();
        
        int x;
        x = getHashIndex(s); 
        
        if (table[x] == null){
            table[x] = new LinkedList<String>();
            table[x].add(s);
        } else {
            table[x].add(s);
            collisions++;

            if (table[x].size() > longestChain){
                longestChain = table[x].size();
                tempindex = x;
            }
        }
    }
    
    public static boolean contains(String s){
        
        boolean contains = false;
        if (table[getHashIndex(s)] != null){
            contains = table[getHashIndex(s)].contains(s.toLowerCase());
        }
        
        return contains;
    }
    
    public static int getHashIndex(String s){
        int mult;
        int size;
        index = 0;
        
        s = s.toLowerCase();
        
        for (int i = 0; i < s.length(); i++){
            index = index * 923 + s.charAt(i);
            //mult--;
        }
        
        index = Math.abs(index);
        
        return index % HASH_MAX;
    }
    
    public static int assignCharNum(char a){
        int charNum ;
        
        charNum = Character.toUpperCase(a) - 'A' + 1;
        
        return charNum;
    }
    
    public static boolean isPrime(int x){
 
        if (x % 2 == 0) {
            return false;
        }    
    
        for(int i = 3; i * i <= x; i += 2) {
            if( x % i == 0) {
                return false;
            }
        }
        
        return true;
    }
}
