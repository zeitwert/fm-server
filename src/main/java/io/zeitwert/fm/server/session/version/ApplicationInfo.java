package io.zeitwert.fm.server.session.version;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.stereotype.Component;

import io.zeitwert.fm.server.Application;

@Component("applicationVersion")
public class ApplicationInfo {

	private static String name = "unknown";
	private static String version = "unknown";

	@PostConstruct
	public void readVersion() {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		try {
			Model model;
			if ((new File("pom.xml")).exists()) {
				model = reader.read(new FileReader("pom.xml"));
			} else {
				InputStream is = Application.class.getResourceAsStream("/META-INF/maven/io.zeitwert/zeitwert-server/pom.xml");
				model = reader.read(new InputStreamReader(is));
			}
			model.getId();
			model.getGroupId();
			name = model.getArtifactId();
			version = model.getVersion();
		} catch (IOException | XmlPullParserException e) {
			e.printStackTrace();
		}
	}

	public static String getName() {
		return name;
	}

	public static String getVersion() {
		return version;
	}

}
