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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unboundid.directory.sdk.http.types.HTTPServerContext;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ops.models.Status;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * An HTTP servlet that reports the availability status of server's store
 * adapters, LDAP load balancing algorithms, and HTTP servlets. A 200 OK is
 * returned if the server's services are available, and a 503 SERVICE
 * UNAVAILABLE is returned otherwise.
 *
 * @author Jacob Childress
 */
public class StatusServlet extends HttpServlet
{
  private static final long serialVersionUID = 4544150159114076878L;
  private static ObjectMapper objectMapper = new ObjectMapper();

  private final HTTPServerContext serverContext;
  private final LDAPInterface connection;
  private final String[] servletsToCheck;


  /**
   * Constructs a servlet instance.
   *
   * @param serverContext
   *          The server context.
   * @param connection
   *          An LDAP connection interface.
   * @param servletsToCheck
   *          The HTTP servlets that must be enabled for the server to be
   *          considered available.
   */
  public StatusServlet(HTTPServerContext serverContext,
                       LDAPInterface connection,
                       String... servletsToCheck)
  {
    this.serverContext = serverContext;
    this.connection = connection;
    this.servletsToCheck = servletsToCheck;
  }


  /** {@inheritDoc} */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException
  {
    serverContext.debugVerbose("START: GET request");
    try
    {
      serverContext.debugVerbose("Initializing StatusClient");
      StatusClient client =
              new StatusClient(connection, servletsToCheck);
      serverContext.debugVerbose("Retrieving status");
      Status status = client.getStatus();
      response.setContentType("application/json");
      if (status.isOK())
      {
        serverContext.debugInfo("Server status OK");
        response.setStatus(HttpServletResponse.SC_OK);
      }
      else
      {
        // TODO: Log details when status includes errors.
        // Note that the server will log ample detail itself.
        serverContext.debugWarning("Server status NOT OK");
        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
      }
      PrintWriter writer = response.getWriter();
      writer.write(objectMapper.writeValueAsString(status));
    }
    catch (Exception e)
    {
      serverContext.debugThrown(e);
      throw new ServletException(e);
    }
    serverContext.debugVerbose("END: GET request");
  }
}