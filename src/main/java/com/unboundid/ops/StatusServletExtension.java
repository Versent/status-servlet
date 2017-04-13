/*
 * Copyright 2016 UnboundID Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.unboundid.ops;

import com.unboundid.directory.sdk.http.api.HTTPServletExtension;
import com.unboundid.directory.sdk.http.config.HTTPServletExtensionConfig;
import com.unboundid.directory.sdk.http.types.HTTPServerContext;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.StringArgument;

import javax.servlet.http.HttpServlet;
import java.util.Collections;
import java.util.List;

/**
 * An {@link HTTPServletExtension} for the {@link StatusServlet}, which
 * reports a server's status, and can be used as a health check endpoint for
 * HTTP load balancers.
 *
 * @author Jacob Childress
 */
public class StatusServletExtension extends HTTPServletExtension
{
  private static final String ARG_PATH = "path";
  private static final String ARG_MONITORED_SERVLET = "monitored-servlet";

  private String path;


  /** {@inheritDoc} */
  @Override
  public String getExtensionName()
  {
    return "Status Servlet";
  }


  /** {@inheritDoc} */
  @Override
  public String[] getExtensionDescription()
  {
    return new String[] {
            "This extension provides a status servlet. It reports status for " +
                "store adapters, LDAP store adapters, and a configurable set of " +
                "HTTP servlets. The status servlet (default path '/status') can " +
                "be used as a health check target for HTTP load balancers. The " +
                "servlet will return a 200 OK if the server's services are " +
                "available, a 429 TOO MANY REQUESTS if the server is degraded, " +
                "and a 503 SERVICE UNAVAILABLE if they are not."
    };
  }


  /** {@inheritDoc} */
  @Override
  public void defineConfigArguments(final ArgumentParser parser)
          throws ArgumentException
  {
    parser.addArgument(new StringArgument(
            null, ARG_PATH, false, 1, "{path}",
            "The base path of this servlet. The default is '/status'.",
            "/status"));
    parser.addArgument(new StringArgument(
            null, ARG_MONITORED_SERVLET, false, 0, "{servletName}",
            "The name of a servlet that is expected to be enabled. " +
                    "By default, no servlets are monitored."));
  }


  /** {@inheritDoc} */
  @Override
  public HttpServlet createServlet(
          HTTPServerContext httpServerContext,
          HTTPServletExtensionConfig httpServletExtensionConfig,
          ArgumentParser argumentParser) throws LDAPException
  {
    StringArgument pathArgument =
            (StringArgument) argumentParser.getNamedArgument(ARG_PATH);
    path = pathArgument.getValue();
    StringArgument monitoredServlets =
            (StringArgument) argumentParser.getNamedArgument(ARG_MONITORED_SERVLET);
    return new StatusServlet(httpServerContext,
                             httpServerContext.getInternalRootConnection(),
                             monitoredServlets.getValues().toArray(
                                           new String[monitoredServlets.getValues().size()]));
  }


  /** {@inheritDoc} */
  @Override
  public List<String> getServletPaths()
  {
    return Collections.singletonList(path);
  }
}
