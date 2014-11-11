/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.io;


import java.util.ArrayList;

import org.openmarkov.core.exception.CanNotWriteNetworkToFileException;
import org.openmarkov.core.exception.NotRecognisedNetworkFileExtensionException;
import org.openmarkov.core.exception.WriterException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.io.ProbNetReader;
import org.openmarkov.core.io.ProbNetWriter;
import org.openmarkov.core.io.format.annotation.FormatManager;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;





/**
 * This class contains some routines to load and to save nets.
 * 
 * @author jmendoza
 * @version 1.0 - jmendoza -
 * @version 1.1 - jlgozalo - Catch block deleted form OpenNetwork (not required)
 *          private constructor added
 */
public class NetsIO {

	// private constructor for a class with only static members
	private NetsIO() {

	}

	/**
	 * Opens a network saved in a file and returns the object that contains its
	 * information.
	 * 
	 * @param fileName
	 *            file where the network is saved.
	 * @return an ProbNetInfo object with the information of the network.
	 * @throws Exception
	 *             if the file doesn't exist or the file format isn't correct.
	 */
	public static ProbNetInfo openNetworkFile(String fileName) throws Exception {
		String fileExtension = getFileExtension(fileName);
		FormatManager formatManager = FormatManager.getInstance();
		ProbNetReader probNetReader = formatManager.getProbNetReader(fileExtension);
		
		ProbNetInfo probNetInfo = probNetReader.loadProbNet(fileName);
		
		
		if (probNetInfo == null) {
			System.out.println("NetsIO.openNetworkFile from "
					+ fileName + ": probNet null");
		}
		return probNetInfo;
		

	/*	if (fileExtension.contentEquals("elv")) {
				//return ElviraParser.getUniqueInstance().loadProbNet(fileName);
		} else if (fileExtension.contentEquals("xml")) {
			/*ProbNet probNet = 
				XMLReader.getUniqueInstance().loadProbNet(fileName);
			if (probNet == null) {
				System.out.println("NetsIO.openNetworkFile from "
						+ fileName + ": probNet null");
			}
			return probNet;*/
		/*} else if (fileExtension.contentEquals("pgmx")) {
			ProbNet probNet = 
				PGMXReader.getUniqueInstance().loadProbNet(fileName);
			if (probNet == null) {
				System.out.println("NetsIO.openNetworkFile from "
						+ fileName + ": probNet null");
			}
			return probNet;
		
		}*/
		

	}

    /**
     * Saves a network in a file.
     * @param network - network to save in the file
     * @param evidence - list of evidence cases
     * @param fileName - file where the network is going to be saved
     * @throws NotRecognisedNetworkFileExtensionException - if file extension is
     *             not recognised
     * @throws CanNotWriteNetworkToFileException - if an I/O error has happened
     */
    public static void saveNetworkFile (ProbNet network,
                                        ArrayList<EvidenceCase> evidence,
                                        String fileName)
        throws NotRecognisedNetworkFileExtensionException,
        CanNotWriteNetworkToFileException
    {
        String fileExtension = getFileExtension (fileName);
        FormatManager formatManager = FormatManager.getInstance ();
        ProbNetWriter probNetWriter = formatManager.getProbNetWriter (fileExtension);
        try
        {
            probNetWriter.writeProbNet (fileName, network, evidence);
            /*
             * if (fileExtension.contentEquals("elv")) {
             * //ElviraWriter.getUniqueInstance().writeProbNet(fileName,
             * network); } else if (fileExtension.contentEquals("xml")) {
             * //XMLWriter.getUniqueInstance().writeProbNet(fileName, network);
             * } else if (fileExtension.contentEquals("pgmx")) {
             * PGMXWriter.getUniqueInstance().writeProbNet(fileName, network); }
             * else if (fileExtension.contentEquals("bif")) {
             * //HuginWriter.getUniqueInstance().writeProbNet(fileName,
             * network); } else { throw new
             * NotRecognisedNetworkFileExtensionException(fileName); } } catch
             * (IOException ex) { throw new
             * CanNotWriteNetworkToFileException(fileName); } 
             */
        }
        catch (WriterException ex)
        {
            throw new CanNotWriteNetworkToFileException (fileName);
        }
    }

    /**
     * Saves a network in a file.
     * @param network - network to save in the file
     * @param fileName - file where the network is going to be saved
     * @throws NotRecognisedNetworkFileExtensionException - if file extension is
     *             not recognised
     * @throws CanNotWriteNetworkToFileException - if an I/O error has happened
     */
    public static void saveNetworkFile (ProbNet network, String fileName)
        throws NotRecognisedNetworkFileExtensionException,
        CanNotWriteNetworkToFileException
    {
        saveNetworkFile (network, new ArrayList<EvidenceCase> (), fileName);
    }

	private static String getFileExtension(String fileName) {

		String fileExtension = null;
		int i = fileName.lastIndexOf('.');
		if ((i > 0) && (i < (fileName.length() - 1))) {
			fileExtension = fileName.substring(i + 1).toLowerCase();
		}

		return fileExtension;

	}

}
