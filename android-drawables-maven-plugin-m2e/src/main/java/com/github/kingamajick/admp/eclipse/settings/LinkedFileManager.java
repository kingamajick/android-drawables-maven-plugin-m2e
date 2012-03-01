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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import com.google.inject.Inject;

/**
 * This class manages the linked files stored in a project for the android-drawable-maven-plugin.
 * 
 * @author R King
 * 
 */
public class LinkedFileManager {

	@Inject private SettingsHandler settingsHandler;

	/**
	 * Removes all the linked files and their related settings from the project. If this fails, the linked files and the settings should
	 * still be in sync.
	 * 
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 */
	public void removeLinkedFiles(final IProject project, final IProgressMonitor monitor) throws CoreException {
		List<String> removedLinkedFiles = new ArrayList<String>();
		try {
			removeLinkedFiles(project, removedLinkedFiles, monitor);
		}
		finally {
			// Always clear up the linked files in the settings, including is the removeLinkedFiles task fails
			try {
				this.settingsHandler.removeLinkedFiles(project, removedLinkedFiles.toArray(new String[removedLinkedFiles.size()]));
			}
			finally {
				project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}
		}
	}

	/**
	 * Creates linked files for all files contained in the unpackLocation and their related settings. If this fails, the linked files and
	 * the settings should still be in sync.
	 * 
	 * @param project
	 * @param unpackLocation
	 * @param monitor
	 * @throws CoreException
	 */
	public void generateLinkedFiles(final IProject project, final IFolder unpackLocation, final IProgressMonitor monitor) throws CoreException {
		List<String> newlyLinkedFiles = new ArrayList<String>();
		try {
			createLinkedFiles(project, unpackLocation, newlyLinkedFiles, monitor);
		}
		finally {
			// Always add the newly linked files to the settings, including if the createLinkedFiles was not successful. This should
			// 'attempt' to keep the settings in sync with the reality.
			try {
				this.settingsHandler.addLinkedFile(project, newlyLinkedFiles.toArray(new String[newlyLinkedFiles.size()]));
			}
			finally {
				project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}
		}
	}

	/**
	 * Removes all the linked files for the given project created for the android-drawable-maven-plugin configurator as well as clearing the
	 * settings file.
	 * 
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 */
	void removeLinkedFiles(final IProject project, final List<String> removedLinkedFiles, final IProgressMonitor monitor) throws CoreException {
		List<String> linkedFiles = this.settingsHandler.getCurrentLinkedFiles(project);
		for (String linkedFile : linkedFiles) {
			IFile file = project.getFile(linkedFile);
			if (file.exists()) {
				if (file.isLinked()) {
					file.delete(true, monitor);
					removedLinkedFiles.add(file.getProjectRelativePath().toString());
				}
				else {
					// TODO: Log warning that an attempt to remove a none linked file was ignored.
				}
			}
		}
	}

	/**
	 * Creates a linked resources for all files contained in the source {@link IFolder} (as well as its sub folders), and create a link to
	 * these files in the project. Any folders discovered in the source folder will be created in the target project. Any file which has had
	 * a link created for it will be added to the linked files list.
	 * 
	 * @param project
	 * @param source
	 * @param linkedFiles
	 * @param monitor
	 * @throws CoreException
	 */
	void createLinkedFiles(final IProject project, final IFolder source, final List<String> linkedFiles, final IProgressMonitor monitor) throws CoreException {
		if (source.exists()) {
			List<IFile> files = new ArrayList<IFile>();
			getFiles(source, files);
			for (IFile file : files) {
				IPath targetPath = file.getFullPath().makeRelativeTo(source.getFullPath());
				IFile target = project.getFile(targetPath);
				prepareFolder((IFolder) target.getParent(), monitor);
				target.createLink(file.getLocation(), IResource.REPLACE, monitor);
				linkedFiles.add(target.getFullPath().makeRelativeTo(project.getFullPath()).toString());
			}
		}

	}

	/**
	 * Populates the list files, with all {@link IFile}s found in folder and its sub folders.
	 * 
	 * @param folder
	 * @param files
	 * @throws CoreException
	 */
	void getFiles(final IFolder folder, final List<IFile> files) throws CoreException {
		for (IResource resource : folder.members()) {
			if (resource instanceof IFile) {
				files.add((IFile) resource);
			}
			else if (resource instanceof IFolder) {
				getFiles((IFolder) resource, files);
			}
			else {
				// Unexpected resource type, is it possible for a IResource to be anything over than an IFile or IFolder in this
				// context?
			}
		}
	}

	/**
	 * Creates the folder if it does not already exist, and also any parent folders required to create that folder.
	 * 
	 * @param folder
	 * @param monitor
	 * @throws CoreException
	 */
	public void prepareFolder(final IFolder folder, final IProgressMonitor monitor) throws CoreException {
		IContainer parent = folder.getParent();
		if (parent instanceof IFolder) {
			prepareFolder((IFolder) parent, monitor);
		}
		if (!folder.exists()) {
			folder.create(true, true, monitor);
		}
	}

}
