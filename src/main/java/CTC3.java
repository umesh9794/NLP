import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by uchaudh on 7/31/2015.
 */
public class CTC3 {


    /**
     * Global variables initialization
     */
    static ConcurrentHashMap<Integer,List<String>> givenItems=new ConcurrentHashMap<Integer, List<String>>();

    static TreeMap<Integer,List<String>> bagPack=new  TreeMap<Integer, List<String>>(Collections.reverseOrder());

    static String totalBudget=null;

    static int minMonth=0;
    static int maxMonth=0;
    static boolean isRevisit=false;
    static boolean isAddedtoBag=false;


    /**
     * Entry point for program, accepts input as command line argument
     * @param args
     */
    public static void main(String[] args) {

        totalBudget=  (args[0]);

        String[] items=  args[1].split(",");

        for(String s: items)
        {
            String[] splittedVal= s.split(";");
            add(givenItems,Integer.parseInt(splittedVal[2]),splittedVal[1]+";"+splittedVal[0]);
        }
        packItems();

        /**
         * Revisit item-list
         */
        while (givenItems.size() !=0) {
            isRevisit=true;
            packItems();
        }

        /**
         * Preparing output
         */
        if(bagPack.size()>0) {
            StringBuilder sb = new StringBuilder();

            int count = 0;

            for (Integer prior : bagPack.keySet()) {
                count++;
                List<String> s = bagPack.get(prior);

                int innerCount = 0;
                for (String val : s) {
                    String price = val.split(";")[1];
                    String Month = val.split(";")[0];

                    sb.append(price + "|" + Month + "|" + prior);
                    if (innerCount++ < s.size() - 1)
                        sb.append(",");
                }
                if (count < bagPack.keySet().size())
                    sb.append(",");
            }
            System.out.print(sb.toString());
        }
        else
            System.out.println("Oops! you should not purchase items from your list!");
    }

    /**
     * Implementation for adding an element in Map<String,List<String>>
     * @param map
     * @param key
     * @param newValue
     */
    public static void add(Map<Integer,List<String>> map,Integer key, String newValue) {
        List<String> currentValue = map.get(key);
//        if(isRevisit)
//       {
        if (currentValue == null) {
            currentValue = new ArrayList<String>();
            map.put(key, currentValue);
        }
        currentValue.add(newValue);
        isAddedtoBag = true;
    }


    /**
     * Implementation for removing an element in Map<String,List<String>>
     * @param key
     * @param newValue
     */
    public static void remove(Integer key, String newValue) {
        List<String> currentValue = givenItems.get(key);
        currentValue.remove(newValue);
        givenItems.remove(key);
        if(currentValue.size()==0)
            givenItems.remove(key);
        else
            givenItems.put(key, currentValue);
    }


