package com.ajith.service;

public interface HierarchyService {
	void createRootRole(String roleName);
	void createSubRole(String subRole, String reportingToRole);
	void display();
	void deleteRole(String roleToDelete, String roleToTransfer);
	void addUser(String userName,String roleName);
	void displayUsers();
	void displayUsersAndSubUsers();
	void deleteUser(String user);
	void findUsersFromTop(String user);
	int findHeight(String root);
	void findCommonBossOfUsers(String user1,String user2);
}
