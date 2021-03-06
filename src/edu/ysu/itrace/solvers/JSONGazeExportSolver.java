package edu.ysu.itrace.solvers;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.eclipse.core.resources.ResourcesPlugin;

import com.google.gson.stream.JsonWriter;

import edu.ysu.itrace.AstManager.SourceCodeEntity;
import edu.ysu.itrace.gaze.IGazeResponse;
import edu.ysu.itrace.gaze.IStyledTextGazeResponse;

/**
 * Solver that simply dumps gaze data to disk in JSON format.
 */
public class JSONGazeExportSolver implements IFileExportSolver {
    private JsonWriter responseWriter;
    private File outFile;
    private String filename = "gaze-responses-USERNAME"
    		+ "-yyMMddTHHmmss-SSSS-Z.json";
    private Dimension screenRect;
    private String sessionID;

    public JSONGazeExportSolver() {
    	UIManager.put("swing.boldMetal", new Boolean(false)); //make UI font plain
    }

    @Override
    public void init() {
        screenRect = Toolkit.getDefaultToolkit().getScreenSize();
        try {
            outFile = new File(getFilename());

            // Check that file does not already exist. If it does, do not begin
            // tracking.
            if (outFile.exists()) {
                System.out.println(friendlyName());
                System.out.println("You cannot overwrite this file. If you "
                        + "wish to continue, delete the file " + "manually.");

                return;
            }

            responseWriter = new JsonWriter(new FileWriter(outFile));

            //responseWriter.setIndent("");
            // to pretty print, use this one instead
            responseWriter.setIndent("  ");
        } catch (IOException e) {
            throw new RuntimeException("Log files could not be created: "
                    + e.getMessage());
        }
        System.out.println("Putting files at " + outFile.getAbsolutePath());

        try {
            responseWriter.beginObject()
                          .name("environment")
                          .beginObject()
                              .name("screen_size")
                              .beginObject()
                                  .name("width")
                                  .value(screenRect.width)
                                  .name("height")
                                  .value(screenRect.height)
                              .endObject()
                          .endObject()
                          .name("gazes")
                          .beginArray();
        } catch (IOException e) {
            throw new RuntimeException("Log file header could not be written: "
                    + e.getMessage());
        }
    }

