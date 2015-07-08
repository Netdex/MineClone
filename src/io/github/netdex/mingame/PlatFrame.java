package io.github.netdex.mingame;
import javax.swing.JFrame;

public class PlatFrame extends JFrame {

	private static final int FRAME_LENGTH = 15;
	
	public PlatFrame() throws Exception {
		super("Mineclone");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final Plat sp = new Plat();
		this.add(sp);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						long time = System.currentTimeMillis();
						sp.tick();
						long end = System.currentTimeMillis() - time;
						Thread.sleep(FRAME_LENGTH - end);
					} catch (Exception e) {

					}
				}
			}
		}).start();
	}

	public static void main(String[] args) throws Exception {
		PlatFrame ss = new PlatFrame();
		ss.pack();
		ss.setVisible(true);
	}

}
