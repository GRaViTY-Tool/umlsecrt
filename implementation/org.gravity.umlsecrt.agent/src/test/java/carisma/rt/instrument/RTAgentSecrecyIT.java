package carisma.rt.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class RTAgentSecrecyIT {

	@Test
	void enforcesSecrecyInForkedJvm() throws Exception {
		final String javaHome = System.getProperty("java.home");
		final String java = new File(javaHome, "bin/java").getAbsolutePath();
		final String classPath = System.getProperty("java.class.path");
		final String agent = new File("target", "org.gravity.umlsecrt.agent-1.0.0-SNAPSHOT.jar").getAbsolutePath();

		final List<String> command = new ArrayList<>();
		command.add(java);
		command.add("-javaagent:" + agent);
		command.add("-cp");
		command.add(classPath);
		command.add(RTAgentTestApplication.class.getName());

		final Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
		final String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
		final int exit = process.waitFor();

		assertEquals(0, exit, output);
	}
}
