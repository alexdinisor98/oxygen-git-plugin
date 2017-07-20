package com.oxygenxml.sdksamples.workspace.git.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Properties;
import java.util.Set;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.oxygenxml.sdksamples.workspace.git.constants.Constants;
import com.oxygenxml.sdksamples.workspace.git.jaxb.entities.Options;
import com.oxygenxml.sdksamples.workspace.git.jaxb.entities.RepositoryLocations;
import com.oxygenxml.sdksamples.workspace.git.jaxb.entities.RepositoryOption;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.util.UtilAccess;

/**
 * Used to save and load different user options
 * 
 * @author intern2
 *
 */
public class OptionsManager {
	/**
	 * Why not store everything into one file?
	 */
	private static final String REPOSITORY_FILENAME = "Options.xml";
	private static final String PROPERTIES_FILENAME = "Options.properties";
	private static final String KEY = "Beni";

	/**
	 * All Repositories that were selected by the user with their options
	 */
	private Options options = null;

	/**
	 * Properties file to store user options
	 */
	private Properties properties = new Properties();

	/**
	 * Singletone instance.
	 */
	private static OptionsManager instance;

	public static OptionsManager getInstance() {
		if (instance == null) {
			instance = new OptionsManager();
		}
		return instance;
	}

	/**
	 * Uses JAXB to load all the selected repositories from the users in the
	 * repositoryOptions variable
	 */
	private void loadRepositoryOptions() {
		if (options == null) {
			options = new Options();
		}
		if (options != null) {
			String fileName = REPOSITORY_FILENAME;
			JAXBContext jaxbContext;
			try {
				jaxbContext = JAXBContext.newInstance(Options.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				options = (Options) jaxbUnmarshaller.unmarshal(new File(Constants.RESOURCES_PATH + fileName));
			} catch (JAXBException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Uses JAXB to save all the selected repositories from the users in the
	 * repositoryOptions variable
	 */
	private void saveRepositoryOptions() {
		String fileName = REPOSITORY_FILENAME;
		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(Options.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(options, new File(Constants.RESOURCES_PATH + fileName));
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * Retrieves the repository selection list
	 * 
	 * @return a set with the repository options
	 */
	public Set<String> getRepositoryEntries() {
		loadRepositoryOptions();

		return options.getRepositoryLocations().getLocations();
	}

	/**
	 * Saves the given repository options
	 * 
	 * @param repositoryOption
	 *          - options to be saved
	 */
	public void addRepository(String repositoryOption) {
		loadRepositoryOptions();

		options.getRepositoryLocations().getLocations().add(repositoryOption);
		saveRepositoryOptions();
	}

	/**
	 * Saves the last selected repository from the user
	 * 
	 * @param path
	 *          - the path to the selected repository
	 */
	public void saveSelectedRepository(String path) {
		loadRepositoryOptions();
		options.setSelectedRepository(path);

		saveRepositoryOptions();
	}

	/**
	 * Loads the last selected repository from the user
	 * 
	 * @return the path to the selected repository
	 */
	public String getSelectedRepository() {
		loadRepositoryOptions();

		return options.getSelectedRepository();
	}

	/**
	 * Saves the user credentials for git push and pull
	 * 
	 * @param userCredentials
	 *          - the credentials to be saved
	 */
	public void saveGitCredentials(UserCredentials userCredentials) {
		loadRepositoryOptions();
		
		options.setUsername(userCredentials.getUsername());
		Cipher cipher = new Cipher();
		String password =cipher.encrypt(userCredentials.getPassword());
		options.setPassword(password);

		saveRepositoryOptions();

	}

	/**
	 * Loads the user credentials for git push and pull
	 * 
	 * @return the credentials
	 */
	public UserCredentials getGitCredentials() {
		loadRepositoryOptions();

		String username = options.getUsername();
		String password = options.getPassword();
		
		Cipher cipher = new Cipher();
		password = cipher.decrypt(password);
		
		UserCredentials userCredentials = new UserCredentials(username, password);
		return userCredentials;
	}


}
