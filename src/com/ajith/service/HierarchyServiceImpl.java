package com.ajith.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HierarchyServiceImpl implements HierarchyService{

	private String root;
	private LinkedHashMap<String,String> user_and_role = new LinkedHashMap<String,String>();
	private LinkedHashMap<String,List<String>> role_and_subroles = new LinkedHashMap<String,List<String>>();
	private LinkedHashMap<String,List<String>> role_and_users = new LinkedHashMap<String,List<String>>();
	private LinkedHashMap<String,String> role_and_parent = new LinkedHashMap<String,String>();
	
	@Override
	public void createRootRole(String roleName) {
		//add in role_
		role_and_subroles.put(roleName, new ArrayList<String>());
		role_and_users.put(roleName, new ArrayList<String>());
		role_and_parent.put(roleName, null);
		root = roleName;
		System.out.println("Root: "+root);
	}
	
	@Override
	public void createSubRole(String subRole, String reportingToRole) {
		if(role_and_subroles.containsKey(subRole)) {
			System.out.println("Role already found");
			return;
		}
		if(role_and_subroles.containsKey(reportingToRole)) {
			//creating a new role in role_and_subroles map
			role_and_subroles.put(subRole, new ArrayList<String>());
			
			//creating a new role in user_and_role map
			role_and_users.put(subRole, new ArrayList<String>());
			
			//updating role and its super role in role_and_super_role map
			role_and_parent.put(subRole, reportingToRole);
			
			//getting subrole_and_subroles for reporting to role
			List<String> subrole_and_subroles = role_and_subroles.get(reportingToRole);
			
			subrole_and_subroles.add(subRole);
			//adding it as a sub role to the reporting role
			role_and_subroles.put(reportingToRole, subrole_and_subroles);
			
			System.out.println("Role: "+subRole+" created successfully");
		}
		else {
			System.out.println("Reporting to role is not present");
			return;
		}
		
	}
	
	@Override
	public void display() {
		StringBuffer sb = new StringBuffer();
		Queue<String> q = new LinkedList<String>();
		q.offer(root);
		while(!q.isEmpty()) {
			 String role = q.remove();
			 sb.append(role+", ");
			 for(String subRole : role_and_subroles.get(role))
				 q.offer(subRole);
		}
		if(sb.length()>2)
			System.out.println(sb.substring(0, sb.length()-2)+".");
	}
	
	@Override
	public void deleteRole(String roleToDelete, String roleToTransfer) {
		boolean rolePresent = role_and_subroles.containsKey(roleToDelete);
		if(!rolePresent) { 
			System.out.println("Role you want to delete is not present. ");
			return;
		}
		
		// children of role to be delete
		List<String> childrenOfRoleToBeRemoved = role_and_subroles.get(roleToDelete);
		//get the super role of roleToDelete
		String superRole = role_and_parent.get(roleToDelete);
		//children of super role
		List<String> childrenOfSuperRole = null;
		if(superRole!=null)
			childrenOfSuperRole = (superRole!=null)?(role_and_subroles.get (superRole)):(null);

		if(role_and_subroles.containsKey(roleToTransfer)) {
			
			//1. roleToTransfer is child of roleToDelete			
			
			if(childrenOfRoleToBeRemoved.contains(roleToTransfer)) {
				//remove the roleToTransfer from children of roleToDelete
				childrenOfRoleToBeRemoved.removeIf(role -> role.equals(roleToTransfer));					
			}
			//2. roleToTransfer is sibling of roleToDelete
			
			// it means super role of roleToDelete contains roleToTransfer
			else if(childrenOfSuperRole!=null && childrenOfSuperRole.contains(roleToTransfer)) {
				//remove the roleToTransfer from children of super role first
				childrenOfSuperRole.removeIf(role -> role.equals(roleToTransfer));
			}
			
			//3. roleToTransfer has no relation as child or sibling..
			else {
				//check whether roleToTransfer is available in roleToDelete's sub children
				boolean available = false;
				//to do this get the parent of roleToTransfer until it's null
				String par = role_and_parent.get(roleToTransfer);
				
				while(par!=null) {
					if(par.equals(roleToDelete))
						available = true;
					par = role_and_parent.get(par);
				}
				// if it's available then its just a replacement
				if(available) {
					//remove the roleToTransfer from it's(roleToTransfer) parent first
					String parentTransfer = role_and_parent.get(roleToTransfer);
					if(parentTransfer!=null)
						role_and_subroles.get(parentTransfer).removeIf(role -> role.equals(roleToTransfer));
				}
				//else we need to cut this branch and join to roleToTransfer
				else {
					//remove the roleToDelete from it's(roleToDelete) parent first
					String parentDelete = role_and_parent.get(roleToDelete);
					if(parentDelete!=null)
						role_and_subroles.get(parentDelete).removeIf(role -> role.equals(roleToDelete));

					//get the users of roleToDelete
					List<String> usersOfRoleToDelete = role_and_users.get(roleToDelete);
					
					//attach children of role to be deleted and users of role to be deleted to the role to be transferred
					role_and_subroles.get(roleToTransfer).addAll(childrenOfRoleToBeRemoved);
					
					role_and_users.get(roleToTransfer).addAll(usersOfRoleToDelete);
					
					removeRoleFromMaps(roleToDelete);
					
					//update the users map with roleToTransfer role
					updateUsersWithRole(usersOfRoleToDelete, roleToTransfer);
				
					System.out.println("Role: "+roleToDelete+" deleted successfully");
					return;
				}
				
			}
			
		}
		else {			
			//we need to create roleToTransfer as new role as it's not present
			role_and_subroles.put(roleToTransfer, new ArrayList<String>());
			//creating a new role in user_and_role map
			role_and_users.put(roleToTransfer, new ArrayList<String>());
						
		}
		//add the children of roleToDelete to the role to be transferred
		role_and_subroles.get(roleToTransfer).addAll(childrenOfRoleToBeRemoved);
		
		if(superRole!=null) {
			//get the index of the role to delete
			int index = childrenOfSuperRole.indexOf(roleToDelete);		
			
			//replace the roleToDelete with roleToTransfer
			role_and_subroles.get(superRole).set(index, roleToTransfer);
			
		}
		else  
			root = roleToTransfer;
		
		//update the super role of the roleToTransfer
		role_and_parent.put(roleToTransfer, superRole);
			
		//get the user_and_role of roleToDelete
		List<String> usersOfDeletedRole = role_and_users.get(roleToDelete);
			
		//update the user_and_role to roleToTransfer
		role_and_users.get(roleToTransfer).addAll(usersOfDeletedRole);
		
		//update the users map with roleToTransfer role
		updateUsersWithRole(usersOfDeletedRole, roleToTransfer);
			
		removeRoleFromMaps(roleToDelete);
		
		System.out.println("Role: "+roleToDelete+" deleted successfully");
	}
	
	@Override
	public void addUser(String userName,String roleName) {
		if(user_and_role.get(userName) != null) {
			System.out.println("Username already found");
			return;
		}
		if(role_and_subroles.containsKey(roleName) == false) {
			System.out.println("Role not found");
			return;
		}
		user_and_role.put(userName, roleName);
		List<String> userRole = role_and_users.get(roleName);
		userRole.add(userName);
		role_and_users.put(roleName, userRole);
		System.out.println("Username added successfully to the role: "+roleName);
	}

	@Override
	public void displayUsers() {
		for(String user: user_and_role.keySet())
			System.out.println(user+" - "+user_and_role.get(user));
	}

	@Override
	public void displayUsersAndSubUsers() {
		for(String role:role_and_users.keySet()) {
			for(String user: role_and_users.get(role)) {
				System.out.print(user+" - ");
				displayFromRoot(role);
				System.out.println();
			}
		}
	}
	
	@Override
	public void deleteUser(String user) {
		//first check whether the user is present
		String role = user_and_role.get(user);
		//if user is not present role is null
		if(role == null) {
			System.out.println(user+" is not present.");
			return;
		}
		//if user is present
		//delete user from user_and_role
		user_and_role.remove(user);
		//delete user from its corresponding role
		role_and_users.get(role).remove(user);
		
		System.out.println("User: "+user+" deleted successfully");
	}

	@Override
	public void findUsersFromTop(String user) {
		//check if user is present or not
		if(!user_and_role.containsKey(user)) {
			System.out.println(user+" is not present. Please enter correct name");
			return;
		}
		//find the role of the user 
		String roleOfUser = user_and_role.get(user);
		int noOfUsers = 0;
		
		//backtrack from the current role's parent until the role is null
		String role = role_and_parent.get(roleOfUser);
		
		while(role!=null) {
			//add no of users present in the top role to the no of users
			noOfUsers += role_and_users.get(role).size();
			role = role_and_parent.get(role);
		}

		//users from top
		System.out.println("Number of users from top: "+noOfUsers);
	}
	
	@Override
	public int findHeight(String root) {
		if(role_and_subroles.get(root).isEmpty())
			return 1;
		int max = 0;
	    for (String subRole  : role_and_subroles.get(root)) 
	    	max = Math.max(max, 1+findHeight(subRole));
	    return max;
	}

	@Override
	public void findCommonBossOfUsers(String user1,String user2) {
		//first check whether user1 and user2 are present or not
		if(!user_and_role.containsKey(user1)) {
			System.out.println(user1+" is not present. Please enter correct name");
			return;
		}
		if(!user_and_role.containsKey(user2)) {
			System.out.println(user2+" is not present. Please enter correct name");
			return;
		}
			
		//first get their roles
		String role1 = user_and_role.get(user1);
		String role2 = user_and_role.get(user2);
		
		LinkedHashSet<String> reportingsOfUser1 = new LinkedHashSet<String>();
		 
		String commonRole = root;
		//until reporting to is CEO, append the roles in reportingsOfUser1
		while(!role1.equals(root)) {
			reportingsOfUser1.add(role1);
			role1 = role_and_parent.get(role1);
		}
		
		//traverse until reporting to is CEO
		while(!role2.equals(root)) {
			//if reportingsOfUser1 contains the parent of role2,then it's the common reporting role
			if(reportingsOfUser1.contains(role2)) {
				commonRole = role2;
				break;
			}
			role2 = role_and_parent.get(role2);
		}
		
		List<String> topMostBoss = new ArrayList<String>();
		//add users of common role to topMostBoss
		topMostBoss.addAll(role_and_users.get(commonRole));
		
		System.out.print("Top most common boss : ");
		//print the topMostBoss
		topMostBoss.forEach(user->System.out.println(user+" "));
	}	
	
	private void removeRoleFromMaps(String roleToDelete) {
		//remove the roleToDelete from role_and_users
		role_and_users.remove(roleToDelete);
		
		//remove the roleToDelete from role_and_subroles map
		role_and_subroles.remove(roleToDelete);
		
		//remove the roleToDelete from role_and_parent map
		role_and_parent.remove(roleToDelete);
		
	}
	
	private void updateUsersWithRole(List<String> users,String role) {
		for(String user : users) 
			user_and_role.put(user,role);
	}
	
	private void displayFromRoot(String root) {
		StringBuffer sb = new StringBuffer();
		Queue<String> q = new LinkedList<String>();
		q.offer(root);
		while(!q.isEmpty()) {
			 String role = q.remove();
			 for(String subRole : role_and_subroles.get(role)) {
				 for(String user: role_and_users.get(subRole)) 
					 System.out.print(user+", ");
				 q.offer(subRole);
			 }
		}
		if(sb.length()>2)
			System.out.println(sb.substring(0, sb.length()-2)+".");
	}
}
