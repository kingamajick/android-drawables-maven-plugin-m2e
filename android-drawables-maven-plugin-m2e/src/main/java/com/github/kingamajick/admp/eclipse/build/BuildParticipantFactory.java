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

import org.apache.maven.plugin.MojoExecution;

import com.github.kingamajick.admp.eclipse.Activator;

/**
 * @author R King
 * 
 */
public class BuildParticipantFactory {

	public UnpackBuildParticipant create(final MojoExecution execution) {
		String goal = execution.getGoal();
		UnpackBuildParticipant buildParticipant = new UnpackBuildParticipant(execution, true);
		// Can I get Guice to do this implicitly.
		Activator.getDefault().getInjector().injectMembers(buildParticipant);
		return buildParticipant;
	}
}
