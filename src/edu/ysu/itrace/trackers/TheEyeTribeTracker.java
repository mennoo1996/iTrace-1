package edu.ysu.itrace.trackers;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import com.theeyetribe.client.GazeManager;
import com.theeyetribe.client.IGazeListener;
import com.theeyetribe.client.data.GazeData;

import edu.ysu.itrace.Calibrator;
import edu.ysu.itrace.Gaze;
import edu.ysu.itrace.exceptions.CalibrationException;
import edu.ysu.itrace.solvers.JSONGazeExportSolver;

public class TheEyeTribeTracker implements IEyeTracker{
	private static class TrackerThread extends Thread {
		private enum RunState {
			RUNNING,
			STOPPING,
			STOPPED
		}
		
		private TheEyeTribeTracker parent = null;
		private volatile RunState running = RunState.STOPPED;
		final GazeManager gm = GazeManager.getInstance();
		
		public TrackerThread(TheEyeTribeTracker parent) {
			this.parent = parent;
		}
		
		public void startTracking() {
			start();
		}
		
		public void stopTracking() {
			if (running != RunState.RUNNING)
				return;
			running = RunState.STOPPING;
			while (running != RunState.STOPPED) {
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
					
				}
			}
		}
		
		public void run() {
			System.out.println("STARTING RUN 125371");
			running = RunState.RUNNING;
			gm.activate(GazeManager.ApiVersion.VERSION_1_0, GazeManager.ClientMode.PUSH);
			
			final GazeListener gazeListener = new GazeListener();
			gm.addGazeListener(gazeListener);
			System.out.println("93483933");
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				
//			}
//		String a = JSONGazeExportSolver.getRawDataFile();
////			while (!a.equals("")) {
//				a = JSONGazeExportSolver.getRawDataFile();
//			}
//			RawDataCollector rw = new RawDataCollector(a);
			System.out.println("MESSAGE WITH ID 111000");
			
//			rw.start();
			System.out.println("MESSAGE WITH ID 1010101");
			System.out.println("GAZE DATA" + GazeListener.lastData);
			System.out.println("934933331");
			while (running == RunState.RUNNING) {
//				while (GazeListener.lastData!=null) {
//					try {
//						Thread.sleep(25);
//					} catch (InterruptedException e) {
//						
//					}
//				}
		
				GazeData lastData = new GazeData(GazeListener.lastData);
				System.out.println(lastData.leftEye.smoothedCoordinates.x);
				System.out.println(lastData.rightEye.smoothedCoordinates.x);
				double left_x = lastData.leftEye.smoothedCoordinates.x;
				double left_y = lastData.leftEye.smoothedCoordinates.y;
				double right_x = lastData.rightEye.smoothedCoordinates.x;
				double right_y = lastData.rightEye.smoothedCoordinates.y;
				double left_diameter = lastData.leftEye.pupilSize;
				double right_diameter = lastData.rightEye.pupilSize;
				
				Gaze gaze = new Gaze(left_x/1920, right_x/1920, left_y/1080, right_y/1080, 1.0, 1.0, left_diameter, right_diameter, new Date());
				parent.calibrator.moveCrosshair((int)lastData.smoothedCoordinates.x, (int)lastData.smoothedCoordinates.y);
				parent.gazePoints.add(gaze);
				try {
					Thread.sleep(25);
					
				} catch (InterruptedException e) {
					
				}
				
			}
//			rw.done=true;
			running = RunState.STOPPED;
			
		}
	}
	
	private class EyeTribeCalibrator extends Calibrator {
		public EyeTribeCalibrator() throws IOException {
			super();
		}

		@Override
		protected void startCalibration() throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void stopCalibration() throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void useCalibrationPoint(double x, double y) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void displayCalibrationStatus() throws Exception {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private LinkedBlockingQueue<Gaze> gazePoints = new LinkedBlockingQueue<Gaze>();
	private EyeTribeCalibrator calibrator;
	private TrackerThread trackerThread = null;
	
	public TheEyeTribeTracker() throws IOException {
		calibrator = new EyeTribeCalibrator();
	}

	@Override
	public void close() {
		try {
			stopTracking();
		} catch (IOException e) {
			
		}
		
	}

	@Override
	public void clear() {
		gazePoints = new LinkedBlockingQueue<Gaze>();
		
	}

	@Override
	public void calibrate() throws CalibrationException {
		calibrator.calibrate();
		try {
			calibrator.displayCalibrationStatus();
		} catch (Exception e) {
			throw new CalibrationException("Cannot display calibration status");
		}
		
	}

	@Override
	public void startTracking() throws IOException {
		if (trackerThread!=null) {
			return;
		}
		
		trackerThread = new TrackerThread(this);
		trackerThread.startTracking();
		
	}

	@Override
	public void stopTracking() throws IOException {
		if (trackerThread == null) {
			return;
		}
		trackerThread.stopTracking();
		trackerThread = null;
		
	}

	@Override
	public Gaze getGaze() {
		System.out.println("GET GAZE CALLED");
		
		return gazePoints.poll();
	}

	@Override
	public void displayCrosshair(boolean enabled) {
		calibrator.displayCrosshair(enabled);
		
	}

	@Override
	public void setXDrift(int drift) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setYDrift(int drift) {
		// TODO Auto-generated method stub
		
	}

}

class GazeListener implements IGazeListener {
	public static GazeData lastData = new GazeData();

	@Override
	public void onGazeUpdate(GazeData arg0) {
		System.out.println("UPDATE GAZE" + arg0);
		lastData = arg0;
		
		
		
		
	}
	
}
