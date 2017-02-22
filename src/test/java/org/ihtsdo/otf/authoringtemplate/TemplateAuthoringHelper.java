package org.ihtsdo.otf.authoringtemplate;

import com.google.common.io.Files;

import java.io.*;

/**
 * This is not a test, please ignore.
 */
public class TemplateAuthoringHelper {

	public static void main(String[] args) throws IOException {
		File file = new File("snomed-templates/.scratch.txt");
		File tempFile = new File(file.getAbsolutePath() + ".tmp");

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
				String line;
				while ((line = reader.readLine()) != null) {
					line = line.replace("\\t", "\t");
					line = line.replace("\\n", "\n");
					writer.write(line);
					writer.newLine();
//
//					line = line.replace("\t", "\\t");
//					line = line.replace("\n", "\\n");
//					line += "\\n";
//					writer.write(line);

				}
			}
		}
		Files.move(tempFile, file);
	}

}