    /**
     * Heuristics for purchasing items
     */
    private static void packItems()
    {
        int accumulatedCost= 0;
        int maxPriceAmongSamePriority=0;
        int count=0;
        int priceTOBeCompared=0;
        Integer keyTobeAdded=null;
        String valToBeAdded=null;
        Integer montWindowToCompare=0;
        isAddedtoBag=false;

        List<Integer> keyList= new ArrayList<Integer>(givenItems.keySet());
        Collections.sort(keyList, Collections.reverseOrder());
        int maxPriority=keyList.get(0);

        for(Integer key: keyList)
        {
            List<String> otherParams= givenItems.get(key);
            if(otherParams.size()>1) {
                int val=0;
                while(val<otherParams.size())
//                for (int val=0;val<otherParams.size();val++)
                {
                    isAddedtoBag=false;
                    boolean monthWindowUpdated=false;
                    Integer budget= Integer.parseInt(totalBudget.toString());
                    String[] monthandPrice=otherParams.get(val).split(";");
                    Integer currentPrice =Integer.parseInt(monthandPrice[1]);

                    int currentMinMonth=0;
                    int currentMaxMonth=0;
                    int monthWindow=0;

                    String[] monthVal= monthandPrice[0].split(":");

                    currentMinMonth= Integer.parseInt(monthVal[0]);
                    currentMaxMonth=currentMinMonth+ Integer.parseInt(monthVal[1])-1;
                    monthWindow=Integer.parseInt(monthVal[1]);

//                    if( count==0 && ((minMonth==0 && maxMonth==0 )|| (currentMinMonth>=minMonth && currentMaxMonth<=maxMonth)))
                    if( count==0 && ((minMonth==0 && maxMonth==0 )|| (montWindowToCompare>monthWindow)))
                    {
                        minMonth=currentMinMonth;
                        maxMonth=currentMaxMonth;
                        priceTOBeCompared= currentPrice;
                        keyTobeAdded=key;
                        valToBeAdded= otherParams.get(val);
                        montWindowToCompare=monthWindow;
                        monthWindowUpdated=true;

                    }
//                    if(count==0 && (currentMinMonth>minMonth && currentMaxMonth<maxMonth))
//                    {
//                        minMonth=currentMinMonth;
//                        maxMonth=currentMaxMonth;
//                    }

                    if(( currentMinMonth==1 && currentMaxMonth==12 || currentMinMonth>=minMonth && currentMaxMonth<=maxMonth || currentMinMonth<minMonth && currentMaxMonth>maxMonth ) &&(  currentPrice<= budget ))
                    {
//                        add(bagPack,key,otherParams.get(0));
//                        budget=budget-currentPrice;
//                        totalBudget= budget.toString();
                        if(val==0) {
                            montWindowToCompare=monthWindow;
                            priceTOBeCompared = currentPrice;
                            keyTobeAdded=key;
                            valToBeAdded= otherParams.get(val);
                        }

                        else {
                            if (count == 0) {
                                if (currentPrice <= priceTOBeCompared && monthWindowUpdated) {
                                    priceTOBeCompared = currentPrice;
                                    keyTobeAdded = key;
                                    valToBeAdded = otherParams.get(val);
                                }
                            } else {
                                if (currentPrice < priceTOBeCompared) {
                                    priceTOBeCompared = currentPrice;
                                    keyTobeAdded = key;
                                    valToBeAdded = otherParams.get(val);
                                }

                            }
                        }

                        if(val==otherParams.size()-1)
                        {
                            add(bagPack, keyTobeAdded,valToBeAdded);
                            remove(keyTobeAdded,valToBeAdded);
                            if(isAddedtoBag)
                            {
                                budget=budget- Integer.parseInt(valToBeAdded.split(";")[1]);
                                totalBudget= budget.toString();
                            }
                        }
                    }
                    else
                    {
                        remove(key,otherParams.get(val));
                    }

                    val++;
                    if(isAddedtoBag && otherParams.size()>1)
                        val=0;
                }
            }
            else
            {
                Integer budget= Integer.parseInt(totalBudget.toString());
                String[] monthandPrice=otherParams.get(0).split(";");
                Integer currentPrice =Integer.parseInt(monthandPrice[1]);
                String[] monthVal= monthandPrice[0].split(":");
                int currentMinMonth=Integer.parseInt(monthVal[0]);
                int currentMaxMonth=currentMinMonth+ Integer.parseInt(monthVal[1])-1;

                if( minMonth==0 && maxMonth==0 ) {
                    minMonth = currentMinMonth;
                    maxMonth = currentMaxMonth;
                }


                if(( currentMinMonth==1 && currentMaxMonth==12 || currentMinMonth>=minMonth && currentMaxMonth<=maxMonth || currentMinMonth<minMonth && currentMaxMonth>maxMonth  ) &&(  currentPrice<= budget ))
                {
                    add(bagPack,key,otherParams.get(0));
                    remove(key,otherParams.get(0));
                    if(isAddedtoBag) {
                        budget = budget - currentPrice;
                        totalBudget = budget.toString();
                    }
                }
                else
                    remove(key,otherParams.get(0));
            }
            count++;
        }
    }
}
