/***
 * Sentence class : used for representing the text exchanged between users
 * during a chat application
 * Contact: 
 *
 * Authors: 
 */

package irc;

public class Sentence implements java.io.Serializable, SentenceInterface {

	private static final long serialVersionUID = 1L;
	String 		data;
  
	public Sentence() {
		data = "";
	}
	
	@Override
	public void write(String text) {
		data = text;
	}
	
	@Override
	public String read() {
		return data;	
	}
	
	@Override
	public void setToUnlock() {
		return;
	}
}