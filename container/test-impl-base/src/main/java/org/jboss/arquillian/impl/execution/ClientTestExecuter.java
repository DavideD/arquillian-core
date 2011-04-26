/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.impl.execution;

import org.jboss.arquillian.api.RunAsClient;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.impl.execution.event.ExecutionEvent;
import org.jboss.arquillian.impl.execution.event.LocalExecutionEvent;
import org.jboss.arquillian.impl.execution.event.RemoteExecutionEvent;
import org.jboss.arquillian.spi.client.deployment.DeploymentDescription;
import org.jboss.arquillian.spi.event.suite.Test;

/**
 * TestExecuter for running on the client side. Can switch between Local and Remote test execution.
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ClientTestExecuter
{
   @Inject
   private Event<ExecutionEvent> executionEvent;
   
   @Inject
   private Instance<DeploymentDescription> deploymentDescription;

   public void execute(@Observes Test event) throws Exception
   {
      boolean runAsClient = true;
      
      DeploymentDescription deploymentDescription = this.deploymentDescription.get();
      if(deploymentDescription != null)
      {
         runAsClient =  deploymentDescription.testable() ? false:true;
         
         if(event.getTestMethod().isAnnotationPresent(RunAsClient.class))
         {
            runAsClient = true;
         }
         else if(event.getTestClass().isAnnotationPresent(RunAsClient.class))
         {
            runAsClient = true;
         }
      }
      if(runAsClient) 
      {
         executionEvent.fire(new LocalExecutionEvent(event.getTestMethodExecutor()));
      }
      else
      {
         executionEvent.fire(new RemoteExecutionEvent(event.getTestMethodExecutor()));
      }
   }
}