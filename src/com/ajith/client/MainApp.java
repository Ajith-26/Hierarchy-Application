package com.ajith.client;

import java.util.Scanner;

import com.ajith.service.HierarchyService;
import com.ajith.service.HierarchyServiceImpl;

public class MainApp {
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		HierarchyService service = new HierarchyServiceImpl();
		boolean exit = true;
		System.out.print("Enter root name: ");
		String rootName = input.nextLine();
		service.createRootRole(rootName);
		while(exit) {
			System.out.println("\nOperations");
			System.out.println("1. Add Sub Role");
			System.out.println("2. Display Roles");
			System.out.println("3. Delete Role");
			System.out.println("4. Add User");
			System.out.println("5. Display Users");
			System.out.println("6. Display Users and Sub Users");
			System.out.println("7. Delete User");
			System.out.println("8. Number of users from top");
			System.out.println("9. Height of role hierarchy");
			System.out.println("10. Common boss of users");
			System.out.println("11. Exit");
			System.out.print("Operation to be performed: ");
			try {
				int op = Integer.parseInt(input.nextLine());
				switch(op) {
					case 1: System.out.print("Enter sub role name: ");
							String subRole = input.nextLine();
							System.out.print("Enter reporting to role name: ");
							String reportingToRole = input.nextLine();
							service.createSubRole(subRole,reportingToRole);
							break;
					case 2: service.display();
							break;
					case 3: System.out.print("Enter the role to be deleted: ");
							String roleToDelete = input.nextLine();
							System.out.print("Enter the role to be transferred: ");
							String roleToTransfer = input.nextLine();
							service.deleteRole(roleToDelete,roleToTransfer);
							break;
					case 4: System.out.print("Enter User Name: ");
							String userName = input.nextLine();
							System.out.print("Enter Role: ");
							String role = input.nextLine();
							service.addUser(userName, role);	
							break;
					case 5: service.displayUsers();
							break;
					case 6: service.displayUsersAndSubUsers();
							break;
					case 7: System.out.print("Enter username to be deleted: ");
							String userNameToDelete = input.nextLine();
							service.deleteUser(userNameToDelete);
							break;
					case 8: System.out.print("Enter username: ");		
							String user = input.nextLine();
							service.findUsersFromTop(user);
							break;
					case 9: System.out.println("Height: "+service.findHeight(rootName));
							break;
					case 10: System.out.print("Enter user1: ");
							 String user1 = input.nextLine();
							 System.out.print("Enter user2: ");
							 String user2 = input.nextLine();
							 service.findCommonBossOfUsers(user1, user2);
							 break;
					case 11: System.out.println("Exitting the application...");
						 	 exit = false;
						 	 break;	
					default: System.out.println("Please enter a valid operation");
						 	 break;
				} 
				System.out.print("\nPress enter to continue");
				input.nextLine();
			} catch(NumberFormatException e) {
				System.out.println("Please enter a number between 1 to 11");
			} catch(Exception e) {
				e.printStackTrace();
			}		}	
		input.close();
	}
}