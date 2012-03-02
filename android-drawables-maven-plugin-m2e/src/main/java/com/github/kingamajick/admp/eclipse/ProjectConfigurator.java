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

import java.io.File;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kingamajick.admp.eclipse.build.BuildParticipantFactory;
import com.github.kingamajick.admp.eclipse.settings.LinkedFileManager;
import com.google.inject.Inject;

/**
 * Adds linked files connected to the resources in the ${unpackLocation}.
 * 
 * @author R King
 * 
 */
public class ProjectConfigurator extends AbstractProjectConfigurator {

	private static final String ANDROID_DRAWABLES_DIR = "/android-drawables";
	private static final String UNPACK_LOCATION_PROP = "unpackLocation";

	private Logger logger = LoggerFactory.getLogger(ProjectConfigurator.class);
	@Inject LinkedFileManager linkedFileManager;
	@Inject BuildParticipantFactory buildParticipantFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator#configure(org.eclipse.m2e.core.project.configurator.
	 * ProjectConfigurationRequest, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void configure(final ProjectConfigurationRequest request, final IProgressMonitor monitor) throws CoreException {
		IProject project = request.getProject();
		MavenProject mavenProject = request.getMavenProject();

		this.linkedFileManager.removeLinkedFiles(project, monitor);

		String outputDirectory = mavenProject.getBuild().getDirectory();
		String unpackLocationPath = mavenProject.getProperties().getProperty(UNPACK_LOCATION_PROP, outputDirectory + ANDROID_DRAWABLES_DIR);
		// Make the unpackLocation relative to the project root
		File baseDir = mavenProject.getBasedir();
		String relativeunpackLocation = unpackLocationPath.substring(baseDir.toString().length());
		IFolder unpackLocation = project.getFolder(relativeunpackLocation);
		this.linkedFileManager.generateLinkedFiles(project, unpackLocation, monitor);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator#unconfigure(org.eclipse.m2e.core.project.configurator.
	 * ProjectConfigurationRequest, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void unconfigure(final ProjectConfigurationRequest request, final IProgressMonitor monitor) throws CoreException {
		this.linkedFileManager.removeLinkedFiles(request.getProject(), monitor);

	}

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
	 * @see org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator#mavenProjectChanged(org.eclipse.m2e.core.project.
	 * MavenProjectChangedEvent, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void mavenProjectChanged(final MavenProjectChangedEvent event, final IProgressMonitor monitor) throws CoreException {
		super.mavenProjectChanged(event, monitor);
	}

	IFolder getUnpackLocation(final IMavenProjectFacade mavenProjectFacade) {
		IProject project = mavenProjectFacade.getProject();
		MavenProject mavenProject = mavenProjectFacade.getMavenProject();

		String outputDirectory = mavenProject.getBuild().getDirectory();
		String unpackLocationPath = mavenProject.getProperties().getProperty(UNPACK_LOCATION_PROP, outputDirectory + ANDROID_DRAWABLES_DIR);
		// Make the unpackLocation relative to the project root
		File baseDir = mavenProject.getBasedir();
		String relativeunpackLocation = unpackLocationPath.substring(baseDir.toString().length());
		IFolder unpackLocation = project.getFolder(relativeunpackLocation);
		return unpackLocation;
	}

}
