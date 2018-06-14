Objective: 

To create a CLT which can take a text file as an input containing JSON data about friendships and produces the following 3 kinds of outputs:
1. Given a user ID, return back all the friends of that user ID and output the result to friends.json
2. Given two user IDs, return their mutual friends and output the result to mutuals.json
3. Given two user IDs and the 4th argument as bacon, return the degree of friendship between the two users and output the result to bacon.json

Additionally, a REST endpoint is created which will return friends of a given User ID. Sample URL: http://localhost:3000/friends?id=F0%2FSs%2BJKEEA%3D

Note: The user ID needs to be URL encoded. 

Architecture:

I used an undirected graph to depict all the friendships. I used a Map to store the list of friends corresponding to a user ID. The graph is created the first time the URL is accessed, making the subsequent calls faster.

For friend list function, I just need to retrieve the list of friends corresponding to the user ID in the Map.

For mutual friends, I used the retainAll function in Set to get the intersection of the two user IDs.

For bacon, I calculated the degree of friendship between two user IDs by using Breadth First Search, starting from the given user ID.

Technologies used:

Lombok for getters and setters
Maven for project dependencies
Jersey for developing RESTful webservice
Jetty provides a web server

How to run:

For jetty

mvn clean package exec:java

curl http://localhost:3000/friends?id=4vYVN%2BFxwNM%3D

For standalone command line

mvn -f pom.standalone.xml clean package


#To see usage

cd target; java -jar HouseParty-jar-with-dependencies.jar
cd ..

read -p "Press enter to continue"

#To see friend list

cd target; java -jar HouseParty-jar-with-dependencies.jar ../src/main/resources/friends.txt 4vYVN+FxwNM=
cd ..
cat target/friends.json
read -p "Press enter to continue"

#To see mutual friend list

cd target; java -jar HouseParty-jar-with-dependencies.jar ../src/main/resources/friends.txt 4vYVN+FxwNM= GKYV7NODVj4=
cd ..
cat target/mutuals.json
read -p "Press enter to continue"

#Bacon assignment

cd target; java -jar HouseParty-jar-with-dependencies.jar ../src/main/resources/friends.txt 4vYVN+FxwNM= GKYV7NODVj4= bacon
cd ..
cat target/bacon.json
read -p "Press enter to continue"
