package com.LilG.com;

import java.io.*;

import static java.lang.Thread.sleep;

/**
 * Created by ggonz on 5/6/2016.
 * Tests command line
 */
public class ProcessTest {
	public static void main(String[] args) throws IOException {
		ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-command", "-");
		Process p;
		try {
			p = pb.start();
		} catch (IOException e) {
			System.out.println("Failed to start powershell");
			return;
		}
		BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		p_stdin.write("dir");
		p_stdin.flush();
		p_stdin.flush();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		final String[] line = new String[1];
		System.out.println("Begin!");
		try {
			long time = System.currentTimeMillis() + 10 * 1000;
			System.out.println(System.currentTimeMillis());
			System.out.println(time);
			System.out.println(System.currentTimeMillis());
			while (time > System.currentTimeMillis()) {
				System.out.println("reading...");
				Thread thread = new Thread(() -> {
					try {
						line[0] = bufferedReader.readLine();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				thread.run();
				sleep(500);
				System.out.println(line[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		System.out.println("Exit");

	}

}
