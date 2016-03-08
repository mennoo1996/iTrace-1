package edu.ysu.itrace.gaze.handlers;

import com.sun.jna.platform.win32.WinDef.RECT;

import WindowInfo.ActiveWindowInfo;
import edu.ysu.itrace.Gaze;
import edu.ysu.itrace.gaze.IGazeHandler;
import edu.ysu.itrace.gaze.RootGazeResponse;

/**
 * Implements the gaze handler interface for a StyledText widget.
 */
public class RootGazeHandler implements IGazeHandler {

    /**
     * Constructs a new gaze handler for the target StyledText object
     */
    public RootGazeHandler(Object target) {
    }

    @Override
    public RootGazeResponse handleGaze(int absoluteX, int absoluteY,
            int relativeX, int relativeY, final Gaze gaze) {
    	
    	System.out.println("Handling raw gaze");
    	String activeWindowTitle;
		try {
			activeWindowTitle =  ActiveWindowInfo.getActiveWindowTitle();
		} catch (Exception e) {
			activeWindowTitle = "null";
		}
		final String finalActiveWindowTitle = activeWindowTitle;
		
		RECT activeWindowRect;
		try {
			activeWindowRect =  ActiveWindowInfo.getActiveWindowRectangle();
		} catch (Exception e) {
			activeWindowRect = null;
		}
		final RECT finalActiveWindowRect = activeWindowRect;
		
        /*
         * This anonymous class just grabs the variables marked final
         * in the enclosing method and returns them.
         */
        return new RootGazeResponse() {
            @Override
            public Gaze getGaze() {
                return gaze;
            }

            public IGazeHandler getGazeHandler() {
                return RootGazeHandler.this;
            }

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "Raw Point";
			}

			@Override
			public String getGazeType() {
				// TODO Auto-generated method stub
				return "raw";
			}

			@Override
			public String getActiveWindowTitle() {
				// TODO Auto-generated method stub
				return finalActiveWindowTitle;
			}

			@Override
			public RECT getActiveWindowRECT() {
				// TODO Auto-generated method stub
				return finalActiveWindowRect;
			}
			
			
        };
    }
}
