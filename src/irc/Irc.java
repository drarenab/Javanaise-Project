/***
 * Irc class : simple implementation of a chat using JAVANAISE
 * Contact: 
 *
 * Authors: 
 */

package irc;

import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jvn.JvnException;
import jvn.JvnObject;
import jvn.JvnServerImpl;


public class Irc {
	public TextArea		text;
	public TextField	data;
	Frame 			frame;
	JvnObject       sentence;


  /**
  * main method
  * create a JVN object nammed IRC for representing the Chat application
  **/
	public static void main(String argv[]) {
	   System.out.println("Irc main called");

		// initialize JVN
		JvnServerImpl js = JvnServerImpl.jvnGetServer();
		
		// look up the IRC object in the JVN server
		// if not found, create it, and register it in the JVN server
        JvnObject jo = null;
        try {
            jo = js.jvnLookupObject("IRC");

        if (jo == null) {
			jo = js.jvnCreateObject(new Sentence());
			// after creation, I have a write lock on the object
			jo.jvnUnLock();
			js.jvnRegisterObject("IRC", jo);

			System.out.println("IRC object null, new one has been created");
		}
        
        jo.jvnSetToNoLock();
		// create the graphical part of the Chat application

            System.out.println("Object Id : "+jo.jvnGetObjectId());
		 new Irc(jo);

        } catch (JvnException e) {
            e.printStackTrace();
        }

	}

  /**
   * IRC Constructor
   @param jo the JVN object representing the Chat
   **/
	public Irc(JvnObject jo) {
        int objectUniqueId = System.identityHashCode(jo) ;
        sentence = jo;
		frame=new Frame();
		frame.setLayout(new GridLayout(1,1));
		text=new TextArea(10,60);
		text.setEditable(false);
		text.setForeground(Color.red);
		frame.add(text);
		data=new TextField(40);
		frame.add(data);

		Button read_button = new Button("read");
		read_button.addActionListener(new readListener(this));

		frame.add(read_button);

		Button write_button = new Button("write");
		write_button.addActionListener(new writeListener(this));

		frame.add(write_button);
		Button unlock_button = new Button("unLock");
		unlock_button.addActionListener(new unLockListener(this));

		frame.add(unlock_button);

		frame.setSize(545,201);
		text.setBackground(Color.black); 
		frame.setVisible(true);
	}
}


 /**
  * Internal class to manage user events (read) on the CHAT application
  **/
 class readListener implements ActionListener {
	Irc irc;
  
	public readListener (Irc i) {
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
 class writeListener implements ActionListener {
	Irc irc;
  
	public writeListener (Irc i) {
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
 class unLockListener implements ActionListener {
		Irc irc;
	  
		public unLockListener (Irc i) {
			irc = i;
		}
	   
	 /**
	  * Management of user events
	  **/
		public void actionPerformed (ActionEvent e) {
		 try {
			// lock the object in read mode
			irc.sentence.jvnSetToNoLock();

		   } catch (JvnException je) {
			   System.out.println("IRC problem : " + je.getMessage());
		   }
		}
	}



