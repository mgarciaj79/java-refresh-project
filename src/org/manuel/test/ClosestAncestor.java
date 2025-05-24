package org.manuel.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClosestAncestor {
	
	 public static String findClosestCommonAncestor(List<List<String>> hierarchy, String region1, String region2) {
	        // 1. Build a parent map for efficient ancestor retrieval
	        Map<String, String> parentMap = new HashMap<>();
	        Set<String> allRegions = new HashSet<>();

	        for (List<String> entry : hierarchy) {
	            if (!entry.isEmpty()) {
	                String parent = entry.get(0);
	                allRegions.add(parent);
	                for (int i = 1; i < entry.size(); i++) {
	                    String child = entry.get(i);
	                    parentMap.put(child, parent);
	                    allRegions.add(child);
	                }
	            }
	        }

	        if (!allRegions.contains(region1) || !allRegions.contains(region2)) {
	            return "One or both regions not found"; // One or both regions not found
	        }

	        // 2. Get the path from region1 to the root
	        List<String> path1 = getPathToRoot(region1, parentMap);

	        // 3. Get the path from region2 to the root
	        List<String> path2 = getPathToRoot(region2, parentMap);

	        // 4. Find the closest common ancestor by comparing paths from the root
	        String closestCommonAncestor = null;
	        int i = path1.size() - 1;
	        int j = path2.size() - 1;

	        while (i >= 0 && j >= 0 && path1.get(i).equals(path2.get(j))) {
	            closestCommonAncestor = path1.get(i);
	            i--;
	            j--;
	        }

	        return closestCommonAncestor;
	    }

	    private static List<String> getPathToRoot(String region, Map<String, String> parentMap) {
	        List<String> path = new ArrayList<>();
	        String current = region;
	        while (current != null) {
	            path.add(current);
	            current = parentMap.get(current);
	        }
	        return path;
	    }

	    public static void main(String[] args) {
	        List<List<String>> hierarchy = Arrays.asList(
	                Arrays.asList("World", "ContinentA", "ContinentB"),
	                Arrays.asList("ContinentA", "CountryX", "CountryY"),
	                Arrays.asList("CountryX", "Region1", "Region2"),
	                Arrays.asList("ContinentB", "CountryZ"),
	                Arrays.asList("CountryZ", "Region3", "Region1")
	        );

	        String region1 = "Region1";
	        String region2 = "Region3";
	        String commonAncestor = findClosestCommonAncestor(hierarchy, region1, region2);
	        System.out.println("Closest common ancestor of " + region1 + " and " + region2 + ": " + commonAncestor);

	        region1 = "Region1";
	        region2 = "Region2";
	        commonAncestor = findClosestCommonAncestor(hierarchy, region1, region2);
	        System.out.println("Closest common ancestor of " + region1 + " and " + region2 + ": " + commonAncestor);

	        region1 = "Region1";
	        region2 = "CountryX";
	        commonAncestor = findClosestCommonAncestor(hierarchy, region1, region2);
	        System.out.println("Closest common ancestor of " + region1 + " and " + region2 + ": " + commonAncestor);

	        region1 = "CountryY";
	        region2 = "CountryZ";
	        commonAncestor = findClosestCommonAncestor(hierarchy, region1, region2);
	        System.out.println("Closest common ancestor of " + region1 + " and " + region2 + ": " + commonAncestor);

	        region1 = "NonExistentRegion";
	        region2 = "Region1";
	        commonAncestor = findClosestCommonAncestor(hierarchy, region1, region2);
	        System.out.println("Closest common ancestor of " + region1 + " and " + region2 + ": " + commonAncestor);

	        List<List<String>> hierarchy2 = Arrays.asList(
	                Arrays.asList("Earth", "Asia", "Africa"),
	                Arrays.asList("Asia", "China", "India"),
	                Arrays.asList("Africa", "Egypt", "Nigeria"),
	                Arrays.asList("China", "Beijing", "Shanghai"),
	                Arrays.asList("India", "Delhi", "Mumbai")
	        );
	        region1 = "Beijing";
	        region2 = "Mumbai";
	        commonAncestor = findClosestCommonAncestor(hierarchy2, region1, region2);
	        System.out.println("Closest common ancestor of " + region1 + " and " + region2 + ": " + commonAncestor);

	        List<List<String>> hierarchy3 = Arrays.asList(
	                Arrays.asList("A", "B", "C"),
	                Arrays.asList("B", "D", "E"),
	                Arrays.asList("C", "F", "G"),
	                Arrays.asList("E", "H", "I")
	        );
	        region1 = "H";
	        region2 = "F";
	        commonAncestor = findClosestCommonAncestor(hierarchy3, region1, region2);
	        System.out.println("Closest common ancestor of " + region1 + " and " + region2 + ": " + commonAncestor);

	        List<List<String>> hierarchy4 = Arrays.asList(
	                Arrays.asList("A", "B"),
	                Arrays.asList("B", "C"),
	                Arrays.asList("C", "D")
	        );
	        region1 = "A";
	        region2 = "D";
	        commonAncestor = findClosestCommonAncestor(hierarchy4, region1, region2);
	        System.out.println("Closest common ancestor of " + region1 + " and " + region2 + ": " + commonAncestor);

	        region1 = "B";
	        region2 = "NonExistent";
	        commonAncestor = findClosestCommonAncestor(hierarchy, region1, region2);
	        System.out.println("Closest common ancestor of " + region1 + " and " + region2 + ": " + commonAncestor);
	    }

}
