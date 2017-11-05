package irc;

import annotation.Read;
import annotation.Write;
/**
 * Sentence interface containing two functions annotated with our new annotation
 * @author karim
 *
 */
public interface SentenceInterface {

	@Write
	public void write(String text) ;
	@Read
	public String read() ;
}
