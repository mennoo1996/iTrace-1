package edu.ysu.itrace.gaze.handlers;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;

import com.sun.jna.platform.win32.WinDef.RECT;

import WindowInfo.ActiveWindowInfo;
import edu.ysu.itrace.AstManager;
import edu.ysu.itrace.AstManager.SourceCodeEntity;
import edu.ysu.itrace.ControlView;
import edu.ysu.itrace.Gaze;
import edu.ysu.itrace.gaze.IGazeHandler;
import edu.ysu.itrace.gaze.IStyledTextGazeResponse;

/**
 * Implements the gaze handler interface for a StyledText widget.
 */
public class StyledTextGazeHandler implements IGazeHandler {
    private StyledText targetStyledText;

    /**
     * Constructs a new gaze handler for the target StyledText object
     */
    public StyledTextGazeHandler(Object target) {
        this.targetStyledText = (StyledText) target;
    }

    @Override
    public IStyledTextGazeResponse handleGaze(int absoluteX, int absoluteY,
            int relativeX, int relativeY, final Gaze gaze) {
    	System.out.println("handling styled text gaze");
        final int lineIndex;
        final int col;
        final Point absoluteLineAnchorPosition;
        final String name;
        final int lineHeight;
        final int fontHeight;
        final AstManager.SourceCodeEntity[] entities;
        final String path;
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

        try {
            if (targetStyledText.getData(ControlView.KEY_AST) == null)
            		return null;
            AstManager astManager = (AstManager) targetStyledText
            		.getData(ControlView.KEY_AST);
            lineIndex = targetStyledText.getLineIndex(relativeY);
            int lineOffset = targetStyledText.getOffsetAtLine(lineIndex);
            int offset = targetStyledText.getOffsetAtLocation(new Point(
                    relativeX, relativeY));
            col = offset - lineOffset;

            // (0, 0) relative to the control in absolute screen
            // coordinates.
            Point relativeRoot = new Point(absoluteX - relativeX, absoluteY
                    - relativeY);
            // Top-left position of the first character on the line in
            // relative coordinates.
            Point lineAnchorPosition = targetStyledText
                    .getLocationAtOffset(targetStyledText
                            .getOffsetAtLine(lineIndex));
            // To absolute.
            absoluteLineAnchorPosition = new Point(lineAnchorPosition.x
                    + relativeRoot.x, lineAnchorPosition.y + relativeRoot.y);

            lineHeight = targetStyledText.getLineHeight();
            fontHeight = targetStyledText.getFont().getFontData()[0]
                    .getHeight();
            entities = astManager.getSCEs(lineIndex + 1, col);
            path = astManager.getPath();
            int splitLength = path.split("\\\\").length;
            name = path.split("\\\\")[splitLength-1];
        } catch (IllegalArgumentException e) {
            /* An IllegalArgumentException SHOULD mean that the gaze fell
             * outside the valid text area, so just drop this one.
             */
        	System.out.println("got exception shitty");
            return null;
        }

        /*
         * This anonymous class just grabs the variables marked final
         * in the enclosing method and returns them.
         */
        return new IStyledTextGazeResponse() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getGazeType() {
                return "text";
            }

            @Override
            public int getLineHeight() {
                return lineHeight;
            }

            @Override
            public int getFontHeight() {
                return fontHeight;
            }

            @Override
            public Gaze getGaze() {
                return gaze;
            }

            public IGazeHandler getGazeHandler() {
                return StyledTextGazeHandler.this;
            }

            @Override
            public int getLine() {
                return lineIndex + 1;
            }

            @Override
            public int getCol() {
                return col;
            }

            // Write out the position at the top-left of the first
            // character in absolute screen coordinates.
            @Override
            public int getLineBaseX() {
                return absoluteLineAnchorPosition.x;
            }

            @Override
            public int getLineBaseY() {
                return absoluteLineAnchorPosition.y;
            }

            @Override
            public SourceCodeEntity[] getSCEs() {
                return entities;
            }

            @Override
            public String getPath() {
                return path;
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
