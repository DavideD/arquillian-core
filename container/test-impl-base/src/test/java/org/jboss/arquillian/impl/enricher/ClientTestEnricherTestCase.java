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
package org.jboss.arquillian.impl.enricher;

import java.util.Arrays;
import java.util.List;

import org.jboss.arquillian.container.spi.Container;
import org.jboss.arquillian.container.test.test.AbstractContainerTestTestBase;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.spi.TestEnricher;
import org.jboss.arquillian.spi.event.enrichment.AfterEnrichment;
import org.jboss.arquillian.spi.event.enrichment.BeforeEnrichment;
import org.jboss.arquillian.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Verifies that the TestEnricher SPI is called.
 *
 * @author <a href="mailto:aknutsen@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientTestEnricherTestCase extends AbstractContainerTestTestBase
{
   @Mock
   private ServiceLoader serviceLoader;
   
   @Mock
   private TestEnricher enricher;

   @Mock
   private Container container;
   
   /* (non-Javadoc)
    * @see org.jboss.arquillian.core.test.AbstractManagerTestBase#addExtensions(java.util.List)
    */
   @Override
   protected void addExtensions(List<Class<?>> extensions)
   {
      extensions.add(ClientTestEnricher.class);
   }
   
   @Test
   public void shouldCallAllEnrichers() throws Exception
   {
      ClassLoader cl = this.getClass().getClassLoader();
      Mockito.when(serviceLoader.all(cl, TestEnricher.class)).thenReturn(Arrays.asList(enricher, enricher));
      Mockito.when(container.getClassLoader()).thenReturn(cl);
      
      bind(SuiteScoped.class, ServiceLoader.class, serviceLoader);
      bind(SuiteScoped.class, Container.class, container);
      
      fire(new Before(this, getClass().getMethod("shouldCallAllEnrichers")));
      
      Mockito.verify(enricher, Mockito.times(2)).enrich(this);
      
      assertEventFired(BeforeEnrichment.class, 1);
      assertEventFired(AfterEnrichment.class, 1);
   }
}
