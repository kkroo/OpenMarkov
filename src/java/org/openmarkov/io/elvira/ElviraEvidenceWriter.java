/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.io.elvira;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.Variable;

/** @author marias */
public class ElviraEvidenceWriter {

	// Methods
	/** @param fileName = path + network name + extension.
	 * @param probNet. <code>ProbNet</code> <code>String</code>
	 * @throws IOException */
	public static void writeEvidenceCase(String fileName, EvidenceCase evidence)
			throws IOException {
		FileWriter writer;
		PrintWriter out;
		writer = new FileWriter(fileName);
		out = new PrintWriter(writer);
		writeEvidencePreamble(out, evidence);
		writeFindings(out, evidence);
		out.println("\n}");
		out.close();
	}

	private static void writeEvidencePreamble(
			PrintWriter out, EvidenceCase evidence) {
		out.println("//	   Evidence case");
		out.println("//	   Elvira format\n");
		out.println("evidence NoName {\n");
		out.println("//	   Evidence additionalProperties\n");
		out.println("title = " + '"' + "Untitled" + '"' + ";");
		out.println("version = 1.0;\n");
	}

	private static void writeFindings(PrintWriter out, EvidenceCase evidence) {
		for (Finding finding : evidence.getFindings()) {
			int stateIndex = finding.getStateIndex();
			Variable variable = finding.getVariable();
			int numStates = variable.getNumStates();
			int elviraEvidenceStateIndex = numStates - stateIndex - 1;
			out.println(finding.getVariable().getName() + " = "
					+ elviraEvidenceStateIndex + ",     // "
					+ variable.getStateName(stateIndex));
		}
	}

}
