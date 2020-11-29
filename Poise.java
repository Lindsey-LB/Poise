
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This program manages the project data of a structural engineering company's projects,
 * which are contain in a database.
 * 
 * The program is able to add new projects, edit existing projects, view projects and finalise
 * projects.
 * 
 * @author Lindsey
 * @see Project - Class for project objects
 * @see ProjectContact - Class for project contact objects
 */
public class Poise {

	/**
	 * Imports a list of project objects for editing and recording/creating new project objects.
	 * <p>
	 * Uses Scanner class to get inputs from user.
	 * <p>
	 * Connects to poisePMS database using mySQL.
	 * <p>
	 * The main menu allow a user to add a new project, edit a project, view a selected project
	 * view incomplete projects and view overdue projects. The edit menu allows a user to change
	 * the project deadline, add a payment to the total amount paid, edit the contractor details, 
	 * or finalise a project in the poisePMS database
	 * 
	 * @param args The command line arguments
	 */
	public static void main(String[] args){
		// Scanner for inputs from user
		Scanner userInput = new Scanner(System.in);

		// Introductory message
		System.out.println("Welcome to the Poise Project Manager!");

		// Connect to database
		Connection connection = connectToDatabase();

		try {
			/* Direct line to poisePMS database to run queries
			 * and execute updates*/
			Statement statement = connection.createStatement();

			// Projects list for viewing project data
			ArrayList<Project> projects = getProjects(statement);

			// Initiate value for main menu
			int mainChoice = 0;

			while(mainChoice != 6) {
				displayMainMenu();
				mainChoice = getMenuChoice(userInput, mainChoice);

				switch(mainChoice) {
				// Main menu options:
				case 1:
					// Add a new project
					addProject(userInput, projects, statement, connection);
					break;
				case 2:
					//Use selectProject method to return the selected project
					Project project = selectProject(projects, userInput);

					// Initiate value to control loop
					int editChoice = 0;

					/* Edit menu is set to display and ask for menu selection 
					 * after each action, to allow for multiple changes
					 * of the same project.*/
					while ((editChoice != 4) && (editChoice != 5)) {
						displayEditMenu();

						editChoice = getMenuChoice(userInput, editChoice);

						switch(editChoice) {
						case 1:
							updateDeadline(project, userInput, statement);
							break;
						case 2:
							updatePaid(project, userInput, statement);
							break;
						case 3:
							updateContractor(project, userInput, statement, connection);
							break;
						case 4:
							finaliseProject(project, statement);
							break;
						case 5:
							// Exits edit menu
							break;
						default:
							System.out.println("Error! Please enter a valid edit menu option.");
						}
					}
					break;
				case 3:
					// Select and view a project
					printSelectedProject(projects, userInput);
					break;
				case 4:
					// View all projects with no completion date value
					printIncompleteProjects(projects);
					break;
				case 5:
					// View all projects whose deadline is in the past
					printOverdueProjects(projects);
					break;
				case 6:
					System.out.println("Exit program.");
					break;
				default:
					System.out.println("Error! Please enter a valid menu option.");
				}
			}
			// Close connections
			userInput.close();
			connection.close();
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	// METHODS  ---------------------------->
		
	/**
	 * Makes connection to the poisePMS database.
	 * <p>
	 * @return the connection to poisePMS
	 */
	public static Connection connectToDatabase() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/poisepms?useSSL=false",
					"otheruser",
					"swordfish");
		} catch (SQLException e) {
			System.out.println("The connection to the database failed.");
			e.printStackTrace();
		}
		return connection;
	}	
	
	/**
	 * Creates an array list of project objects, using the values from the poisePMS database. 
	 * <p>
	 * The values extracted from the poisePMS database are contained in 5 tables - projects,
	 * sites, customers, contractors and architects. These tables are combined using INNER JOIN
	 * when the query is run, which enables all relevant project data to be accessed in one query.
	 * <p>
	 * Once all project values have been accessed, the values are saved as variables. 
	 * The variables are used to create the project contact objects, and the related project 
	 * objects. These objects are then added to the array list.
	 * <p>
	 * @param statement The line to the database for running queries
	 * @return The array list containing all existing project objects listed in the database.
	 * @throws SQLException - If a database error occurs.
	 */
	public static ArrayList<Project> getProjects(Statement statement) throws SQLException {
		// For project objects to be added.
		ArrayList<Project> projects = new ArrayList<>();
		
		// Get all results from the database required to create project objects
        ResultSet results = statement.executeQuery("SELECT projects.PROJECTNUM, projects.projectName, "
        		+ "projects.BUILDTYPE, projects.ERFNUM, sites.Address, projects.TOTALFEE, "
        		+ "projects.totalpaid, projects.deadline, projects.customer, customers.Telephone, "
        		+ "customers.Email, customers.Address, projects.contractor, contractors.Telephone, "
        		+ "contractors.Email, contractors.Address, projects.architect, architects.Telephone,"
        		+ "architects.Email, architects.Address, projects.projectManager, projects.completionDate "
        		+ "FROM ((((projects JOIN sites on projects.ERFNUM = sites.ERFNUM) "
        		+ "INNER JOIN customers on projects.customer = customers.customer) "
        		+ "INNER JOIN contractors on projects.contractor = contractors.contractor)"
        		+ "INNER JOIN architects on projects.architect = architects.architect)");

        // Loop through results and save project values as variables
        while (results.next()) {
        	int PROJECTNUM = results.getInt("projects.PROJECTNUM");
        	
        	String projectName = results.getString("projects.projectName");
        	
        	String BUILDTYPE = results.getString("projects.BUILDTYPE");
        	
        	int ERFNUM = results.getInt("projects.ERFNUM");
        	
        	String ADDRESS = results.getString("sites.ADDRESS");
        	
        	double TOTALFEE = results.getDouble("projects.TOTALFEE");
        	
        	double totalPaid = results.getDouble("projects.totalPaid");
        	
        	Date deadline = results.getDate("projects.deadline");
        	
        	String customerName = results.getString("projects.customer");
        	String customerPhone = results.getString("customers.Telephone");
        	String customerEmail = results.getString("customers.Email");
         	String customerAddress = results.getString("customers.Address");
        	String customerType = "Customer";
        	
        	// Create customer object for the project
        	ProjectContact customerContact = new ProjectContact(customerType, customerName, customerPhone, customerEmail, customerAddress);
        	
        	String contractorName = results.getString("projects.contractor");
        	String contractorPhone = results.getString("contractors.Telephone");
        	String contractorEmail = results.getString("contractors.Email");
        	String contractorAddress = results.getString("contractors.Address");
        	String contractorType = "Contractor";
        	
        	// Create contractor object for the project
        	ProjectContact contractorContact = new ProjectContact(contractorType, contractorName, contractorPhone, contractorEmail, contractorAddress);
        	
        	String architectName = results.getString("projects.architect");
        	String architectPhone = results.getString("architects.Telephone");
        	String architectEmail = results.getString("architects.Email");
        	String architectAddress = results.getString("architects.Address");
        	String architectType = "Architect";
        	
        	// Create architect object for the project
        	ProjectContact architectContact = new ProjectContact(architectType, architectName, architectPhone, architectEmail, architectAddress);
        	
        	String projectManager = results.getString("projects.projectManager");
        	
        	String completionDate = results.getString("projects.completionDate");
        	
        	// Create project object
        	Project existingProject = new Project(PROJECTNUM, projectName, BUILDTYPE, ERFNUM, ADDRESS, TOTALFEE, totalPaid, deadline, customerContact, contractorContact, architectContact, projectManager, completionDate);
        	
        	// Add the project to the array list
        	projects.add(existingProject);
        }
        
        return projects;
    }
	
	// METHODS - CREATE PROJECT ---->
	// Takes list of project strings and converts them to project objects
	/**
	 * Creates a new Project Contact object by using a Scanner to get the required attribute values from the user.
	 * 
	 * @param userInput The Scanner which will be used for the user input values.
	 * @param contact The contact value which can be either "Customer", "Contractor" or "Architect".
	 * @return The new Project Contact object
	 */
	public static ProjectContact newContact(Scanner userInput, String contact) {
			System.out.println("Contact Details - " + contact);
			
			System.out.print("Name: ");
			String contactName = userInput.nextLine();
			System.out.print("Tel. Number: ");
			String phone = userInput.nextLine();
			System.out.print("E-mail: ");
			String email = userInput.nextLine();
			System.out.print("Physical Address: ");
			String contactAddress = userInput.nextLine();
			
			// Use fields to create object
			ProjectContact newContact = new ProjectContact(contact, contactName, phone, email, contactAddress );
			return newContact;
	}

	/**
	 * Parses a string date input to the required Date value. 
	 * <p>
	 * The method is used for dates received from the user.
	 * The string must be entered in format "yyyy-MM-dd". 
	 * <p>
	 * @param newDateString The string date input value. 
	 * @return The Date value for the String date input value.
	 */
	private static Date formatDateString(String newDateString) {
		// Converts date strings of format yyyy-MM-dd to a date value
		Date newDate = null;
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			newDate = dateFormatter.parse(newDateString);
		} catch (ParseException e) {
			System.out.println("Error! Date format incorrect.");
		}
		return newDate;
	}	
	
	/**
	 * Prompts user to input project attribute values for a new project object, 
	 * constructs the new project object and adds it to the array list of projects
	 * and updates the poisePMS database.
	 * <p>
	 * Project number entries are limited to unique values. Any entry is compared
	 * to the existing project numbers before they are accepted. Invalid entries
	 * restart the loop for the user to enter the value.
	 * <p>
	 * ERF numbers, customer names, contractor names and architect names are limited to
	 * unique values for new entries to the database. If a duplicate is entered,
	 * the details of the duplicated entry will not be saved to the database.
	 * <p>
	 * Caught exceptions restart the loops for user entries which need to be parsed.
	 * <p>
	 * @param userInput The Scanner used to read user inputs.
	 * @param projects The array list of projects.
	 * @param statement The line to the database to run queries.
	 * @param connection The connection to the database used to control changes committed.
	 * @throws SQLException If the method is called on a closed connection.
	 */
	public static void addProject(Scanner userInput, ArrayList<Project> projects, Statement statement, Connection connection) throws SQLException {
		//Declare & initiate variables for project object
		int projectNum = 0, ERFNum = 0;
		String address, buildType, projectName, projectManager, deadlineString = null, completionDate = null;
		ProjectContact customer = null, contractor = null, architect = null;
		double totalFee = 0, totalPaid = 0;
		Date deadline = null;

		// Introductory message
		System.out.println();
		System.out.println("\t***Add New Project***");
		System.out.println();
		System.out.println("Please provide project details.");
		
		// PROJECTNUM - value
		while(projectNum == 0) {
			int newProjectNum;
			System.out.print("Project number: ");
			String projectNumString = userInput.nextLine();
			int count = 0;
			
			try{
				newProjectNum = Integer.parseInt(projectNumString);
				for(Project counter: projects) {
					if(newProjectNum == counter.getProjectNum()){
						System.out.println("That project number already exists.");
						break;
					}
					else {
						count += 1;
					}
				}
				if(count == projects.size()) {
						projectNum = newProjectNum;
					}
			}catch(NumberFormatException e) {
				System.out.println("Error! Only integers are to be entered.");
			}
		}
		
		// BUILDTYPE - value
		System.out.print("Building type: ");
		buildType = userInput.nextLine();
		
		// ERFNUM - value
		while(ERFNum == 0) {
			System.out.print("ERF Number: ");
			String ERFNumString = userInput.nextLine();
			try {
				ERFNum = Integer.parseInt(ERFNumString);
			}catch(NumberFormatException e) {
				System.out.println("Error! Only integers are to be entered.");
			}
		}
		
		// ADDRESS - value
		System.out.print("Project address: ");
		address = userInput.nextLine();
		
		//TOTALFEE - value
		while(totalFee == 0) {
			System.out.print("Total Fee: R ");
			String feeInput = userInput.nextLine();
			try {
				totalFee = Double.parseDouble(feeInput);
			}catch(NumberFormatException e) {
				System.out.println("Error! Only integers or decimals are to be entered for fees.");
			}
		}
		
		//TOTALPAID - value
		while(totalPaid == 0) {
			System.out.print("Total Paid: R ");
			String paidInput = userInput.nextLine();
			try{
				totalPaid = Double.parseDouble(paidInput);
			}catch(NumberFormatException e) {
				System.out.println("Error! Only integers or decimals are to be entered for fees.");
			}
		}
		
		// DEADLINE - value
		while(deadline == null) {
			System.out.print("Project deadline (YYYY-MM-DD): ");
			deadlineString = userInput.nextLine();
			deadline = formatDateString(deadlineString);
		}
		
		// CUSTOMER - Object
		String customerContact = "Customer";
		customer = newContact(userInput, customerContact);
		
		// CONTRACTOR - Object
		String contractorContact = "Contractor";
		contractor = newContact(userInput, contractorContact);
		
		// ARCHITECT - Object
		String architectContact = "Architect";
		architect = newContact(userInput, architectContact);
		
		// PROJECTNAME - auto-assignment option incl.
		System.out.print("Enter project name: ");
		String projectNameTemp = userInput.nextLine();

		// PROJECTNAME - For empty input
		if(projectNameTemp.equals("")) {
			String custName = customer.contactName;
			String[] splitName = custName.split(" ");
			try {
				projectName = buildType + " " + splitName[1];
			}catch(ArrayIndexOutOfBoundsException error) {
				projectName = buildType + " " + splitName[0];
			}
		}
		// PROJECTNAME - For project input from user
		else {
			projectName = projectNameTemp;
		}
		
		//PROJECT MANAGER - Value
		System.out.print("Project Manager: ");
		projectManager = userInput.nextLine();
		
		// Variable used to control update of array list
		boolean  successfulUpdate = false;
		
		Savepoint savepoint = null;
		
		try {
			// Deactivate auto commit for manual commit after successful update.
			connection.setAutoCommit(false);
			
			// Set savepoint for rollback if required.
			savepoint = connection.setSavepoint("Savepoint");
			
			//Update tables
			statement.executeUpdate("INSERT INTO sites "
					+ "VALUES('"+ERFNum+"','"+address+"')");
			
			statement.executeUpdate("INSERT INTO customers "
					+ "VALUES('"+customer.getContactName()+"', '"+customer.getPhone()+"','"
					+customer.getEmail()+"','"+customer.getContactAddress()+"')");
			
			statement.executeUpdate("INSERT INTO contractors "
					+ "VALUES('"+contractor.getContactName()+"', '"+contractor.getPhone()+"','"
					+contractor.getEmail()+"','"+contractor.getContactAddress()+"')");
			
			statement.executeUpdate("INSERT INTO architects "
					+ "VALUES('"+architect.getContactName()+"', '"+architect.getPhone()+"','"
					+architect.getEmail()+"','"+architect.getContactAddress()+"')");
			
			// Projects must be updated last, due to foreign keys
			statement.executeUpdate("INSERT INTO projects "
					+ "VALUES('"+projectNum+"','"+projectName+"','"+buildType+"','"+ERFNum
					+"','"+totalFee+"','"+totalPaid+"','"+deadlineString+"','"
					+customer.getContactName()+"', '"+contractor.getContactName()+"','"
					+architect.getContactName()+"', '"+projectManager+"', NULL)");

			// Commit changes
			connection.commit();

			successfulUpdate = true;
		
		} catch (SQLException e) {
				System.out.println("Not all data for this project was accepted "
						+ "to the database.\nPlease try again.");
				connection.rollback(savepoint);
		}
		if (successfulUpdate) {
			// Update array list once the database is updated
			Project newProject = new Project(projectNum, projectName, buildType, ERFNum, address, totalFee, totalPaid, deadline, customer, contractor, architect, projectManager, completionDate);
			System.out.println("The new project - " + newProject.getProjectName() + " - has been added.");
			System.out.println();
			System.out.println(newProject);
			projects.add(newProject);
		}
		// Restore auto commit once complete.
		connection.setAutoCommit(true);
	}
	
	/**
	 * Prints the main menu.
	 * <p>
	 * The main menu prints after each main menu function has completed.
	 */
	private static void displayMainMenu() {
		System.out.print("\n-----------Main Menu-----------\n1 - Add New Project\n"
				+ "2 - Edit/Finalise Project\n3 - View a Selected Project"
				+ "\n4 - View Incomplete Projects\n5 - View Overdue Projects\n"
				+ "6 - Exit\n\nPlease make your selection: ");
	}
	
	// METHODS - EDIT PROJECT 	---->																			

	/**
	 * Selects a project from the project objects list to edit or finalise.
	 * <p>
	 * Projects can be selected using their name or number. The method first tests
	 * for a project number entry. If the entry cannot be parsed to an integer,
	 * the entry is treated as the project name. The projects array list is then
	 * searched for a matching value. 
	 * <p>
	 * The project with the matching value is saved as the selected project object to be edited,
	 * only if the project is not finalised (the corresponding completion date is not set).
	 * <p>
	 * Only projects that are not finalised can be selected to edit.
	 * @param projects The array list of project objects.
	 * @param userInput The Scanner for the user to input the selection.
	 * @return The selected project object to be edited.
	 * @exception NumberFormatException The exception is handled with a try-catch block.
	 */
	private static Project selectProject(ArrayList<Project> projects, Scanner userInput) {
		Project selectedProject = null;
		
		System.out.println();
		System.out.println("Please select a project for editing.");
		
		while(selectedProject == null) {
			//Ask user to enter the project name or number
			System.out.print("Project number/name: ");
			String projectSelection = userInput.nextLine();
			try {
				// Parse to an integer to check if the project number was entered.
				int projectNumberSelection = Integer.parseInt(projectSelection);
				// Check if there is a project in the projects array list...
				for(int i = 0; i < projects.size(); i++) {
					// ...that has the project number...
					if(projectNumberSelection == projects.get(i).getProjectNum()) {
						// ...and is incomplete (method is only used for editing)...
						if((projects.get(i).getCompletionDate()) == null) {
							//...and save the project if a match is found.
							selectedProject = projects.get(i);
						}
						else {
							// Error message for matches that are complete.
							System.out.println("Finalised projects cannot be edited.");
						}
					}
				}
				// Error message for no matches found
				if (selectedProject == null) {
					System.out.println("That project does not exist. Please try again.");
				}
			// If the entry was not a number (the parse failed)	
			}catch(NumberFormatException e) {
				// The entry is checked as a project name.
				for(int i = 0; i < projects.size(); i++) {
					if(projectSelection.equalsIgnoreCase(projects.get(i).getProjectName())) {
						if((projects.get(i).getCompletionDate()) == null) {
							selectedProject = projects.get(i);
						}
					}
				}
				// Error message for no matches found
				if (selectedProject == null) {
					System.out.println("That project does not exist. Please try again.");
				}
			}
		}
		return selectedProject;
	}
	
	/**
	 * Prints the edit menu.
	 * <p>
	 * The edit menu prints whenever a function is completed from the edit menu.
	 */
	private static void displayEditMenu() {
		System.out.print("\n--------Edit Menu--------\n1 - Change Deadline\n2 - Add Payment\n3 - Update Contractor Details"
				+ "\n4 - Finalise Project\n5 - Escape to Main Menu\n\nPlease make your selection: ");
	}

	/**
	 * Gets menu choice from the Scanner and updates the menu choice value.
	 * <p>
	 * The method takes the String menu choice and parses to save the integer value as
	 * the new menuChoice. 
	 * <p>
	 * @param userInput The Scanner used to get the user's menu choice.
	 * @param menuChoice The integer value for the menu choice. Set to default value of 0.
	 * @return The integer for the updated menu choice.
	 * @exception NumberFormatException The exception is handled by a try-catch block
	 */
	private static int getMenuChoice(Scanner userInput, int menuChoice) {
		String editChoiceStr = userInput.nextLine();
		try{
			menuChoice = Integer.parseInt(editChoiceStr);
		}catch(NumberFormatException e) {
			System.out.println("Error! Please enter a valid menu option.");
		}
		return menuChoice;
	}
	
	/**
	 * Updates the deadline value for the selected project object.
	 * <p>
	 * The method uses a scanner to get a new deadline from the user,
	 * and updates the database. The method then uses the formatDateString method
	 * to parse it to a new date value. The new date value is set 
	 * as the deadline for the selected project object.
	 * <p>
	 * @param project The project object selected to edit.
	 * @param userInput The Scanner used to input the  new deadline.
	 * @param statement The line to the database to run queries.
	 * @return The project object containing the updated deadline.
	 * @see formatDateString
	 */
	public static Project updateDeadline(Project project, Scanner userInput, Statement statement){
		int rowsAffected = 0;
		String newDeadlineString = null;
		
		// Headings
		System.out.println();
		System.out.println("\t***Update Deadline***");
		System.out.println();

		while(rowsAffected == 0) {
			System.out.println("Enter new project deadline(yyyy-MM-dd): ");
		
			// Get new deadline from user
			newDeadlineString = userInput.nextLine();
			try {
				rowsAffected = statement.executeUpdate(
						"UPDATE projects SET deadline= '"+newDeadlineString+"'WHERE PROJECTNUM ='"+project.getProjectNum()+"'"
						);
			} catch (SQLException e) {
				System.out.println("Error! Date format incorrect.");
			}
		}
		
		// Date value is required for update to project object
		Date newDeadline = formatDateString(newDeadlineString);
		
		//Confirm change after database has been updated
		SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd");
		String oldDeadline = dateformatter.format(project.getDeadline());
		System.out.println();
		System.out.println("The project deadline has been updated from " + oldDeadline + " to " + newDeadlineString);
		
		// Overwrite deadline
		project.setDeadline(newDeadline);
		
		return project;
	}

	/**
	 * Updates the Contractor project contact object for the selected project object.
	 * <p>
	 * @param project The project object selected to edit.
	 * @param userInput The Scanner used to input the new Contractor contact details.
	 * @param statement The line to the database to run queries.
	 * @param connection The connection to the database.
	 * @return The project object containing the updated Contractor project contact object
	 * @throws SQLException ifdatabase error occurs.
	 */
	public static Project updateContractor(Project project, Scanner userInput, Statement statement, Connection connection) throws SQLException {
		String currentContractorName = project.contractor.getContactName();
		String contact = "Contractor";
		System.out.println();
		System.out.println("\t***Update Contractor***");
		System.out.println();
		ProjectContact newContractor = newContact(userInput, contact);
		boolean successfulUpdate = false;
		
		// Initiate savepoint variable
		Savepoint savepoint1 = null;
		
		try {
			// Switch off autocommit to enable manual commit after all tables are updated
			connection.setAutoCommit(false);
			
			// Create save point to roll back changes if any of the tables fail to update
			savepoint1 = connection.setSavepoint("Savepoint1");
			
			// Add new contractor to contractors table
			statement.executeUpdate("INSERT INTO contractors VALUES('"+newContractor.getContactName()
					+"','"+newContractor.getPhone()+"','"+newContractor.getEmail()
					+"','"+newContractor.getContactAddress()+"')");
			
			// Update contractor value in projects table
			statement.executeUpdate(
					"UPDATE projects SET contractor= '"+newContractor.getContactName()
					+"'WHERE PROJECTNUM ='"+project.getProjectNum()+"'");
			
			// Delete previous contractor value
			statement.executeUpdate("DELETE from contractors "
					+ "WHERE contractor = '"+currentContractorName+"'");
			
			// Commit changes
			connection.commit();
			
			// The project object can now be updated with the new values
			successfulUpdate = true;
		
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("That contractor name already exists in the database."
					+ "\nPlease try again.");
			connection.rollback(savepoint1);
			System.out.println();
		}

		if (successfulUpdate) {
			// Overwrite contractor object
			project.contractor = newContractor;
			System.out.println("The Contractor details for " + project.getProjectName() + " have "
					+ "been updated.");
		}
		// Reinstate auto commit
		connection.setAutoCommit(true);
		return project;
	}

	/**
	 * Updates the amount paid for the project object.
	 * <p>
	 * The scanner reads the input and parses it to a double value.
	 * The value is added to the project's total amount paid. 
	 * The result of the equation is set as the selected project's new total paid value.
	 * <p>
	 * @param project The project object selected to edit.
	 * @param userInput The Scanner used to input the new payment amount.
	 * @param statement The line to the database to run queries.
	 * @return The project object containing the updated amount of fees paid
	 * @exception NumberFormatException The exception is handled by a try catch block.
	 */
	public static Project updatePaid(Project project, Scanner userInput, Statement statement) {
		// Initialise variable for the loop
		double payment = 0;
		
		// Heading
		System.out.println();
		System.out.println("\t***Update Payment***");
		System.out.println();

		// Start loop
		while(payment == 0) {
			try {
				//Get payment amount from user
				System.out.print("Enter the payment amount: R ");
				String paymentString = userInput.nextLine();
				// Cast to double for addition
				payment = Double.parseDouble(paymentString);
			}catch(NumberFormatException e) {
				// If the parse fails, the loop restarts
				System.out.println("\nError. Please enter a value in rands and cents.\n");
			}
		}
		// Update totalPaid value in project object
		double newTotalPaid = project.getTotalPaid() + payment;
		project.setTotalPaid(newTotalPaid);
		
		// Update database with new totalPaid value
		try {
			statement.executeUpdate(
					"UPDATE projects SET totalPaid= '"+project.getTotalPaid()+"'WHERE PROJECTNUM ='"+project.getProjectNum()+"'"
					);
			DecimalFormat decimalFormat = new DecimalFormat("#.00", DecimalFormatSymbols.getInstance(Locale.US));
			System.out.println("The payment of R" + decimalFormat.format(payment) + " has been added.");
		} catch (SQLException e) {
			System.out.println("Error! The database failed to update.");
		}
		
		return project;
	}

	// METHODS - FINALISE PROJECT	---->
	
	/**
	 * Prints final invoice for a completed/finalised project.
	 * <p>
	 * The test for outstanding fees compares the total amount paid to the total fee.
	 * If the total paid is less, the invoice prints.
	 * @param project The project selected to be finalised.
	 */
	private static void printInvoice(Project project) {
		// Check for outstanding fees
		if (project.totalPaid < project.TOTALFEE) {
			
			// Calculate total amount owed
			double totalOwed = project.TOTALFEE - project.totalPaid;
			
			// Print invoice
			System.out.println("\t***FINAL INVOICE***");
			System.out.println(project.customer);
			System.out.println();
			DecimalFormat decimalFormat = new DecimalFormat("#.00", DecimalFormatSymbols.getInstance(Locale.US));
			System.out.println("> Payable:\tR " + decimalFormat.format(totalOwed));
			System.out.println();
		}
	}
	
	/**
	 * Finalises project object.
	 * <p>
	 * The invoice is printed using the printInvoice method. 
	 * <p>
	 * The completion date is set as the current date at the time the project is finalised,
	 * and formatted to be displayed as "yyyy-MM-dd".
	 * <p>
	 * The project string is written to the required text file using the saveToFile method.
	 * @param project The project selected to be finalised
	 * @param statement The line to the database to run queries.
	 * @return The finalised project object
	 * @see printInvoice
	 */
	public static Project finaliseProject(Project project, Statement statement) {
		// Print invoice 
		System.out.println();
		printInvoice(project);
		
		// Get current date (format as required)
		LocalDate date = LocalDate.now();
		String completionDate = date.toString();
		
		// Set new string value for project name
		String newName = project.projectName + " (Finalised)";
		
		// Update the record in the database
		try {
			statement.executeUpdate(
						"UPDATE projects SET completionDate= '"+completionDate+"', "
								+ "projectName= '"+newName+"' WHERE PROJECTNUM ='"+project.getProjectNum()+"'"
						);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// Update completion date of object
		project.setCompletionDate(completionDate);
			
		// Update project name of object
		project.setProjectName(newName);
		
		return project;
	}
	
	// METHODS - VIEW PROJECTS		---->
	/**
	 * Allows the user to select and print a project from the projects array list to the console.
	 * <p>
	 * There is a loop to receive the project selection from the user. Invalid selections will trigger 
	 * an error message, and restart the loop.
	 * <p>
	 * @param projects The array list of project objects.
	 * @param userInput The Scanner used for the user to select a project
	 */
	public static void printSelectedProject(ArrayList<Project> projects, Scanner userInput) {
		// Initiate variable for loop
		Project selectedProject = null;
		
		// Introductory message
		System.out.println();
		System.out.println("\t***View a Project***\t");
		System.out.println();
		
		// Loop start
		// Note: Loop cannot be exited until a valid project has been
		// selected.
		while(selectedProject == null) {
			//Ask user to enter the project name or number
			System.out.println("Project number/name: ");
			String projectSelection = userInput.nextLine();
			// Assume the project number will be entered
			try {
				int projectNumberSelection = Integer.parseInt(projectSelection);
				// Iterate through the projects list
				for(int i = 0; i < projects.size(); i++) {
					// If a match is found, the project is selected
					if(projectNumberSelection == projects.get(i).getProjectNum()) {
						selectedProject = projects.get(i);
					}
				}
			// If not, the exception will trigger a search for the project name.
			}catch(NumberFormatException e) {
				for(int i = 0; i < projects.size(); i++) {
					if(projectSelection.equalsIgnoreCase(projects.get(i).getProjectName())) {
						selectedProject = projects.get(i);
					}
				}
			}
			// If no project number or names are matched, the user is notified.
			if (selectedProject == null) {
				System.out.println("That project does not exist.\nPlease try again.");
			}
		}
		// If the project was successfully found, the project prints.
		System.out.println(selectedProject);
	}
	
	/**
	 * Prints the project objects that are overdue.
	 * <p>
	 * The method first checks if the project is complete, by checking the value for the
	 * completion date. If this value is null, the project is not finalised.
	 * <p>
	 * The method then checks the deadline of the incomplete projects.
	 * If the current date is after the deadline and the completion date is empty, 
	 * the project is printed.
	 * <p>
	 * @param projects The list of project objects
	 */
	public static void printOverdueProjects(ArrayList<Project> projects) {
		// Heading
		System.out.println();
		System.out.println("\t***Overdue Projects***\t");
		System.out.println();
		
		// Get current date for comparison
		Date today = new Date();
		
		// Set counter for message if no projects are overdue.
		int count = 0;
		
		// Loops through the array list
		for(Project counter: projects) {
			// Checks if incomplete
			if(counter.getCompletionDate() == null) {
				// Checks if deadline has passed
				if(today.after(counter.getDeadline())){
					// Prints if conditions met
					System.out.println(counter);
					count +=1;
				}
			}
		}
		// Print message if no projects were printed.
		if(count == 0) {
			System.out.println("\nThere are no overdue projects.\n");
		}
	}

	/**
	 * Prints the project objects that are incomplete. 
	 * <p>
	 * To check whether the project is incomplete, the loop searches for
	 * an empty string (null value) as a completion date value.
	 * <p>
	 * @param projects The list of project objects
	 */
	public static void printIncompleteProjects(ArrayList<Project> projects) {
		// Heading
		System.out.println();
		System.out.println("\t***Incomplete Projects***\t");
		System.out.println();
		
		// Set counter for message if no projects are incomplete.
		int count = 0;
		
		// Loops through array list
		for(Project counter: projects) {
			// Checks if incomplete
			if(counter.getCompletionDate() == null) {
				// Prints if conditions met
				System.out.println(counter);
				count +=1;
			}
		}
		// Print message if no projects were printed.
		if(count == 0) {
			System.out.println("\nThere are no incomplete projects.\n");
		}
	}
	
}
