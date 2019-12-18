package bearmaps.proj2c;

import bearmaps.hw4.streetmap.Node;
import bearmaps.hw4.streetmap.StreetMapGraph;
import bearmaps.proj2ab.KDTree;
import bearmaps.proj2ab.Point;

import java.util.*;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 *
 * @author Alan Yao, Josh Hug, ________
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {
    private List<Node> nodes;
    private MyTrieSet trie;
    private Map<String, List<Node>> nameMap;


    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);

         nodes = this.getNodes();
         trie = new MyTrieSet();
         nameMap = new HashMap();
         for (Node n : nodes) {
             String name = n.name();
             if (name != null) {
                 String cn = cleanString(name);
                 trie.add(cn);
                 List<Node> theList = nameMap.get(cn);
                 if (theList == null) { // the name is not in map yet
                     List<Node> toAdd = new LinkedList<>();
                     toAdd.add(n);
                     nameMap.put(cleanString(name), toAdd);
                 } else { // the map already has the same name place
                    theList.add(n);
                 }

             }
         }

    }


    /**
     * For Project Part II
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */

    public long closest(double lon, double lat) {
        Map<Point, Node> m = mapping(nodes);
        List<Point> points = new LinkedList<>(m.keySet());
        KDTree kd = new KDTree(points);
        Point closest = kd.nearest(lon, lat);
        return m.get(closest).id();
    }

    private Map<Point, Node> mapping(List<Node> nodes) {
        Map<Point, Node> map = new HashMap<>();
        for (Node n : nodes) {
            if (neighbors(n.id()).isEmpty()) {
                continue;
            }
            double lon = n.lon();
            double lat = n.lat();
            Point p = new Point(lon, lat);
            map.put(p, n);
        }
        return map;
    }


    /**
     * For Project Part III (gold points)
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        prefix = cleanString(prefix);

        List<String> samePrefixNames = new LinkedList<>();

        for (String cleanedName : trie.keysWithPrefix(prefix)) {
            List<Node> theList = nameMap.get(cleanedName);
            samePrefixNames.add(theList.get(0).name());
        }
        return samePrefixNames;
    }

    /**
     * For Project Part III (gold points)
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        String cleanedName = cleanString(locationName);
        List<Node> theList = nameMap.get(cleanedName);  // hashMap's get() method: time complexity O(1)
        List<Map<String, Object>> returnList = new LinkedList<>();
        for (Node n : theList) {
            Map<String, Object> map = new HashMap<>();
            map.put("lat", n.lat());
            map.put("lon", n.lon());
            map.put("name", n.name());
            map.put("id", n.id());
            returnList.add(map);
        }
        return returnList;
    }


    /**
     * Useful for Part III. Do not modify.
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

}
