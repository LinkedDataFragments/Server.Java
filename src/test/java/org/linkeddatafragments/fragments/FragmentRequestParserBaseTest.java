package org.linkeddatafragments.fragments;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.linkeddatafragments.config.ConfigReader;

public class FragmentRequestParserBaseTest {

	@Test
	public void shouldIncludeContextPathInBaseUrl() throws IOException {
		final ServletContext servletContext = mock(ServletContext.class);
		when(servletContext.getContextPath()).thenReturn("/foo");
		
		final HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getServletContext()).thenReturn(servletContext);
		when(request.getServerPort()).thenReturn(123);
		when(request.getScheme()).thenReturn("https");
		when(request.getServerName()).thenReturn("foo.bla");

		final ConfigReader configReader = mock(ConfigReader.class);
		when(configReader.getBaseURL()).thenReturn(null);

		final String baseUrl = FragmentRequestParserBase.extractBaseURL(
				request, configReader);
		Assert.assertEquals("Wrong baseUrl extracted",
				"https://foo.bla:123/foo", baseUrl);

	}
}
