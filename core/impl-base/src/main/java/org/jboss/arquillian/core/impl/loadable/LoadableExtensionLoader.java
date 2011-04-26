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

import java.util.Collection;

import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.impl.ServiceRegistry;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.core.spi.LoadableExtension.ExtensionBuilder;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.core.spi.context.Context;
import org.jboss.arquillian.core.spi.event.ManagerProcessing;

/**
 * LoadableExtensionLoader
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class LoadableExtensionLoader
{
   @Inject @ApplicationScoped
   private InstanceProducer<ServiceLoader> serviceLoaderProducer;
   
   private DynamicServiceLoader serviceLoader;
   
   public LoadableExtensionLoader()
   {
      this(new DynamicServiceLoader());
   }
   
   LoadableExtensionLoader(DynamicServiceLoader serviceLoader)
   {
      this.serviceLoader = serviceLoader;
   }

   public void load(@Observes final ManagerProcessing event)
   {
      final ServiceRegistry registry = new ServiceRegistry();
      
      Collection<LoadableExtension> extensions = serviceLoader.all(LoadableExtension.class);
      
      for(LoadableExtension extension : extensions)
      {
         extension.register(new ExtensionBuilder()
         {
            @Override
            public <T> ExtensionBuilder service(Class<T> service, Class<? extends T> impl)
            {
               registry.addService(service, impl);
               return this;
            }
            
            @Override
            public ExtensionBuilder observer(Class<?> handler)
            {
               event.observer(handler);
               return this;
            }
            
            @Override
            public ExtensionBuilder context(Class<? extends Context> context)
            {
               event.context(context);
               return this;
            }
         });
      }
      
      serviceLoaderProducer.set(registry.getServiceLoader());
   }
}
