package WindowInfo;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;

// see http://twall.github.io/jna/4.0/javadoc/

public class ActiveWindowInfo {
    private static final int MAX_TITLE_LENGTH = 1024;

    public static String getActiveWindowTitle() throws Exception {
       String res = "";
    	for(int i = 0; i < 10; i++) {
    		char[] buffer = new char[MAX_TITLE_LENGTH * 2];
    		HWND hwnd = User32.INSTANCE.GetForegroundWindow();
    		User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
    		res = Native.toString(buffer);
    	}
    	return res;
    }
    
    public static RECT getActiveWindowRectangle() throws Exception {
		HWND hwnd = User32.INSTANCE.GetForegroundWindow();
		RECT rect = new RECT();
		User32.INSTANCE.GetWindowRect(hwnd, rect);
		return rect;
    }
    
    public static void main(String[] args) {
    	try {
			System.out.println(getActiveWindowTitle());
			System.out.println(getActiveWindowRectangle().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
 }