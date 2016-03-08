package edu.ysu.itrace.gaze;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Shell;

import edu.ysu.itrace.gaze.handlers.RootGazeHandler;
import edu.ysu.itrace.gaze.handlers.StyledTextGazeHandler;

/**
 * Creates IGazeHandlers from objects within the Workbench.
 */
public class GazeHandlerFactory {

    /**
     * Creates and returns a new IGazeHandler object from the specified object,
     * or returns null if no handler object is defined for that object.
     */
    public static IGazeHandler createHandler(Object target) {
        // create gaze handler for a StyledText widget
        if (target instanceof StyledText) {
        	System.out.println("returning styled text handler");
            return new StyledTextGazeHandler(target);
        }

        return null;
    }
    
    public static IGazeHandler creatRootHandler(Object target) {
    	if(target instanceof Shell) {
    		System.out.println("returning shell handler");
    		return new RootGazeHandler(target);
    	}
    	
    	return null;
    }
}
