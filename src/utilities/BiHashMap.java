package utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BiHashMap<K1, K2, V>
{
    private final Map<K1, Map<K2, V>> map;

    public BiHashMap()
    {
        map = new HashMap<K1, Map<K2, V>>();
    }

    public V put(K1 key1, K2 key2, V value)
    {
        Map<K2, V> map;
        if (this.map.containsKey(key1))
        {
            map = this.map.get(key1);
        }
        else
        {
            map = new HashMap<K2, V>();
            this.map.put(key1, map);
        }

        return map.put(key2, value);
    }

    public V get(K1 key1, K2 key2)
    {
        if (map.containsKey(key1))
        {
            return map.get(key1).get(key2);
        }
        else
        {
            return null;
        }
    }

    public V getOrDefault(K1 key1, K2 key2, V def)
    {
        if (map.containsKey(key1))
        {
            if (map.get(key1).containsKey(key2))
                return map.get(key1).get(key2);
            else
                return def;
        }
        else
        {
            return def;
        }
    }

    public boolean containsKeys(K1 key1, K2 key2)
    {
        return map.containsKey(key1) && map.get(key1).containsKey(key2);
    }

    public void clear()
    {
        map.clear();
    }

    public Set<Entry<K1, Map<K2, V>>> getValues()
    {
        return map.entrySet();
    }

    public Cell getDimensions()
    {
        // TODO: can we check only size of first val?
        int maxH = 0;
        for (Entry<K1, Map<K2, V>> val : getValues())
        {
            maxH = Math.max(maxH, val.getValue().size());
        }
        return new Cell(map.size(), maxH);
    }
}
