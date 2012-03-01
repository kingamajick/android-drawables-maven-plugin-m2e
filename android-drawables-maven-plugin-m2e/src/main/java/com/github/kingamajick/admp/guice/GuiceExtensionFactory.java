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
package com.github.kingamajick.admp.guice;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

import com.github.kingamajick.admp.eclipse.Activator;
import com.google.inject.Injector;

/**
 * A implementation of {@link IExecutableExtension} and {@link IExecutableExtensionFactory} to use Guice to inject any required fields
 * before the class is returned. The syntax to use this extension is class="guice.GuiceExtensionFactory:<classToLoad>".
 * 
 * @author R King
 * 
 */
public class GuiceExtensionFactory implements IExecutableExtension, IExecutableExtensionFactory {

	private final Activator BundleActivator = Activator.getDefault();
	private final Bundle bundle = this.BundleActivator.getBundle();
	private final Injector injector = this.BundleActivator.getInjector();
	private String classToInstantiate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 * java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		if (data == null) {
			throw new CoreException(createErrorStatus("GuiceExtensionFactory requires a class as an additional parameter, for example 'class=\"guice.GuiceExtensionFactory:<class>\""));
		}
		if (!(data instanceof String)) {
			throw new CoreException(createErrorStatus("Unexpected parameter type " + data.getClass().getName()
					+ ", expected String, configuration for the class attribute should be of the form 'class=\"guice.GuiceExtensionFactory:<class>\""));
		}
		this.classToInstantiate = (String) data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IExecutableExtensionFactory#create()
	 */
	public Object create() throws CoreException {
		try {
			Class<?> clazz = this.bundle.loadClass(this.classToInstantiate);
			return this.injector.getInstance(clazz);
		}
		catch (ClassNotFoundException e) {
			throw new CoreException(createErrorStatus("Unable to find class " + this.classToInstantiate, e));
		}
	}

	/**
	 * Helper method to create an {@link IStatus} representing an error for the contributor with the given message.
	 * 
	 * @param message
	 * @return
	 */
	private IStatus createErrorStatus(final String message) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
	}

	/**
	 * Helper method to create an {@link IStatus} representing an error for the contributor with the given message and {@link Throwable}.
	 * 
	 * @param message
	 * @param throwable
	 * @return
	 */
	private IStatus createErrorStatus(final String message, final Throwable throwable) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, throwable);
	}

}
