package ca.bcit.infosys.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import ca.bcit.infosys.access.EmployeeManager;
import ca.bcit.infosys.employee.Credentials;
import ca.bcit.infosys.employee.Employee;

/**
 * Resources to display categories and choose one.
 * 
 * @author blink
 * @version 1
 *
 */
@Named("empControl")
@SessionScoped
public class EmployeeController implements Serializable {
	@Inject
	private EmployeeManager empManager;
	/**
	 * List that hold the emplyees
	 */
	private List<Employee> empList;
	@Inject
	private Employee employee;
	/**
	 * Hold the new employee information
	 */
	private Employee newEmp;
	/**
	 * Hold the information of the employee which will be used
	 * to change the password
	 */
	private Employee editEmp;
	/**
	 * Hold the new password
	 */
	private String newPassword = "";
	/**
	 * Map that will hold the credential combos.
	 */
	private Map<String, String> credenCombo;
	@Inject private Credentials credential;
	@Inject private Employee currentEmployee;
	
	public Employee getEditEmp() {
		return editEmp;
	}

	public void setEditEmp(Employee editEmp) {
		this.editEmp = editEmp;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public Map<String, String> getCredenCombo() {
		return credenCombo;
	}

	public void setCredenCombo(Map<String, String> credenCombo) {
		this.credenCombo = credenCombo;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	/**
	 * Verify the user name which should match the password.
	 * If matched, then check the authority of the user.
	 * 0 means superuser, while other number is normal user.
	 * @return
	 */
	public String login() {
		boolean result = empManager.verifyUser(credential);		
		if (result) {
			currentEmployee = empManager.getEmployee(credential.getUserName());
			int type =currentEmployee.getType();
			credential = new Credentials();
			if (type == 0){
				getEmployees();
				getCredentials();
				return "superShowUser";
			}
			else
				return "user";
		} else
			return "failure";
	}

	public String logout() {
		empManager.logout(currentEmployee);
		return "login";
	}
	/**
	 * Assign the empList with the employee list in the empManager.
	 */
	public String getEmployees() {		
		empList = empManager.getEmployees();
		return "superShowUser";
	}
	
	/**
	 * Assign the credenCombo whti the credential combos Map
	 * in the empManager.
	 */
	public void getCredentials() {
		credenCombo = empManager.getLoginCombos();		
	}

	/**
	 * Delete the employee
	 * @param emp
	 */
	public void deleteEmp(Employee emp) {
		if (emp != null)
			empManager.deleteEmpoyee(emp);
		getEmployees();
	}

	/**
	 * Create a new employee and add it to the list.
	 * @return
	 */
	public String newEmployee() {
		if (employee != null)
			newEmp = new Employee(employee.getName(),employee.getEmpNumber(),employee.getUserName(), 1);
		//System.out.println(newEmp.getName());
		empManager.addEmployee(newEmp);
		empList.add(newEmp);
		//setEmpList(empManager.getEmployees());
		getEmployees();	
		
		return "superShowUser";
	}

	public EmployeeManager getEmpManager() {
		return empManager;
	}

	public void setEmpManager(EmployeeManager empManager) {
		this.empManager = empManager;
	}

	public List<Employee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<Employee> empList) {
		this.empList = empList;
	}

	public Credentials getCredential() {
		return credential;
	}

	public void setCredential(Credentials credential) {
		this.credential = credential;
	}
	
	/**
	 * Return back to the superShowUser page.
	 * @return
	 */
	public String update() {
        for (Employee e : empList) {
            if (e.isEditable() == true) {
            	empManager.merge(e);
                e.setEditable(false);
            }
        }
		
		return "superShowUser";
	}

	/**
	 * Get the currend employee who signed in.
	 * @return
	 */
	public Employee getCurrentEmployee() {
		currentEmployee = empManager.getCurrentEmployee();
		return currentEmployee;
	}

	public void setCurrentEmployee(Employee currentEmployee) {
		this.currentEmployee = currentEmployee;
	}
	
	public String add(){
		employee = new Employee();
		return "newEmp";
	}
	
	/**
	 * Use editEmp to hold the emplyee information which will
	 * be used to change password.
	 * @param e
	 * @return
	 */
	public String displayEditEmp (Employee e) {
		editEmp = e;
		newPassword = "";
		return "changePassword";		
	}
	
	/**
	 * Change the password based on the user name of the employee.
	 * The user name is also in the login combos.
	 * @param newPassword
	 * @return
	 */
	public String changePassword(String newPassword) {
		empManager.changePass(editEmp, newPassword);
		return "superShowUser";
	}
		
	public String goBack() {
		return "login";
	}
	
	public String cancel() {
		return "superShowUser";
	}
}
