
/**
 * Class to create a Project Contact object.
 * <p>
 * Project Contact objects are attributes for Project objects.
 * Project Contact objects and Project objects are managed by the Poise class.
 * 
 * @author Lindsey
 * @see Project
 * @see Poise
 */
public class ProjectContact {
	
	String contact, contactName, phone, email, contactAddress;
	
	/**
	 * Project Contact constructor.
	 * @param contact The contact type - "Customer", "Contractor" or Architect.
	 * @param contactName - Contact's name.
	 * @param phone - Contact's telephone number.
	 * @param email - Contact's e-mail address.
	 * @param contactAddress - Contact's physical address.
	 */
	public ProjectContact(String contact, String contactName, String phone, String email, String contactAddress ) {
		this.contact = contact;
		this.contactName = contactName;
		this.phone = phone;
		this.email = email;
		this.contactAddress = contactAddress;
	}

	/**
	 * Gets project contact type string value.
	 * @return Project Contact object string value.
	 */
	public String getContact() {
		return contact;
	}

	/**
	 * Gets project contact name string value.
	 * @return contact name string value.
	 */
	public String getContactName() {
		return contactName;
	}

	/**
	 * Gets project contact telephone number string value.
	 * @return contact telephone number string value.
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Gets project contact e-mail string value.
	 * @return contact e-mail string value.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Gets project contact address string value.
	 * @return contact address string value.
	 */
	public String getContactAddress() {
		return contactAddress;
	}

	/**
	 * Overrides toString method.
	 * <p>
	 * Builds the string using the project contact attributes,
	 * and formats it to a reader-friendly format.
	 */
	public String toString() {
		String output = "\n> " + contact +":";
		output += "\n Name: \t\t" + contactName;
		output += "\n Phone: \t" + phone;
		output += "\n E-mail: \t" + email;
		output += "\n Address: \t" + contactAddress;
		return output;
	}
}
