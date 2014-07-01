package com.lm.util;
import java.util.*;

public class MapUtil
{
    public static <K, V extends Comparable<? super V>> Map<Integer, K> 
        sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
            new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return -(o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<Integer, K> result = new LinkedHashMap<Integer, K>();
        int i=0;
        for (Map.Entry<K, V> entry : list)
        {
            result.put(i++, entry.getKey() );
        }
        return result;
    }
}