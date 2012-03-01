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
package com.github.kingamajick.admp.eclipse.settings;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.github.kingamajick.admp.eclipse.Activator;

/**
 * @author R King
 * 
 */
public class SettingsHandler {

	private final static String LINKED_FILES_PREF_NODE = "linkedFiles";
	private final static String PLUGIN_ID = Activator.PLUGIN_ID;

	/**
	 * Helper method to generated {@link CoreException}
	 * 
	 * @param pluginID
	 * @param message
	 * @param t
	 * @return
	 */
	private static CoreException createCoreError(final String pluginID, final String message, final Throwable t) {
		return new CoreException(new Status(IStatus.ERROR, SettingsHandler.PLUGIN_ID, "Unable to write settings", t));
	}

	public List<String> getCurrentLinkedFiles(final IProject project) throws CoreException {
		ProjectScope projectScope = new ProjectScope(project);
		IEclipsePreferences prefs = projectScope.getNode(SettingsHandler.PLUGIN_ID);
		Preferences linkedFilesPref = prefs.node(LINKED_FILES_PREF_NODE);
		try {
			return Arrays.asList(linkedFilesPref.keys());
		}
		catch (BackingStoreException e) {
			throw createCoreError(PLUGIN_ID, "Unable to read current settings", e);
		}
	}

	public void addLinkedFile(final IProject project, final String... linkedFiles) throws CoreException {
		ProjectScope projectScope = new ProjectScope(project);
		IEclipsePreferences prefs = projectScope.getNode(SettingsHandler.PLUGIN_ID);
		Preferences linkedFilesPref = prefs.node(LINKED_FILES_PREF_NODE);
		for (String linkedFile : linkedFiles) {
			linkedFilesPref.putBoolean(linkedFile, true);
		}
		try {
			linkedFilesPref.flush();
		}
		catch (BackingStoreException e) {
			throw createCoreError(PLUGIN_ID, "Unable to write settings changes when trying to add linked files", e);
		}
	}

	public void removeLinkedFiles(final IProject project, final String... linkedFiles) throws CoreException {
		ProjectScope projectScope = new ProjectScope(project);
		IEclipsePreferences prefs = projectScope.getNode(SettingsHandler.PLUGIN_ID);
		Preferences linkedFilesPref = prefs.node(LINKED_FILES_PREF_NODE);
		for (String linkedFile : linkedFiles) {
			linkedFilesPref.remove(linkedFile);
		}
		try {
			linkedFilesPref.flush();
		}
		catch (BackingStoreException e) {
			throw createCoreError(PLUGIN_ID, "Unable to write settings changes when trying to remove linked files", e);
		}
	}

	public void removeAllLinkedFiles(final IProject project) throws CoreException {
		ProjectScope projectScope = new ProjectScope(project);
		IEclipsePreferences prefs = projectScope.getNode(SettingsHandler.PLUGIN_ID);
		Preferences linkedFilesPref = prefs.node(LINKED_FILES_PREF_NODE);
		try {
			linkedFilesPref.removeNode();
			linkedFilesPref.flush();
		}
		catch (BackingStoreException e) {
			throw createCoreError(PLUGIN_ID, "Unable to write settings changes when trying to remove all linked files", e);
		}
	}
}
