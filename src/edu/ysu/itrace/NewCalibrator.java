package edu.ysu.itrace;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


public abstract class NewCalibrator extends JFrame {
	//Panel that houses calibration animation.
	private class Animation extends JPanel implements ActionListener{
		private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		//Points and indices.
		private Point[] points = new Point[10];
		private int originIndex;
		private int destinationIndex;
		//Sprite parameters.
		private int circleWidth;
		private int circleHeight;
		private int dotWidth;
		private int dotHeight;
		private int circleX;
		private int circleY;
		private int dotX;
		private int dotY;
		//Animation variables.
		Timer timer;
		private double t;
		
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(Color.red);
			g2d.fillOval(circleX,circleY,circleWidth,circleHeight);
			g2d.setColor(Color.black);
			g2d.fillOval(dotX,dotY,dotWidth,dotHeight);
		}
		
		public void actionPerformed( ActionEvent e ){
	        if( originIndex != destinationIndex ){
	            t++;
	            circleX = ( points[originIndex].x + ( points[destinationIndex].x-points[originIndex].x )*( int )t/30 )-circleWidth/2;
	            circleY = ( points[originIndex].y + ( points[destinationIndex].y-points[originIndex].y )*( int )t/30 )-circleHeight/2;
	            dotX = ( points[originIndex].x + ( points[destinationIndex].x-points[originIndex].x )*( int )t/30 )-dotWidth/2;
	            dotY = ( points[originIndex].y + ( points[destinationIndex].y-points[originIndex].y )*( int )t/30 )-dotHeight/2;
	            if( circleX == points[destinationIndex].x-circleWidth/2 && circleY == points[destinationIndex].y-circleHeight/2  ){
	                originIndex = destinationIndex;
	                t = 0;
	            }
	        }else{
	            t += Math.PI/64;
	            circleWidth = 50 - ( int )( Math.sin( t )*40 );
	            circleHeight = 50 - ( int )( Math.sin( t )*40 );
	            if( t > 2*Math.PI ){
	                circleWidth = 50;
	                circleHeight = 50;
	                if( destinationIndex == 9 ) destinationIndex = 0;
	                else destinationIndex++;
	                t = 0;
	                try {
						useCalibrationPoint(points[destinationIndex].x, points[destinationIndex].y);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	                if(destinationIndex == 9){
	                	try {
							stopCalibration();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	                	SwingUtilities.getWindowAncestor(this).dispose();
	                }
	            }
	            circleX = points[originIndex].x-circleWidth/2;
	            circleY = points[originIndex].y-circleHeight/2;
	        }
	        repaint();
	    }
		
		public Animation() throws Exception {
			setBackground(Color.darkGray);
			//Setting points based on screen dimensions
			points[0] = new Point( screenSize.width/4,screenSize.height/4 );
	        points[1] = new Point( screenSize.width/4,screenSize.height/2 );
	        points[2] = new Point( screenSize.width/4,( 3*screenSize.height )/4 );
	        points[3] = new Point( screenSize.width/2,screenSize.height/4 );
	        points[4] = new Point( screenSize.width/2,screenSize.height/2 );
	        points[5] = new Point( screenSize.width/2,( 3*screenSize.height )/4 );
	        points[6] = new Point( ( 3*screenSize.width )/4,screenSize.height/4 );
	        points[7] = new Point( ( 3*screenSize.width )/4,screenSize.height/2 );
	        points[8] = new Point( ( 3*screenSize.width )/4,( 3*screenSize.height )/4 );
	        points[9] = new Point( -75,-75 );
	        
	        Random rand = new Random();
	        Point tmp;

	        for( int i=8;i>0;i-- ){
	            int j = rand.nextInt(i);
	            tmp = points[j];
	            points[j] = points[i];
	            points[i] = tmp;
	        }
	        
	        originIndex = 9;
	        destinationIndex = 0;
	        //setting sprite parameters.
	        circleWidth = 50;
	        circleHeight = 50;
	        circleX = points[originIndex].x-circleWidth/2;
	        circleY = points[originIndex].y-circleHeight/2;
	        dotWidth = 8;
	        dotHeight = 8;
	        dotX = points[originIndex].x-dotWidth/2;
	        dotY = points[originIndex].y-dotHeight/2;
	        //Setting animation variables
	        t = 0;
	        timer = new Timer(20,this);
	        timer.start();
	        startCalibration();
		}
	}
	
	protected abstract void startCalibration() throws Exception;
    protected abstract void stopCalibration() throws Exception;
    protected abstract void useCalibrationPoint(double x, double y)
            throws Exception;
    protected abstract void displayCalibrationStatus() throws Exception;
	
	
	public NewCalibrator() throws Exception {
		super();
		GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screen.setFullScreenWindow(this);
		add(new Animation());
		setVisible(true);
	}
}
