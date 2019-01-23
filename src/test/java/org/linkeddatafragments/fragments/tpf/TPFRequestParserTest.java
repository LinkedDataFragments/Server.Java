package org.linkeddatafragments.fragments.tpf;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.linkeddatafragments.config.ConfigReader;
import org.linkeddatafragments.fragments.tpf.TPFRequestParser.Worker;

public class TPFRequestParserTest {

	@Test
	public void shouldReturnCorrectDatasetUrl() {
		final TPFRequestParser parser = new TPFRequestParser(null);
		final ServletContext servletContext = mock(ServletContext.class);
		when(servletContext.getContextPath()).thenReturn("/foo");
		final HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getServletContext()).thenReturn(servletContext);
		when(request.getServerPort()).thenReturn(123);
		when(request.getScheme()).thenReturn("https");
		when(request.getServerName()).thenReturn("foo.bla");
		when(request.getServletPath()).thenReturn("");
		when(request.getPathInfo()).thenReturn("/someDataset");
		when(request.getRequestURI()).thenReturn("/foo" + "" + "/someDataset");
		final ConfigReader config = mock(ConfigReader.class);
		when(config.getBaseURL()).thenReturn(null);
		final Worker worker = parser.getWorker(request, config);
		final String datasetUrl = worker.getDatasetURL();
		Assert.assertEquals("wrong datasetUrl returned",
				"https://foo.bla:123/foo/someDataset", datasetUrl);
	}

}
