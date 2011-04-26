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
package org.jboss.arquillian.container.test.impl;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.core.spi.annotation.Extension;
import org.jboss.arquillian.impl.client.ContainerEventController;
import org.jboss.arquillian.impl.client.container.ContainerRestarter;
import org.jboss.arquillian.impl.client.deployment.DeploymentGenerator;
import org.jboss.arquillian.impl.client.deployment.tool.ArchiveDeploymentToolingExporter;
import org.jboss.arquillian.impl.client.protocol.ProtocolRegistryCreator;
import org.jboss.arquillian.impl.deployment.ArquillianDeploymentAppender;
import org.jboss.arquillian.impl.enricher.ClientTestEnricher;
import org.jboss.arquillian.impl.enricher.resource.ArquillianResourceTestEnricher;
import org.jboss.arquillian.impl.execution.ClientTestExecuter;
import org.jboss.arquillian.impl.execution.LocalTestExecuter;
import org.jboss.arquillian.impl.execution.RemoteTestExecuter;
import org.jboss.arquillian.spi.TestEnricher;
import org.jboss.arquillian.spi.client.deployment.AuxiliaryArchiveAppender;

/**
 * ContainerTestExtension
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@Extension(name = "arquillian.container.test")
public class ContainerTestExtension implements LoadableExtension
{
   @Override
   public void register(ExtensionBuilder builder)
   {
      builder.service(AuxiliaryArchiveAppender.class, ArquillianDeploymentAppender.class)
             .service(TestEnricher.class, ArquillianResourceTestEnricher.class);
      
      builder.observer(ContainerEventController.class)
             .observer(ContainerRestarter.class)
             .observer(DeploymentGenerator.class)
             .observer(ArchiveDeploymentToolingExporter.class)
             .observer(ProtocolRegistryCreator.class)
             .observer(ClientTestEnricher.class)
             .observer(ClientTestExecuter.class)
             .observer(LocalTestExecuter.class)
             .observer(RemoteTestExecuter.class);
   }

}
