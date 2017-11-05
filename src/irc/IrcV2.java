package irc;

import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import jvn.JvnException;
import jvn.JvnObject;
import jvn.JvnServerImpl;

/**
 * Version 2 de la class IrcV2 
 * @author karim
 *
 */
public class IrcV2 {
	public TextArea		text;
	public TextField	data;
	Frame 			frame;
	SentenceInterface      sentence;


  /**
  * main method
  * create a JVN object nammed IRC for representing the Chat application
  **/
	public static void main(String argv[]) {
	   try {
		   
		// initialize JVN
		JvnServerImpl js = JvnServerImpl.jvnGetServer();
		
//		// look up the IRC object in the JVN server
//		// if not found, create it, and register it in the JVN server
//		JvnObject jo = js.jvnLookupObject("IRC");
//		   
//		if (jo == null) {
//			jo = js.jvnCreateObject((Serializable) new Sentence());
//			// after creation, I have a write lock on the object
//			jo.jvnUnLock();
//			js.jvnRegisterObject("IRC", jo);
//		}
		
		SentenceInterface s = null;//= use proxy 
		// create the graphical part of the Chat application
		 new IrcV2(s);
	   
	   } catch (Exception e) {
		   System.out.println("IRC problem : " + e.getMessage());
	   }
	}

  /**
   * IRC Constructor
   @param jo the JVN object representing the Chat
   **/
	public IrcV2(SentenceInterface s) {
		sentence = s;
		frame=new Frame();
		frame.setLayout(new GridLayout(1,1));
		text=new TextArea(10,60);
		text.setEditable(false);
		text.setForeground(Color.red);
		frame.add(text);
		data=new TextField(40);
		frame.add(data);
		Button read_button = new Button("read");
		read_button.addActionListener(new readListener2(this));
		frame.add(read_button);
		Button write_button = new Button("write");
		write_button.addActionListener(new writeListener2(this));
		frame.add(write_button);
		Button unlock_button = new Button("unLock");
		unlock_button.addActionListener(new unLockListener2(this));

		frame.add(unlock_button);

		frame.setSize(545,201);
		text.setBackground(Color.black); 
		frame.setVisible(true);
	}
}


 /**
  * Internal class to manage user events (read) on the CHAT application
  **/
 class readListener2 implements ActionListener {
	IrcV2 irc;
  
	public readListener2 (IrcV2 i) {
		irc = i;
	}
   
 /**
  * Management of user events
  **/
	public void actionPerformed (ActionEvent e) {
	 try {
		// lock the object in read mode
		irc.sentence.jvnLockRead();
		
		// invoke the method
		String s = ((Sentence)(irc.sentence.jvnGetObjectState())).read();
		
		// unlock the object
		irc.sentence.jvnUnLock();
		
		// display the read value
		irc.data.setText(s);
		irc.text.append(s+"\n");
	   } catch (JvnException je) {
		   System.out.println("IRC problem : " + je.getMessage());
	   }
	}
}

 /**
  * Internal class to manage user events (write) on the CHAT application
  **/
 class writeListener2 implements ActionListener {
	IrcV2 irc;
  
	public writeListener2 (IrcV2 i) {
        	irc = i;
	}
  
  /**
    * Management of user events
   **/
	public void actionPerformed (ActionEvent e) {
	   try {	
		// get the value to be written from the buffer
    String s = irc.data.getText();
        	
    // lock the object in write mode
		irc.sentence.jvnLockWrite();
		
		// invoke the method
		((Sentence)(irc.sentence.jvnGetObjectState())).write(s);
		
//		 unlock the object
		irc.sentence.jvnUnLock();
	 } catch (JvnException je) {
		   System.out.println("IRC problem  : " + je.getMessage());
	 }
	}
}
 class unLockListener2 implements ActionListener {
		IrcV2 irc;
	  
		public unLockListener2 (IrcV2 i) {
			irc = i;
		}
	   
	 /**
	  * Management of user events
	  **/
		public void actionPerformed (ActionEvent e) {
		 try {
			// lock the object in read mode
			irc.sentence.jvnUnLock();
			
			// invoke the method
			String s = ((Sentence)(irc.sentence.jvnGetObjectState())).read();
			
			// unlock the object
			irc.sentence.jvnUnLock();
			
			// display the read value
			irc.data.setText(s);
			irc.text.append(s+"\n");
		   } catch (JvnException je) {
			   System.out.println("IRC problem : " + je.getMessage());
		   }
		}
	}





