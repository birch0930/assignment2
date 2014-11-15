package ca.bcit.infosys.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import ca.bcit.infosys.access.EmployeeManager;
import ca.bcit.infosys.access.TimesheetManager;
import ca.bcit.infosys.employee.Employee;
import ca.bcit.infosys.timesheet.Timesheet;
import ca.bcit.infosys.timesheet.TimesheetCollection;
import ca.bcit.infosys.timesheet.TimesheetRow;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

/**
 * 
 * @author Huanan
 *
 */

@Named("sheetControl")
@SessionScoped
public class TimeSheetController implements Serializable, TimesheetCollection {

	@Inject
	private EmployeeManager employeeManager;
	@Inject
	private Timesheet currentTimesheet;
	@Inject
	private TimesheetManager timesheetManager;
	private List<Timesheet> timesheetList;
	private Employee employee;
	static private String userName;

	/**
	 * Default Constructor
	 */
	public TimeSheetController() {

	}

	public void handleEvent(ValueChangeEvent   event) {
		Integer dueDate= (Integer) event.getNewValue();
	//	Integer dueDate = (Integer)((UIInput)event.getSource()).getValue(); both works
		currentTimesheet.setWeekNumber(dueDate, Calendar.getInstance().get(Calendar.YEAR));
	}


	
	@Override
	public List<Timesheet> getTimesheets() {
		return timesheetList;
	}

	@Override
	public List<Timesheet> getTimesheets(Employee e) {
		timesheetList = timesheetManager.getTimesheets(e.getEmpNumber());
		return timesheetList;
	}

	/**
	 * @return display current timesheet
	 */
	public String displayCurrentTimesheet() {
		employee = employeeManager.getEmployee(userName);
		currentTimesheet = getCurrentTimesheet(employee);
		System.out.println(employee.getEmpNumber());
		if (currentTimesheet == null) {
			return this.addTimesheet();
		}
		return "displayTimesheet";
	}

	@Override
	public Timesheet getCurrentTimesheet(Employee e) {


		timesheetList = getTimesheets(e);
		int currentWeekNum = new Timesheet().getWeekNumber();
		if (timesheetList == null || timesheetList.size() == 0)
			return null;
		
		for (Timesheet timesheet : timesheetList) {
			System.out.println(currentWeekNum);
			if(timesheet.getWeekNumber()==currentWeekNum)
				return timesheet;
		}
	
		return null;
	}

	@Override
	public String addTimesheet() {

		employee = employeeManager.getEmployee(userName);
		currentTimesheet = new Timesheet();
		currentTimesheet.setEmployee(employee);

		return "newTimesheet.xhtml";
	}

	/**
	 * add a row
	 */
	public void addRow() {
		//System.out.println(userName);
		currentTimesheet.addRow();
		timesheetManager.update(currentTimesheet);
		timesheetList = getTimesheets(currentTimesheet.getEmployee());
	}

	/**
	 * show previous timesheet
	 * 
	 * @return current page
	 */
	public String previous() {
		int index =currentTimesheet.getWeekNumber();
		index--;
		for (Timesheet timesheet : timesheetList) {
			System.out.println(index);
			if(timesheet.getWeekNumber()==index)
				currentTimesheet = timesheet;
		}
		return "";
	}

	/**
	 * show later timesheet
	 * 
	 * @return current page
	 */
	public String nextTimesheet() {
		int index =currentTimesheet.getWeekNumber();
		index++;
		for (Timesheet timesheet : timesheetList) {
			System.out.println(index);
			if(timesheet.getWeekNumber()==index)
				currentTimesheet = timesheet;
		}

		return "";
	}

	/**
	 * @param row
	 * @return current page
	 */
	public String editTimesheet(TimesheetRow row) {
		row.setEditable(true);
		//timesheetManager.update(currentTimesheet);
		//timesheetList = getTimesheets(currentTimesheet.getEmployee());
		return "";
	}

	/**
	 * @return display page
	 */
	public String updateTimesheet() {
		checkEmptyRow();
		for (TimesheetRow timesheetRow : currentTimesheet.getDetails()) {
			if (timesheetRow.isEditable())
				timesheetRow.setEditable(false);
		}
		timesheetManager.update(currentTimesheet);
		timesheetList = getTimesheets(currentTimesheet.getEmployee());
		return "displayTimesheet";
	}

	/**
	 * @param Row
	 * @return display page
	 */
	public String delRow(TimesheetRow Row) {

		currentTimesheet.deleteRow(Row);
		timesheetManager.update(currentTimesheet);
		timesheetList = getTimesheets(currentTimesheet.getEmployee());
		return "displayTimesheet";
	}

	/**
	 * @return display page
	 */
	public String saveTimesheet() {
		checkEmptyRow();
		timesheetManager.add(currentTimesheet);
		timesheetList = getTimesheets(currentTimesheet.getEmployee());

		return "displayTimesheet";
	}

	/**
	 * when saving or updating new timesheet, check if projectId or work package
	 * is empty, then ignore it
	 */
	private void checkEmptyRow() {
		List<TimesheetRow> list = new ArrayList<TimesheetRow>();
		for (TimesheetRow timesheetRow : currentTimesheet.getDetails()) {
			if (timesheetRow.getProjectID() == null
					|| timesheetRow.getProjectID().equals("")
					|| timesheetRow.getWorkPackage() == null
					|| timesheetRow.getWorkPackage().equals(""))
				list.add(timesheetRow);
		}

		currentTimesheet.getDetails().removeAll(list);
	}

	/**
	 * @return currentTimesheet
	 */
	public Timesheet getCurrentTimesheet() {
		return currentTimesheet;
	}

	/**
	 * @param currentTimesheet
	 */
	public void setCurrentTimesheet(Timesheet currentTimesheet) {
		this.currentTimesheet = currentTimesheet;
	}

	/**
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return
	 */
	public List<Timesheet> getTimesheetList() {
		return timesheetList;
	}

	/**
	 * @param timesheetList
	 */
	public void setTimesheetList(List<Timesheet> timesheetList) {
		this.timesheetList = timesheetList;
	}

	/**
	 * @return
	 */
	public Employee getEmployee() {
		return employee;
	}

	/**
	 * @param employee
	 */
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

}
