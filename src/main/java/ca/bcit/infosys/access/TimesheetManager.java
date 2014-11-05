package ca.bcit.infosys.access;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ca.bcit.infosys.timesheet.Timesheet;

/**
 * @author Huanan
 *
 */

public class TimesheetManager implements java.io.Serializable{
	
	
	/**
	 *  database simulator
	 * 
	 */
	static private List<Timesheet>  timesheetList =  new ArrayList<Timesheet>(); 
	
	
	
	/**
	 *  Constructor
	 */
	public TimesheetManager() {
	}

	/**
	 * @param Timesheet timesheet
	 */
	public void add(Timesheet timesheet){
		timesheetList.add(timesheet);
	}
	
	/**
	 * @param Timesheet timesheet
	 */
	public void update(Timesheet timesheet){
		int index = timesheetList.indexOf(timesheet);
		if(index != -1)
		timesheetList.set(index, timesheet);
		else timesheetList.add(timesheet);
	}

	
	/**
	 * @return List<Timesheet> timesheetList
	 */
	@SuppressWarnings("unchecked")
	public List<Timesheet> getTimesheets() {
		
		Collections.sort(timesheetList);

		return timesheetList;
	}

}