    @Override
    public void process(IGazeResponse response) {
        try {
                int screenX =
                        (int) (screenRect.width * response.getGaze().getX());
                int screenY =
                        (int) (screenRect.height * response.getGaze().getY());

                responseWriter.beginObject()
                              .name("name")
                              .value(response.getName())
                              .name("type")
                              .value(response.getGazeType())
                              .name("x")
                              .value(screenX)
                              .name("y")
                              .value(screenY)
                              .name("left_validation")
                              .value(response.getGaze().getLeftValidity())
                              .name("right_validation")
                              .value(response.getGaze().getRightValidity())
                              .name("left_pupil_diameter")
                              .value(response.getGaze().getLeftPupilDiameter())
                              .name("right_pupil_diameter")
                              .value(response.getGaze().getRightPupilDiameter())
                              .name("tracker_time")
                              .value(response.getGaze().getTrackerTime().getTime())
                              .name("system_time")
                              .value(response.getGaze().getSystemTime())
                              .name("nano_time")
                              .value(response.getGaze().getNanoTime())
                              .name("active_window_title")
                              .value(response.getActiveWindowTitle())
                              .name("active_window_rect")
                              .beginObject()
                              .name("left")
                              .value(response.getActiveWindowRECT().left)
                              .name("right")
                              .value(response.getActiveWindowRECT().right)
                              .name("top")
                              .value(response.getActiveWindowRECT().top)
                              .name("bottom")
                              .value(response.getActiveWindowRECT().bottom)
                              .endObject();
                try {
                	responseWriter
                	.name("lefteye")
                	.beginObject()
                	.name("x")
                	.value((int) (screenRect.width * response.getGaze().getLeftX()))
                	.name("y")
                	.value((int) (screenRect.width * response.getGaze().getLeftY()))
                	.name("valid")
                	.value(response.getGaze().getLeftValidity())
                	.name("pupildiameter")
                	.value(response.getGaze().getLeftPupilDiameter())
                	.endObject()
                	.name("righteye")
                	.beginObject()
                	.name("x")
                	.value((int) (screenRect.width * response.getGaze().getRightX()))
                	.name("y")
                	.value((int) (screenRect.width * response.getGaze().getRightY()))
                	.name("valid")
                	.value(response.getGaze().getRightValidity())
                	.name("pupildiameter")
                	.value(response.getGaze().getRightPupilDiameter())
                	.endObject();
                    IStyledTextGazeResponse styledResponse =
                            (IStyledTextGazeResponse) response;
                    responseWriter.name("path")
                                  .value(styledResponse.getPath())
                                  .name("line_height")
                                  .value(styledResponse.getLineHeight())
                                  .name("font_height")
                                  .value(styledResponse.getFontHeight())
                                  .name("line")
                                  .value(styledResponse.getLine())
                                  .name("col")
                                  .value(styledResponse.getCol())
                                  .name("line_base_x")
                                  .value(styledResponse.getLineBaseX())
                                  .name("line_base_y")
                                  .value(styledResponse.getLineBaseY())
                                  .name("sces")
                                  .beginArray();
                    for (SourceCodeEntity sce : styledResponse.getSCEs()) {
                        responseWriter.beginObject()
                                      .name("name")
                                      .value(sce.name)
                                      .name("type")
                                      .value(sce.type.toString())
                                      .name("how")
                                      .value(sce.how.toString())
                                      .name("total_length")
                                      .value(sce.totalLength)
                                      .name("start_line")
                                      .value(sce.startLine)
                                      .name("end_line")
                                      .value(sce.endLine)
                                      .name("start_col")
                                      .value(sce.startCol)
                                      .name("end_col")
                                      .value(sce.endCol)
                                      .endObject();
                    }
                    responseWriter.endArray();
                    
                    

                } catch (ClassCastException e) {
                    // not styled text, oh well
                }
                responseWriter.endObject();
        } catch (IOException e) {
            // ignore write errors
        }
    }

    @Override
    public void dispose() {
        try {
            responseWriter.endArray()
                          .endObject();
            responseWriter.flush();
            responseWriter.close();
            System.out.println("Gaze responses saved.");
        } catch (IOException e) {
            throw new RuntimeException("Log file footer could not be written: "
                    + e.getMessage());
        }
    }

    @Override
    public void config(String sessionID, String devUsername) {
    	filename = "gaze-responses-" + devUsername +
    			"-" + sessionID + ".json";
    	this.sessionID = sessionID;
    }
    
    @Override
    public String getFilename() {
        String workspaceLocation =
                ResourcesPlugin.getWorkspace().getRoot().getLocation()
                        .toString();
        return workspaceLocation + "/" + sessionID + "/" + filename;
    }

    @Override
    public String friendlyName() {
        return "JSON Gaze Export";
    }

    @Override
    public void displayExportFile() {
    	JTextField displayVal = new JTextField(filename);
    	displayVal.setEditable(false);
    	
    	JPanel displayPanel = new JPanel();
    	displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS)); //vertically align
    	displayPanel.add(new JLabel("Export Filename"));
    	displayPanel.add(displayVal);
    	displayPanel.setPreferredSize(new Dimension(400,40)); //resize appropriately
    	
    	final int displayDialog = JOptionPane.showConfirmDialog(null, displayPanel, 
    			friendlyName() + " Display", JOptionPane.OK_CANCEL_OPTION,
    			JOptionPane.PLAIN_MESSAGE);
    	if (displayDialog == JOptionPane.OK_OPTION) {
    		//do nothing
    	}
    }
}
