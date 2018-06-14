package com.mycompany.friendshipgraph;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Relation {
	private Person to;
	private Person from;	
	private String timestamp;
	private boolean areFriends;
}
