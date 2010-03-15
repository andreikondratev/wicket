/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.protocol.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.protocol.http.request.WebExternalResourceRequestTarget;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.resource.WebExternalResourceStream;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Test WebExternalResourceRequestTarget and WebExternalResourceStream
 * 
 * @author <a href="mailto:jbq@apache.org">Jean-Baptiste Quenot</a>
 */
public class WebExternalResourceTest extends TestCase
{
	private WicketTester tester;

	@Override
	protected void setUp() throws Exception
	{
		File tempDir = new File("target/webapp");
		tempDir.mkdir();
		File tempFile = new File(tempDir, "index.html");
		FileOutputStream out = new FileOutputStream(tempFile);
		InputStream in = WebExternalResourceTest.class.getResourceAsStream("index.html");
		Streams.copy(in, out);
		in.close();
		out.close();
		tester = new WicketTester(new MockApplication(), tempDir.getPath());
		// We fake the browser URL, otherwise Wicket doesn't know the requested URL and cannot guess
		// the Content-Type
		tester.getRequest().setURL("index.html");


	}

	@Override
	protected void tearDown() throws Exception
	{
		tester.destroy();
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testWebExternalResourceRequestTarget() throws Exception
	{
		WebExternalResourceRequestTarget rt = new WebExternalResourceRequestTarget("/index.html");
		tester.processRequest(rt);
		assertTrue(tester.getContentTypeFromResponseHeader().startsWith("text/html"));
		tester.assertContains("<h1>Hello, World!</h1>");
	}

	/**
	 * 
	 * @throws Exception
	 */
	// FIXME WebExternalResourceStream does not implement length()
	public void testWebExternalResource() throws Exception
	{
		WebExternalResourceStream resource = new WebExternalResourceStream("/index.html");
		ResourceStreamRequestHandler rt = new ResourceStreamRequestHandler(resource);
		tester.processRequest(rt);
		assertTrue(tester.getContentTypeFromResponseHeader().startsWith("text/html"));
		tester.assertContains("<h1>Hello, World!</h1>");
	}
}
