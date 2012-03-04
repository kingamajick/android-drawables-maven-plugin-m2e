/**
 * Copyright 2012 R King
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.kingamajick.admp.eclipse;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;

import com.github.kingamajick.admp.eclipse.build.BuildParticipantFactory;
import com.google.inject.Inject;

/**
 * Adds linked files connected to the resources in the ${unpackLocation}.
 * 
 * @author R King
 * 
 */
public class ProjectConfigurator extends AbstractProjectConfigurator {

	@Inject BuildParticipantFactory buildParticipantFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator#getBuildParticipant(org.eclipse.m2e.core.project.
	 * IMavenProjectFacade, org.apache.maven.plugin.MojoExecution, org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata)
	 */
	public AbstractBuildParticipant getBuildParticipant(final IMavenProjectFacade projectFacade, final MojoExecution execution, final IPluginExecutionMetadata executionMetadata) {
		return this.buildParticipantFactory.create(execution);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator#configure(org.eclipse.m2e.core.project.configurator.
	 * ProjectConfigurationRequest, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void configure(final ProjectConfigurationRequest request, final IProgressMonitor monitor) throws CoreException {
		// Do nothing
	}

}
