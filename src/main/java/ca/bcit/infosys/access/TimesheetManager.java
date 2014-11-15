package ca.bcit.infosys.access;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import ca.bcit.infosys.timesheet.Timesheet;
import ca.bcit.infosys.timesheet.TimesheetRow;

/**
 * @author Huanan
 *
 */

public class TimesheetManager implements java.io.Serializable {
	/** dataSource for connection pool on JBoss AS 7 or higher. */
	@Resource(mappedName = "java:jboss/datasources/TIMESHEET")
	private DataSource dataSource;

	private final int TIMESHEET_ID = 1;
	private final int EMP_ID = 2;
	private final int WEEKNO = 3;
	private final int PROJECT_ID = 4;
	private final int WP = 5;
	private final int TOTAL = 6;
	private final int SAT = 7;
	private final int SUN = 8;
	private final int MON = 9;
	private final int TUE = 10;
	private final int WED = 11;
	private final int THU = 12;
	private final int FRI = 13;
	private final int NOTES = 14;
	private final int ENDWEEK = 15;
	/**
	 * Constructor
	 */
	public TimesheetManager() {
	}

	/**
	 * @param Timesheet
	 *            timesheet
	 */
	public void add(Timesheet timesheet) {

		Connection connection = null;
		PreparedStatement stmt = null;

		for (TimesheetRow timesheetRow : timesheet.getDetails()) {

			try {
				try {
					connection = dataSource.getConnection();
					try {
						stmt = connection
								.prepareStatement("INSERT INTO Suppliers "
										+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

						stmt.setInt(EMP_ID, timesheet.getEmployee()
								.getEmpNumber());
						stmt.setInt(WEEKNO, timesheet.getWeekNumber());
						stmt.setInt(PROJECT_ID, timesheetRow.getProjectID());
						stmt.setString(WP, timesheetRow.getWorkPackage());
						stmt.setBigDecimal(TOTAL, timesheetRow.getSum());
						stmt.setBigDecimal(SAT,
								timesheetRow.getHoursForWeek()[0]);
						stmt.setBigDecimal(SUN,
								timesheetRow.getHoursForWeek()[1]);
						stmt.setBigDecimal(MON,
								timesheetRow.getHoursForWeek()[2]);
						stmt.setBigDecimal(TUE,
								timesheetRow.getHoursForWeek()[3]);
						stmt.setBigDecimal(WED,
								timesheetRow.getHoursForWeek()[4]);
						stmt.setBigDecimal(THU,
								timesheetRow.getHoursForWeek()[5]);
						stmt.setBigDecimal(FRI,
								timesheetRow.getHoursForWeek()[6]);
						stmt.setString(NOTES, timesheetRow.getNotes());
						stmt.setDate(ENDWEEK, new Date(timesheet.getEndWeek().getTime()));
						stmt.executeUpdate();
					} finally {
						if (stmt != null) {
							stmt.close();
						}
					}
				} finally {
					if (connection != null) {
						connection.close();
					}
				}
			} catch (SQLException ex) {
				System.out.println("Error in persist " + timesheet);
				ex.printStackTrace();
			}
		}
	}

	/**
	 * @param Timesheet
	 *            timesheet
	 */
	public void update(Timesheet timesheet) {
//		int index = timesheetList.indexOf(timesheet);
//		if (index != -1)
//			timesheetList.set(index, timesheet);
//		else
//			timesheetList.add(timesheet);
	}

	/**
	 * @return List<Timesheet> timesheetList
	 */
	@SuppressWarnings("unchecked")
	public List<Timesheet> getTimesheets(final int empId) {

		  ArrayList<Timesheet> timesheetList = new ArrayList<Timesheet>();
	        Connection connection = null;
	        Statement stmt = null;
	        try {
	            try {
	                connection = dataSource.getConnection();
	                try {
	                    stmt = connection.createStatement();
	                    ResultSet result = stmt.executeQuery(
	                            "SELECT * FROM TIMESHEETS WHERE EMP_ID ="+empId+" ORDER BY WEEKNO");
	               	                    
	                    while (result.next()) {
	                    	TimesheetRow tsw = new TimesheetRow();
	                    	tsw.setProjectID(result.getInt("PROJECT_ID"));
	                    	tsw.setWorkPackage(result.getString("WP"));
	                    	tsw.setHour(SAT, result.getBigDecimal(SAT));
	                    	tsw.setHour(SUN, result.getBigDecimal(SUN));
	                    	tsw.setHour(MON, result.getBigDecimal(MON));
	                    	tsw.setHour(TUE, result.getBigDecimal(TUE));
	                    	tsw.setHour(WED, result.getBigDecimal(WED));
	                    	tsw.setHour(THU, result.getBigDecimal(THU));
	                    	tsw.setHour(FRI, result.getBigDecimal(FRI));
	                    	tsw.setNotes(result.getString("NOTES"));
	                            
	                    }
	                } finally {
	                    if (stmt != null) {
	                        stmt.close();
	                    }
	                }
	            } finally {
	                if (connection != null) {
	                    connection.close();
	                }
	            }
	        } catch (SQLException ex) {
	            System.out.println("Error in getAll");
	            ex.printStackTrace();
	            return null;
	        }

		return timesheetList;
	}

}