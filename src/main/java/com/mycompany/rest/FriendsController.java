package com.mycompany.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycompany.friendshipgraph.FriendshipGraph;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;


@Path("/friends")
public class FriendsController {

    static FriendshipGraph friendshipGraph;

    static {
        try {
            friendshipGraph = new FriendshipGraph();
            ClassLoader classLoader = FriendsController.class.getClassLoader();
            InputStream is = classLoader.getResourceAsStream("friends.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            friendshipGraph.loadFrom(br);
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unable to load the file");
            System.exit(-1);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response friendList(@QueryParam("id") String id) {

        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Arguments. User id not valid").build();
        }
        id = id.trim();
        if (id.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Arguments. User id not valid").build();
        }



        Map map = friendshipGraph.friends(id);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return Response.ok(gson.toJson(map)).build();

    }
}
