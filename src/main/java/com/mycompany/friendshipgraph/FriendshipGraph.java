package com.mycompany.friendshipgraph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

@NoArgsConstructor
public class FriendshipGraph {
    final static Logger logger = Logger.getLogger(FriendshipGraph.class);
    HashMap<Person, Set<Person>> graph = new HashMap<Person, Set<Person>>();


    public void loadFrom(String fileName) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(
                new FileReader(fileName));
        loadFrom(br);
    }

    public void loadFrom(BufferedReader br) {
        Gson gson = new Gson();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Reading JSON from a file");
                logger.debug("----------------------------");
            }


            String inputLine;
            int lineNumber=1;
            while ((inputLine = br.readLine()) != null) {
                try {
                    Relation relationObj = gson.fromJson(inputLine, Relation.class);
                    Person p1 = relationObj.getFrom();
                    Person p2 = relationObj.getTo();

                    boolean areFriends = relationObj.isAreFriends();
                    if (!graph.containsKey(p1)) {
                        graph.put(p1, new HashSet<Person>());
                    }
                    if (!graph.containsKey(p2)) {
                        graph.put(p2, new HashSet<Person>());
                    }
                    if (areFriends) {
                        graph.get(p1).add(p2);
                        graph.get(p2).add(p1);
                    } else {
                        graph.get(p1).remove(p2);
                        graph.get(p2).remove(p1);
                    }

                } catch (JsonSyntaxException e) {
                    //ignore the error and continue processing the next line
                    e.printStackTrace();
                } catch (JsonIOException e) {
                    //ignore the error and continue processing the next line
                    e.printStackTrace();
                }
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Map friends(String userID) {
        Person dummy = new Person(userID, "");

        Map<String, Set<Person>> map = new HashMap<String, Set<Person>>();
        Set<Person> friends;
        if (graph.containsKey(dummy)) {
            friends = graph.get(dummy);
        } else {
            friends = new HashSet<Person>();
        }
        map.put("friends", friends);
        return map;
    }


    public Map mutual(String userId1, String userId2) {

        Person p1 = new Person(userId1, "");
        Person p2 = new Person(userId2, "");

        Set<Person> p1Friends = null;
        Set<Person> p2Friends = null;

        if (graph.containsKey(p1)) {
            p1Friends = graph.get(p1);
        }
        if (graph.containsKey(p2)) {
            p2Friends = graph.get(p2);
        }

        if (p1Friends != null && p2Friends != null) {
            p1Friends.retainAll(p2Friends);
        } else {
            p1Friends.clear();
        }

        Map<String, Set<Person>> map = new HashMap<String, Set<Person>>();
        map.put("mutuals", p1Friends);
        return map;

    }

    public Map degrees(String ID1, String ID2) {

        Deque<PersonDegrees> q = new ArrayDeque<PersonDegrees>();
        int length = -1;
        Person p1 = new Person(ID1, "");
        PersonDegrees pd1 = new PersonDegrees(p1, 0);
        q.addFirst(pd1);
        Map<String, Boolean> visited = new HashMap<String, Boolean>();
        visited.put(ID1, true);

        while (!q.isEmpty()) {
            PersonDegrees currPD = q.pollLast();
            Person currP = currPD.getP();
            Set<Person> list = graph.get(currP);
            for (Person nextP : list) {
                PersonDegrees nextPD = new PersonDegrees(nextP, currPD.getLen() + 1);
                if (!visited.containsKey(nextP.getId())) {
                    q.addFirst(nextPD);
                    visited.put(nextP.getId(), true);
                    if (nextP.getId().equals(ID2)) {
                        length = nextPD.getLen();
                        break;
                    }
                }
            }
        }

        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        List<Integer> list = new ArrayList<Integer>();
        list.add(length);
        map.put("degrees", list);
        return map;


    }

    static void printUsage() {
        System.out.println("----------------------");
        System.out.println("Friend finder ");
        System.out.println("----------------------");
        System.out.println("friendFinder filename user_id : Take the file path as the 1st argument followed by a user id as the 2nd argument then output a file containing the user’s friends.");
        System.out.println("○ Name the output file “friends.json.” You should output a JSON object with an array called friends, where each item in the list is a JSON object with keys “id” and “name.”");
        System.out.println("friendFinder filename user_id1 user_id2  : Take the file path as the 1st argument followed by two user ids as 2nd and 3rd arguments, then output a file containing the users’ mutual friends.");
        System.out.println("○ Name the output file “mutuals.json.” You should output a JSON object with an array called mutuals, where each item in the list is a JSON object with keys “id” and “name.”");
    }

    static void writeToFile(Map map, String fileName) {
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            FileWriter writer = new FileWriter(fileName);
            writer.write(gson.toJson(map));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        if (args.length < 2 || args.length > 4) {
            printUsage();
            System.exit(0);
        }
        FriendshipGraph obj = new FriendshipGraph();
        try {
            obj.loadFrom(args[0]);
            Map result = null;
            String outputFile = null;
            if (args.length == 2) {
                result = obj.friends(args[1]);
                outputFile = "friends.json";
            } else if (args.length == 3) {
                result = obj.mutual(args[1], args[2]);
                outputFile = "mutuals.json";
            } else {
                if (args[3].equals("bacon")) {
                    result = obj.degrees(args[1], args[2]);
                    outputFile = "bacon.json";
                }
            }
            if (result != null) {
                writeToFile(result, outputFile);
            } else {
                System.out.println("There was an error processing the file");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File : " + args[0] + " not found.");
        }
    }

}
