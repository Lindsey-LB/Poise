# Poise

## Purpose
This program manages the project data of a structural engineering company's projects, which are contain in a database. The program is able to add new projects, edit existing projects, view projects and finalise projects.
 
## Contents:
 * Project Class - used to create project object.
 * ProjectContact Class - used to create a project contact object.
 * Poise - main program.
 
## Main Program
*The main program - Poise - imports projects from a database and creates a list of project objects when the program is run. Any changes to the database are updated to the list of project objects once te database is successfully updated.*

*Allows for the user to display, edit or finalise projects:*
 * Captures details for a new project. The new project is added to the database.
 * Updates the due date of an existing project in the database. 
 * Updates the total amount paid by the customer for an existing project.
 * Updates the Contractor's contact details for an existing object.
 * Finalises a project - marks the project as "complete", adds the completion date, and produces an invoice if there is money owed by the client at the point of finalisation.
 * Displays all existing projects.
 * Displays all incomplete projects.
 * Displays all overdue projects.

*Javadocs have been included for further detail.*
