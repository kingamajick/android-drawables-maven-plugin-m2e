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
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kingamajick.admp.eclipse.settings.LinkedFileManager;
import com.google.inject.Inject;

/**
 * @author R King
 * 
 */
public class UnpackBuildParticipant extends MojoExecutionBuildParticipant {

	private Logger logger = LoggerFactory.getLogger(UnpackBuildParticipant.class);
	@Inject LinkedFileManager linkedFileManager;

	/**
	 * @param execution
	 * @param runOnIncremental
	 */
	public UnpackBuildParticipant(final MojoExecution execution, final boolean runOnIncremental) {
		super(execution, runOnIncremental);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant#clean(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void clean(final IProgressMonitor monitor) throws CoreException {
		this.logger.debug("clean");
		IMavenProjectFacade mavenProjectFacade = getMavenProjectFacade();
		IFolder unpackLocation = getUnpackLocation(mavenProjectFacade);
		if (unpackLocation.exists()) {
			unpackLocation.delete(true, monitor);
		}
		this.linkedFileManager.removeLinkedFiles(mavenProjectFacade.getProject(), monitor);

		super.clean(monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant#build(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Set<IProject> build(final int kind, final IProgressMonitor monitor) throws Exception {
		this.logger.debug("build");

		Set<IProject> result = super.build(kind, monitor);

		IMavenProjectFacade mavenProjectFacade = getMavenProjectFacade();
		IFolder unpackLocation = getUnpackLocation(mavenProjectFacade);
		this.linkedFileManager.removeLinkedFiles(mavenProjectFacade.getProject(), monitor);
		this.linkedFileManager.generateLinkedFiles(mavenProjectFacade.getProject(), unpackLocation, monitor);

		return result;
	}

	IFolder getUnpackLocation(final IMavenProjectFacade mavenProjectFacade) {
		IProject project = mavenProjectFacade.getProject();
		MavenProject mavenProject = mavenProjectFacade.getMavenProject();

		String outputDirectory = mavenProject.getBuild().getDirectory();
		String unpackLocationPath = mavenProject.getProperties().getProperty("unpackLocation", outputDirectory + "/android-drawables");
		// Make the unpackLocation relative to the project root
		File baseDir = mavenProject.getBasedir();
		String relativeunpackLocation = unpackLocationPath.substring(baseDir.toString().length());
		IFolder unpackLocation = project.getFolder(relativeunpackLocation);
		return unpackLocation;
	}

}
