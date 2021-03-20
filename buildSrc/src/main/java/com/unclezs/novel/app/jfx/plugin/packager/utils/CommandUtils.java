package com.unclezs.novel.app.jfx.plugin.packager.utils;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Command utils
 */
public class CommandUtils {

	public static String execute(File workingDirectory, String executable, Object... arguments) throws IOException, CommandLineException {
		ExecutionResult result = executeWithResult(workingDirectory, executable, arguments);
		if (result.getExitCode() != 0) {
			throw new CommandLineException("Command execution failed: " + executable + " " + StringUtils.join(arguments, " "));
		}
		return result.getOutput();
	}

	public static String execute(String executable, Object... arguments) throws IOException, CommandLineException {
		return execute(new File("."), executable, arguments);
	}

	public static ExecutionResult executeWithResult(File workingDirectory, String executable, Object... arguments) throws IOException, CommandLineException {
		ExecutionResult result = new ExecutionResult();

		StringBuilder outputBuffer = new StringBuilder();
		StringBuilder errorBuffer = new StringBuilder();

		Commandline command = new Commandline();
		command.setWorkingDirectory(workingDirectory);
		command.setExecutable(executable);
		command.createArguments(arguments);

		String commandLine = command.getCommandLineAsString();

		Logger.info("Executing command: " + commandLine);

		Process process = command.execute();

		BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		while (process.isAlive() || output.ready() || error.ready()) {
			if (output.ready()) {
				String outputLine = output.readLine();
				Logger.info(outputLine);
				outputBuffer.append(outputLine + "\n");
			}
			if (error.ready()) {
				String errorLine = error.readLine();
				Logger.error(errorLine);
				errorBuffer.append(errorLine + "\n");
			}
		}
		output.close();
		error.close();

		result.setCommandLine(commandLine);
		result.setOutput(outputBuffer.toString());
		result.setError(errorBuffer.toString());
		result.setExitCode(process.exitValue());

		return result;
	}

}
