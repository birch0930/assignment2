package ca.bcit.infosys.timesheet;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.List;

import ca.bcit.infosys.employee.Employee;

/**
 * A class representing a single Timesheet.
 *
 * @author Bruce Link
 */
public class Timesheet implements java.io.Serializable,Comparable {
    /** Serial version number. */
    private static final long serialVersionUID = 2L;
    /** The user associated with this timesheet. */
    private Employee employee;
    /** The date of Friday for the week of the timesheet. */
    private Date endWeek;
    /** The ArrayList of all details (i.e. rows) that the form contains. */
    private List<TimesheetRow> details;
    /** The total number of overtime hours on the timesheet. */
    private BigDecimal overtime;
    /** The total number of flextime hours on the timesheet. */
    private BigDecimal flextime;
    
    private int weekNumber;
    /** Number of days in a week. */
    public static final int DAYS_IN_WEEK = 7;

    /** Number of hours in a day. */
    public static final BigDecimal HOURS_IN_DAY =
            new BigDecimal(24.0).setScale(1, BigDecimal.ROUND_HALF_UP);

    /** Full work week */
    public static final BigDecimal FULL_WORK_WEEK =
            new BigDecimal(40.0).setScale(1, BigDecimal.ROUND_HALF_UP);

    /**
     * Constructor for Timesheet.
     * Initialize a Timesheet with 5 empty rows and
     * to the current date.
     */
    public Timesheet() {
        details = new ArrayList<TimesheetRow>() {
            private static final long serialVersionUID = 1L;
            {
                add(new TimesheetRow());
                add(new TimesheetRow());
                add(new TimesheetRow());
                add(new TimesheetRow());
                add(new TimesheetRow());
            }
        };
        Calendar c = new GregorianCalendar();
        int currentDay = c.get(Calendar.DAY_OF_WEEK);
        int leftDays = Calendar.FRIDAY - currentDay;
        c.add(Calendar.DATE, leftDays);
        endWeek = c.getTime();
        checkFriday(endWeek);
    }
    
  

    /**
     * Creates a Timesheet object with all fields set. Used to create sample
     * data.
     * @param user The owner of the timesheet
     * @param end The date of the end of the week for the timesheet (Friday)
     * @param charges The detailed hours charged for the week for this timesheet
     */
    public Timesheet(final Employee user, final Date end,
            final List<TimesheetRow> charges) {
        employee = user;
        checkFriday(end);
        endWeek = end;
        details = charges;
        getWeekNumber();
    }

    /**
     * @return the employee.
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * @return the endWeek
     */
    public Date getEndWeek() {
        return endWeek;
    }

    private void checkFriday(final Date end) {
        Calendar c = new GregorianCalendar();
        c.setTime(end);
        int currentDay = c.get(Calendar.DAY_OF_WEEK);
        if (currentDay != Calendar.FRIDAY) {
            throw new IllegalArgumentException("EndWeek must be a Friday");
        }

    }
    /**
     * @param end the endWeek to set. Must be a Friday
     */
    public void setEndWeek(final Date end) {
    	
        checkFriday(end);
        endWeek = end;
    }

    /**
     * @return the weeknumber of the timesheet
     */
    public int getWeekNumber() {
        Calendar c = new GregorianCalendar();
        c.setTime(endWeek);
        c.setFirstDayOfWeek(Calendar.SATURDAY);
        weekNumber =c.get(Calendar.WEEK_OF_YEAR);
        return weekNumber;
    }

