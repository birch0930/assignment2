package ca.bcit.infosys.access;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
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
	@Inject
	EmployeeManager em;
	// private final int TIMESHEET_ID = 1;
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
								.prepareStatement("INSERT INTO TIMESHEETS "
										+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
						stmt.setString(1, null);
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
						// stmt.setDate(ENDWEEK, new
						// Date(timesheet.getEndWeek().getTime()));
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

		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			try {
				connection = dataSource.getConnection();
				try {
					stmt = connection
							.prepareStatement(" DELETE FROM TIMESHEETS WHERE EMPLOYEE_ID = ? AND  WEEKNO = ?");
					stmt.setInt(1, timesheet.getEmployee().getEmpNumber());
					stmt.setInt(2, timesheet.getWeekNumber());
					stmt.executeUpdate();
					add(timesheet);
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

	/**
	 * @return List<Timesheet> timesheetList
	 */
	@SuppressWarnings("unchecked")
	public List<Timesheet> getTimesheets(final int empId) {
		ArrayList<Timesheet> timesheetList = new ArrayList<Timesheet>();
		int i = 0;
		Connection connection = null;
		Statement stmt = null;
		Calendar c = new GregorianCalendar();
		int currentYear = c.get(Calendar.YEAR);
		try {
			try {
				connection = dataSource.getConnection();
				try {
					stmt = connection.createStatement();
					ResultSet result = stmt
							.executeQuery("SELECT * FROM TIMESHEETS WHERE EMPLOYEE_ID ="
									+ empId + " ORDER BY WEEKNO ASC");
					Timesheet timesheet = null;
					int index = 0;
					ArrayList<TimesheetRow> tempRows = new ArrayList<TimesheetRow>();
					while (result.next()) {
						// System.out.println(result.getInt("WEEKNO"));

						if (i == result.getInt("WEEKNO")) {
							TimesheetRow tsw = new TimesheetRow();
							tsw.setProjectID(result.getInt("PROJECT_ID"));
							tsw.setWorkPackage(result.getString("WP"));
							tsw.setHour(0, result.getBigDecimal("SAT"));
							tsw.setHour(1, result.getBigDecimal("SUN"));
							tsw.setHour(2, result.getBigDecimal("MON"));
							tsw.setHour(3, result.getBigDecimal("TUE"));
							tsw.setHour(4, result.getBigDecimal("WED"));
							tsw.setHour(5, result.getBigDecimal("THU"));
							tsw.setHour(6, result.getBigDecimal("FRI"));
							tsw.setNotes(result.getString("NOTES"));
							tempRows.add(tsw);

						} else {
							timesheet = new Timesheet();
							if (tempRows.size() > 0) {
								timesheetList.get(index - 1).setDetails(
										tempRows);
								tempRows = new ArrayList<TimesheetRow>();
							}
							TimesheetRow tsw = new TimesheetRow();
							timesheet.setEmployee(em.getEmployeeById(result
									.getInt("EMPLOYEE_ID")));
							timesheet.setWeekNumber(result.getInt("WEEKNO"),
									currentYear);
							tsw.setProjectID(result.getInt("PROJECT_ID"));
							tsw.setWorkPackage(result.getString("WP"));
							tsw.setHour(0, result.getBigDecimal("SAT"));
							tsw.setHour(1, result.getBigDecimal("SUN"));
							tsw.setHour(2, result.getBigDecimal("MON"));
							tsw.setHour(3, result.getBigDecimal("TUE"));
							tsw.setHour(4, result.getBigDecimal("WED"));
							tsw.setHour(5, result.getBigDecimal("THU"));
							tsw.setHour(6, result.getBigDecimal("FRI"));
							tsw.setNotes(result.getString("NOTES"));
							tempRows.add(tsw);
							timesheet.setDetails(tempRows);
							timesheetList.add(index, timesheet);
							index++;
						}
						i = result.getInt("WEEKNO");

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