package com.atomikos.osgi.sample.web.internal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.atomikos.osgi.sample.AccountManager;

@SuppressWarnings("serial")
public class Controller extends HttpServlet {
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

	private ServletContext sc;
	private BundleContext bundleContext;

	private AccountManager accountManager;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		sc = config.getServletContext();
		bundleContext = (BundleContext) sc.getAttribute("osgi-bundlecontext");

		ServiceReference sr = bundleContext.getServiceReference(AccountManager.class.getName());
		LOGGER.info("ServiceReference " + sr);
		if (sr != null) {
			accountManager = (AccountManager) bundleContext.getService(sr);
			LOGGER.info("Found service account Manager " + accountManager);
		}
	}


	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");

		final PrintWriter writer = response.getWriter();
		writer.println("<html><body align='center'>");
		writer.println("<h1>Atomikos</h1>");

		writer.println("<p>");
		writer.println("<form METHOD=GET>");
		String operation = request.getParameter("operation");
		if (operation == null) {
			// the default...
			operation = "balance";
		}
		writer.println("<input type='radio' name='operation' value='balance' " + isChecked(operation, "balance")
				+ "> balance");
		writer.println("<input type='radio' name='operation' value='withdraw' " + isChecked(operation, "withdraw")
				+ "> withdraw");
		writer.println("<input type='radio' name='operation' value='owner' " + isChecked(operation, "owner")
				+ "> owner");
		writer.println("<br>");
		writer.println("accno (0 to 100): <INPUT type=text name='accno' value='50'> <br>");
		writer.println("amount: <INPUT type=text name='amount' value='10'> <br>");

		writer.println("<INPUT type='submit' value='Envoyer'>");

		writer.println("</form>");

		int accno = getAccountNumber(request);

		writer.println("last operation " + operation + " <br>");
		if ("balance".equals(operation)) {
			balance(writer, accno);
		} else if ("withdraw".equals(operation)) {
			withdraw(request, writer, accno);
		} else if ("owner".equals(operation)) {
			owner(writer, accno);
		}

		writer.println("</p>");
		writer.println("</body></html>");
	}


	private int getAccountNumber(final HttpServletRequest request) throws ServletException {
		int accno = 50;
		String accnoStr = request.getParameter("accno");
		if (accnoStr != null) {

			try {
				accno = Integer.parseInt(accnoStr);
			} catch (NumberFormatException e1) {
				LOGGER.warning(e1.getMessage());
				throw new ServletException("accno must be an integer...");
			}
		}
		return accno;
	}


	private void owner(final PrintWriter writer, int accno) throws ServletException {
		try {
			writer.println("owner on  " + accno + " is :" + accountManager.getOwner(accno));
		} catch (Exception e) {
			LOGGER.warning(e.getMessage());
			throw new ServletException(e);
		}
	}


	private void balance(final PrintWriter writer, int accno) throws ServletException {
		try {
			writer.println("balance on  " + accno + " is :" + accountManager.getBalance(accno));
		} catch (Exception e) {
			LOGGER.warning(e.getMessage());
			throw new ServletException(e);
		}
	}


	private void withdraw(final HttpServletRequest request, final PrintWriter writer, int accno)
			throws ServletException {
		int amount = 0;
		String amountStr = request.getParameter("amount");
		if (amountStr != null) {

			try {
				amount = Integer.parseInt(amountStr);
			} catch (NumberFormatException e1) {
				throw new ServletException("amount must be an integer...");
			}
		}
		try {
			accountManager.withdraw(accno, amount);
			writer.println("balance on  " + accno + " is :" + accountManager.getBalance(accno));
		} catch (Exception e) {
			LOGGER.warning(e.getMessage());
			throw new ServletException(e);
		}
	}

	private String isChecked(String operation, String option) {
		// The default...
		return operation.equals(option) ? "checked" : "";
	}
}