    /**
     * Sets the end of week based on the week number.
     *
     * @param weekNo the week number of the timesheet week
     * @param weekYear the year of the timesheet
     */
    public void setWeekNumber(final int weekNo, final int weekYear) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.SATURDAY);
        c.setTime(endWeek);
        c.setWeekDate(weekYear, weekNo, Calendar.FRIDAY);
        endWeek = c.getTime();
    }

    /**
     * @return the endWeek as string
     */
    public String getWeekEnding() {
        Calendar c = new GregorianCalendar();
        c.setTime(endWeek);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        month += 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        return month + "/" + day + "/" + year;
    }

    /**
     * @return the details
     */
    public List<TimesheetRow> getDetails() {
        return details;
    }

    /**
     * Sets the details of the timesheet.
     *
     * @param newDetails new weekly charges to set
     */
    public void setDetails(final ArrayList<TimesheetRow> newDetails) {
        details = newDetails;
    }

    /**
     * @return the overtime
     */
    public BigDecimal getOvertime() {
        return overtime;
    }

    /**
     * @param ot the overtime to set
     */
    public void setOvertime(final BigDecimal ot) {
        overtime = ot.setScale(1, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * @return the flextime
     */
    public BigDecimal getFlextime() {
        return flextime;
    }

    /**
     * @param flex the flextime to set
     */
    public void setFlextime(final BigDecimal flex) {
        flextime = flex.setScale(1, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calculates the total hours.
     *
     * @return total hours for timesheet.
     */
    public BigDecimal getTotalHours() {
        BigDecimal sum = BigDecimal.ZERO;
        for (TimesheetRow row : details) {
            sum = sum.add(row.getSum());
        }
        return sum;
    }

    /**
     * Calculates the daily hours.
     *
     * @return array of total hours for each day of week for timesheet.
     */
    public BigDecimal[] getDailyHours() {
        BigDecimal[] sums = new BigDecimal[DAYS_IN_WEEK];
        for (TimesheetRow day : details) {
            BigDecimal[] hours = day.getHoursForWeek();
            for (int i = 0; i < DAYS_IN_WEEK; i++) {
                if (hours[i] != null) {
                    if (sums[i] == null) {
                        sums[i] = hours[i];
                    } else {
                        sums[i] = sums[i].add(hours[i]);
                    }
                }
            }
        }
        return sums;
    }

    /**
     * Checks to see if timesheet total nets 40 hours.
     * @return true if FULL_WORK_WEEK == hours -flextime - overtime
     */
    public boolean isValid() {
        BigDecimal net = getTotalHours();
        if (overtime != null) {
            net = net.subtract(overtime);
        }
        if (flextime != null) {
            net = net.subtract(flextime);
        }
        return net.equals(FULL_WORK_WEEK);
    }

    /**
     * Deletes the specified row from the timesheet.
     *
     * @param rowToRemove
     *            the row to remove from the timesheet.
     */
    public void deleteRow(final TimesheetRow rowToRemove) {
        details.remove(rowToRemove);
    }

    /**
     * Add an empty to to the timesheet.
     */
    public void addRow() {
        details.add(new TimesheetRow());
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endWeek == null) ? 0 : endWeek.hashCode());
		result = prime * result
				+ ((flextime == null) ? 0 : flextime.hashCode());
		result = prime * result
				+ ((overtime == null) ? 0 : overtime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Timesheet other = (Timesheet) obj;
		if (endWeek == null) {
			if (other.endWeek != null)
				return false;
		} else if (!endWeek.equals(other.endWeek))
			return false;
		if (flextime == null) {
			if (other.flextime != null)
				return false;
		} else if (!flextime.equals(other.flextime))
			return false;
		if (overtime == null) {
			if (other.overtime != null)
				return false;
		} else if (!overtime.equals(other.overtime))
			return false;
		return true;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void setWeekNumber(int weekNumber) {
		this.weekNumber = weekNumber;
	}



	@Override
	public String toString() {
		return "Timesheet [employee=" + employee + ", endWeek=" + endWeek
				+ ", details=" + details + ", overtime=" + overtime
				+ ", flextime=" + flextime + ", weekNumber=" + weekNumber + "]";
	}



	@Override
	public int compareTo(Object o) {
		if(o instanceof Timesheet){
			Timesheet t = (Timesheet)o;
			if(this.getWeekNumber()<t.getWeekNumber()) return -1;
			if(this.getWeekNumber()>t.getWeekNumber()) return 1;
		}
		return 0;
	}
	
	

}
