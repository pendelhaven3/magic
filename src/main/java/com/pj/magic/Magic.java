package com.pj.magic;


public class Magic {

	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
        		Launcher launcher = new Launcher();
        		launcher.launch();
            }
        });
	}
	
}
