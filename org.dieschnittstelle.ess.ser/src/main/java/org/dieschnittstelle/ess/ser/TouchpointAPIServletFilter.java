package org.dieschnittstelle.ess.ser;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static org.dieschnittstelle.ess.utils.Utils.show;

/**
 * checks whether the gui servlet is accessed by a user agent that accepts html
 * @author kreutel
 *
 */
public class TouchpointAPIServletFilter implements Filter {

	protected static Logger logger = org.apache.logging.log4j.LogManager
			.getLogger(TouchpointAPIServletFilter.class);

	public TouchpointAPIServletFilter() {
		show("TouchpointGUIServletFilter: constructor invoked\n");
	}

	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		show("TouchpointGUIServletFilter: doFilter() invoked\n");
		
		// check whether we have a an accept-language header that will be set by the browser but not by the apache http client.
		// otherwise reject the request
		String userAgentHeader = ((HttpServletRequest) request)
				.getHeader("User-Agent");
		logger.info("got user-agent header: " + userAgentHeader);

		// blockieren, wenn User-Agent auf Browser hindeutet
		if (userAgentHeader != null && userAgentHeader.contains("Mozilla")) {
			((HttpServletResponse) response)
					.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
