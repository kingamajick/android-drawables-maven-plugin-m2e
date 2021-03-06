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
package com.github.kingamajick.admp.eclipse.build;

import java.io.File;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;

/**
 * @author R King
 * 
 */
public class UnpackBuildParticipant extends MojoExecutionBuildParticipant {

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
	 * @see org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant#build(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Set<IProject> build(final int kind, final IProgressMonitor monitor) throws Exception {
		Set<IProject> result = super.build(kind, monitor);

		File unpackLocation = MavenPlugin.getMaven().getMojoParameterValue(getSession(), getMojoExecution(), "unpackLocation", File.class);
		getBuildContext().refresh(new File(unpackLocation, "res"));
		return result;
	}
}
