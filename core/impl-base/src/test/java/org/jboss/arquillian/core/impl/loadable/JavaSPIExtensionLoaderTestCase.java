/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
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
package org.jboss.arquillian.core.impl.loadable;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import junit.framework.Assert;

import org.jboss.arquillian.core.impl.loadable.util.FakeExtensionLoader;
import org.jboss.arquillian.core.impl.loadable.util.ShouldBeIncluded;
import org.junit.Test;


/**
 * JavaSPIExtensionLoaderTestCase
 *
 * @author Davide D'Alto
 * @version $Revision: $
 */
public class JavaSPIExtensionLoaderTestCase 
{

   @Test
   public void shouldBeAbleToAddSelectedProvider() throws Exception
   {
      JavaSPIExtensionLoader loader = new JavaSPIExtensionLoader();
      Collection<FakeExtensionLoader> all = loader.all(JavaSPIExtensionLoaderTestCase.class.getClassLoader(), FakeExtensionLoader.class);
      Assert.assertEquals("Unexpected number of provider loaded", 1, all.size());
      Assert.assertEquals("Wrong provider loaded", ShouldBeIncluded.class, all.iterator().next().getClass());
   }
   
   @Test
   public void shouldBeAbleToAddSelectedProviderFromJars() throws Exception
   {
      URL resource1 = createUrlFromResourceName("loader-test1.jar");
      URL resource2 = createUrlFromResourceName("loader-test2.jar");
      
      ClassLoader emptyClassLoader = new ClassLoader(null){};
      ClassLoader originalClassLoader = SecurityActions.getThreadContextClassLoader();
      ClassLoader classLoader = new URLClassLoader(new URL[]{resource1, resource2}, null);
      
      Collection<?> all = null;
      Class<?> expectedClass = null;
      try {
         Thread.currentThread().setContextClassLoader(emptyClassLoader);
         Class<?> extension = classLoader.loadClass("org.jboss.arquillian.core.impl.loadable.util.FakeExtensionLoader");
         expectedClass = classLoader.loadClass("org.jboss.arquillian.core.impl.loadable.util.ShouldBeIncluded");
         all = new JavaSPIExtensionLoader().all(classLoader, extension);
      } finally {
         Thread.currentThread().setContextClassLoader(originalClassLoader);
      }
      
      Assert.assertEquals("Unexpected number of provider loaded", 1, all.size());
      Assert.assertEquals("Wrong provider loaded", expectedClass, all.iterator().next().getClass());
   }

   private URL createUrlFromResourceName(String resource)
   {
      return SecurityActions.getThreadContextClassLoader().getResource(resource);
   }
}
