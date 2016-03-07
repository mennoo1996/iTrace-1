package edu.ysu.itrace.trackers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import com.theeyetribe.client.GazeManager;
import com.theeyetribe.client.IGazeListener;
import com.theeyetribe.client.data.GazeData;

import edu.ysu.itrace.Calibrator;
import edu.ysu.itrace.Gaze;
import edu.ysu.itrace.exceptions.CalibrationException;

public class TheEyeTribeTracker implements IEyeTracker{
	
	public static String rawDataFile;
	
	
	public static void handleFile(String jsonFile) {
		System.out.println("HANDLING FILE");
		System.out.println("ARGUMENT = " + jsonFile);
		
		File file = new File(jsonFile);
		file = file.getParentFile();
		System.out.println("PARENTFILE = " + file.getAbsolutePath());
		rawDataFile = file.getAbsolutePath() + "/raw.txt";
	}
	
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
		PrintWriter writer;
		
		public void run() {
			try {
				System.out.println("TRY TO CREATE WRITER");
				System.out.println("RAW DATA FILE = " + rawDataFile);
				writer = new PrintWriter(new FileWriter(rawDataFile));
			} catch (Exception e) {
				System.out.println("IT DID NOT WORK");
			}
			System.out.println("IT WORKED");
			//System.out.println("STARTING RUN 125371");
			running = RunState.RUNNING;
			gm.activate(GazeManager.ApiVersion.VERSION_1_0, GazeManager.ClientMode.PUSH);
			
			final GazeListener gazeListener = new GazeListener();
			gm.addGazeListener(gazeListener);
			//System.out.println("93483933");
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				
//			}
		//String a = JSONGazeExportSolver.getRawDataFile();
////			while (!a.equals("")) {
//				a = JSONGazeExportSolver.getRawDataFile();
//			}
			//RawDataCollector rw = new RawDataCollector(a);
			//System.out.println("MESSAGE WITH ID 111000");
			
			//rw.start();
			//System.out.println("MESSAGE WITH ID 1010101");
			//System.out.println("GAZE DATA" + GazeListener.lastData);
			//System.out.println("934933331");
			while (running == RunState.RUNNING) {
//				while (GazeListener.lastData!=null) {
//					try {
//						Thread.sleep(25);
//					} catch (InterruptedException e) {
//						
//					}
//				}
		
				GazeData lastData = new GazeData(GazeListener.lastData);
			//	System.out.println(lastData.leftEye.smoothedCoordinates.x);
				//System.out.println(lastData.rightEye.smoothedCoordinates.x);
				double left_x = lastData.leftEye.smoothedCoordinates.x;
				double left_y = lastData.leftEye.smoothedCoordinates.y;
				double right_x = lastData.rightEye.smoothedCoordinates.x;
				double right_y = lastData.rightEye.smoothedCoordinates.y;
				double left_diameter = lastData.leftEye.pupilSize;
				double right_diameter = lastData.rightEye.pupilSize;
				
				Gaze gaze = new Gaze(left_x/1920, right_x/1920, left_y/1080, right_y/1080, 1.0, 1.0, left_diameter, right_diameter, new Date());
				parent.calibrator.moveCrosshair((int)lastData.smoothedCoordinates.x, (int)lastData.smoothedCoordinates.y);
				parent.gazePoints.add(gaze);
				
				Thread t = new Thread(() -> {
					System.out.println("IN RAW DATA THREAD");
					long time = System.currentTimeMillis();
					StringBuilder sb = new StringBuilder();
					sb.append("{\"category\":\"tracker\",\"request\":\"get\""
							+ ",\"statuscode\":200,\"values\":{\"frame\""
							+ ":{\"avg\":{\"x\":");
					sb.append(lastData.smoothedCoordinates.x);
					sb.append(",\"y\":");
					sb.append(lastData.smoothedCoordinates.y);
					sb.append("},\"fix\":");
					sb.append(lastData.isFixated);
					sb.append(",\"lefteye\":{\"avg\":{\"x\":");
					sb.append(lastData.leftEye.smoothedCoordinates.x);
					sb.append(",\"y\":");
					sb.append(lastData.leftEye.smoothedCoordinates.y);
					sb.append("},\"pcenter\":{\"x\":");
					sb.append(lastData.leftEye.pupilCenterCoordinates.x);
					sb.append(",\"y\":");
					sb.append(lastData.leftEye.pupilCenterCoordinates.y);
					sb.append("},\"psize\":");
					sb.append(lastData.leftEye.pupilSize);
					sb.append(",\"raw\":{\"x\":");
					sb.append(lastData.leftEye.rawCoordinates.x);
					sb.append(",\"y\":");
					sb.append(lastData.leftEye.rawCoordinates.y);
					sb.append("}},\"raw\":{\"x\":");
					sb.append(lastData.rawCoordinates.x);
					sb.append(",\"y\":");
					sb.append(lastData.rawCoordinates.y);
					sb.append("},\"righteye\":{\"avg\":{\"x\":");
					sb.append(lastData.rightEye.smoothedCoordinates.x);
					sb.append(",\"y\":");
					sb.append(lastData.rightEye.smoothedCoordinates.y);
					sb.append("},\"pcenter\":{\"x\":");
					sb.append(lastData.rightEye.pupilCenterCoordinates.x);
					sb.append(",\"y\":");
					sb.append(lastData.rightEye.pupilCenterCoordinates.y);
					sb.append("},\"psize\":");
					sb.append(lastData.rightEye.pupilSize);
					sb.append(",\"raw\":{\"x\":");
					sb.append(lastData.rightEye.rawCoordinates.x);
					sb.append(",\"y\":");
					sb.append(lastData.rightEye.rawCoordinates.y);
					sb.append("}},\"state\":7,\"time\":");
					sb.append(lastData.timeStamp);
					sb.append(",\"timestamp\":\"");
					sb.append(lastData.timeStampString);
					sb.append("\", \"millitime\":");
					sb.append(time);
					sb.append("}}}");
					
					writer.println(sb.toString());
					writer.flush();
					
					
					
				});
				t.start();
				
				try {
					Thread.sleep(25);
					
				} catch (InterruptedException e) {
					
				}
				
			}
			//rw.done=true;
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
		//System.out.println("GET GAZE CALLED");
		
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
	//	System.out.println("UPDATE GAZE" + arg0);
		lastData = arg0;
		
		
		
		
	}
	
}
