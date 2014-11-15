/**
 * 
 */
package ca.bcit.infosys.timesheet;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

/**
 * @author Huanan
 *
 */
@FacesValidator("TimesheetRowValidator")
public class TimesheetRowValidator implements Validator {

	@Inject Timesheet t;
	/**
	 * 
	 */
	public TimesheetRowValidator() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see javax.faces.validator.Validator#validate(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 */
	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		
		System.out.println(1123);
	}

	public Timesheet getT() {
		return t;
	}

	public void setT(Timesheet t) {
		this.t = t;
	}
	
}
