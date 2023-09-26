package org.ihtsdo.otf.transformationandtemplate.rest;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class RootController {

	@RequestMapping(path = "/", method = RequestMethod.GET)
	@Operation(description = "root", hidden = true)
	public void getRoot(HttpServletResponse response) throws IOException {
		response.sendRedirect("swagger-ui.html");
	}

}
