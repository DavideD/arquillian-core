/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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

import java.lang.reflect.Method;
import java.util.List;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.test.AbstractContainerTestTestBase;
import org.jboss.arquillian.impl.execution.event.RemoteExecutionEvent;
import org.jboss.arquillian.spi.ContainerMethodExecutor;
import org.jboss.arquillian.spi.TestMethodExecutor;
import org.jboss.arquillian.spi.TestResult;
import org.jboss.arquillian.spi.TestResult.Status;
import org.jboss.arquillian.test.spi.annotation.ClassScoped;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * TestExecutorHandlerTestCase
 *
 * @author <a href="mailto:aknutsen@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class RemoteTestExecuterTestCase extends AbstractContainerTestTestBase
{
   @Mock
   private ContainerMethodExecutor executor;

   @Mock
   private TestMethodExecutor testExecutor;
   
   @Override
   protected void addExtensions(List<Class<?>> extensions)
   {
      extensions.add(RemoteTestExecuter.class);
   }
   
   @Test(expected = IllegalStateException.class)
   public void shouldThrowIllegalStateOnMissingContainerMethodExecutor() throws Exception
   {
      Mockito.when(testExecutor.getInstance()).thenReturn(this);
      Mockito.when(testExecutor.getMethod()).thenReturn(
            getTestMethod("shouldThrowIllegalStateOnMissingContainerMethodExecutor"));

      fire(new RemoteExecutionEvent(testExecutor));
   }
   
   @Test
   public void shouldInvokeContainerMethodExecutor() throws Exception 
   {
      TestResult result = new TestResult(Status.PASSED);
      Mockito.when(executor.invoke(testExecutor)).thenReturn(result);
      Mockito.when(testExecutor.getInstance()).thenReturn(this);
      Mockito.when(testExecutor.getMethod()).thenReturn(
            getTestMethod("shouldInvokeContainerMethodExecutor"));

      bind(ClassScoped.class, ContainerMethodExecutor.class, executor);
      org.jboss.arquillian.spi.event.suite.Test event = new org.jboss.arquillian.spi.event.suite.Test(testExecutor);
      fire(event);

      Mockito.verify(executor).invoke(testExecutor);
   }
   
   @Test
   public void shouldSetTestResult() throws Exception 
   {
      TestResult result = new TestResult(Status.PASSED);
      Mockito.when(executor.invoke(testExecutor)).thenReturn(result);
      Mockito.when(testExecutor.getInstance()).thenReturn(this);
      Mockito.when(testExecutor.getMethod()).thenReturn(
            getTestMethod("shouldSetTestResult"));

      bind(ClassScoped.class, ContainerMethodExecutor.class, executor);
      
      org.jboss.arquillian.spi.event.suite.Test event = new org.jboss.arquillian.spi.event.suite.Test(testExecutor);
      fire(event);
      
      Assert.assertEquals(
            "Should set result on event",
            result,
            getManager().resolve(TestResult.class));
   }
   
   private Method getTestMethod(String name) throws Exception
   {
      return this.getClass().getMethod(name);
   }
}
