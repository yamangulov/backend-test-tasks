package com.backend.tasks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * AbstractEndpointTest with common test methods.
 */
public abstract class BaseControllerTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
    protected WebApplicationContext webApplicationContext;

	@Autowired
    protected ObjectMapper objectMapper;

	protected MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {
		setup();
	}

	protected void setup() throws Exception {

    	this.mockMvc = webAppContextSetup(webApplicationContext).build();

    }
    
	/**
	 * Returns json representation of the object.
	 * @param o instance
	 * @return json
	 * @throws IOException
	 */
	protected String json(Object o) throws IOException {
		return new Gson().toJson(o);
	}
}
