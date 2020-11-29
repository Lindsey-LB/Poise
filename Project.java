
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class to create Project objects.
 * 
 * Project objects are managed by Poise class.
 * 
 * @author Lindsey
 * @see ProjectContact
 * @see Poise
 *
 */
public class Project {
	
	// ATTRIBUTES
	final int PROJECTNUM;
	final String ADDRESS;
	String projectName;
	final String BUILDTYPE;
	final int ERFNUM;
	final double TOTALFEE;
	double totalPaid;
	Date deadline;
	ProjectContact customer;
	ProjectContact contractor;
	ProjectContact architect;
	String projectManager;
	String completionDate;
		
	/**
	 * Project object constructor
	 * 
	 * @param projectNum - Unique identifying number for the project.
	 * @param projectName - Project Name.
	 * @param buildType - The type of building for the project (House, apartment, hut, etc.).
	 * @param ERFNum - The ERF number at the build site.
	 * @param address - The address for the build site.
	 * @param totalFee - The total fee for the project, in rands.
	 * @param totalPaid - The total amount paid by the customer, in rands.
	 * @param deadline - The project deadline.
	 * @param customer - The customer-type project contact object.
	 * @param contractor - The contractor-type project contact object.
	 * @param architect - The architect-type project contact object.
	 * @param projectManager - The project manager for project.
	 * @param completionDate - The date at which the project was completed.
	 */
	public Project(int projectNum, String projectName, String buildType, int ERFNum, String address, double totalFee, double totalPaid, Date deadline, ProjectContact customer, ProjectContact contractor, ProjectContact architect, String projectManager, String completionDate) {
		this.PROJECTNUM = projectNum;
		this.projectName = projectName;
		this.BUILDTYPE = buildType;
		this.ERFNUM = ERFNum;
		this.ADDRESS = address;
		this.TOTALFEE = totalFee;
		this.totalPaid = totalPaid;
		this.deadline = deadline;
		this.customer = customer;
		this.contractor = contractor;
		this.architect = architect;
		this.projectManager = projectManager;
		this.completionDate = completionDate;
	}
	
	/**
	 * Gets project number integer value.
	 * @return The project number integer value.
	 */
	public int getProjectNum() {
		return PROJECTNUM;
	}

	/**
	 * Gets the project building type string value.
	 * @return The project building type string value.
	 */
	public String getBuildType() {
		return BUILDTYPE;
	}

	/**
	 * Gets the project address string value.
	 * @return The project address string value.
	 */
	public String getAddress() {
		return ADDRESS;
	}

	/**
	 * Gets the ERF number integer value.
	 * @return The ERF number integer value.
	 */
	public int getERFNum() {
		return ERFNUM;
	}

	/**
	 * Gets the project total fees double value.
	 * @return The project total fees double value.
	 */
	public double getTotalFee() {
		return TOTALFEE;
	}
	
	/**
	 * Sets the double value for the total paid amount for the project.
	 * @param totalPaid The double value to be set as the project's total paid value.
	 */
	public void setTotalPaid(double totalPaid) {
		this.totalPaid = totalPaid;
	}
	
	/**
	 * Gets the project total paid double value.
	 * @return The project total paid double value.
	 */
	public double getTotalPaid() {
		return totalPaid;
	}
	
	/**
	 * Sets the Date value for the project deadline.
	 * @param deadline The date value to be set as the project's deadline value.
	 */
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	/**
	 * Gets the project deadline Date value.
	 * @return The project deadline Date value.
	 */
	public Date getDeadline() {
		return deadline;
	}

	/**
	 * Gets the customer project contact object.
	 * @return The customer project contact object.
	 */
	public ProjectContact getCustomer() {
		return customer;
	}

	/**
	 * Gets the contractor project contact object.
	 * @return The contractor project contact object.
	 */
	public ProjectContact getContractor() {
		return contractor;
	}
	
	/**
	 * Sets the contractor project contact object.
	 * @param contractor The contractor project contact object to be set as the project object.
	 */
	public void setContractor(ProjectContact contractor) {
		this.contractor = contractor;
	}

	/**
	 * Gets the architect project contact object.
	 * @return The architect project contact object.
	 */
	public ProjectContact getArchitect() {
		return architect;
	}
	
	/**
	 * Sets the project name string value.
	 * @param projectName The string value to be set as the project name.
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	/**
	 * Gets the project name string value.
	 * @return The project name string value.
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Gets the project manager string value.
	 * @return The string value to be set as the project manager.
	 */
	public String getProjectManager() {
		return projectManager;
	}
	
	/**
	 * Sets the project manager string value.
	 * @param projectManager The string value to be set as the project manager.
	 */
	public void setProjectManager(String projectManager) {
		this.projectManager = projectManager;
	}

	/**
	 * Sets the project completion date string value.
	 * @param completionDate The string value to be set as the completion date.
	 */
	public void setCompletionDate(String completionDate) {
		this.completionDate = completionDate;
	}
	
	/**
	 * Gets the completion date string value.
	 * @return The completion date string value.
	 */
	public String getCompletionDate() {
		return completionDate;
	}

	/**
	 * Overrides the toString method.
	 * <p>
	 * Builds the string using the project attributes,
	 * and formats it to a reader-friendly format.
	 */
	public String toString() {
		String output = "----  Project: " + projectName +"  ----";
		output += "\nProject Number:\t\t" + PROJECTNUM;
		output += "\nBuilding Type:\t\t" + BUILDTYPE;
		output += "\nERF Number:\t\t" + ERFNUM;
		output += "\nPhys. Address:\t\t" + ADDRESS;
		SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd");
		output += "\nDeadline:\t\t" + dateformatter.format(deadline);
		DecimalFormat decimalFormat = new DecimalFormat("#.00", DecimalFormatSymbols.getInstance(Locale.US));
		output += "\nTotal Fee:\t\tR " + decimalFormat.format(TOTALFEE);
		output += "\nTotal Paid:\t\tR " + decimalFormat.format(totalPaid) +"\n";
		output += customer + "\n";
		output += contractor + "\n";
		output += architect + "\n";
		// Add completion date to string if the project has been finalised
		if (completionDate == null){
			output += "\n";
		}
		else {
			output += "\nCompleted:\t\t" + completionDate;
		}
		return output; 
	}
}
