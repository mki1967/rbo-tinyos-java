
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class TextOutputScroll extends JFrame{

    JTextArea textArea;
    JPanel panel;
    JScrollPane scrollPane;

    TextOutputScroll(String title)
    {

	setTitle(title);
	textArea= new JTextArea(20,40);
	textArea.setEditable(false);
	textArea.setLineWrap(true);
	panel=new JPanel();

	scrollPane=new JScrollPane(textArea);
	panel.add(scrollPane);
	add(panel);
	pack();

    }
	    
    StringBuffer output=new StringBuffer();

    void print(String s)
    {
	output.append(s);
	textArea.setText(output.toString());
	textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    void println(String s)
    {
	print(s+"\n");
    }

    public static void main(String[] args)
    {

	try{
	    TextOutputScroll myPrinter= new TextOutputScroll("TEST");
	    myPrinter.setVisible(true);

	    int in;
	    while( (in=System.in.read()) >= 0 )
		{
		    myPrinter.print(String.valueOf((char) in));
		}
	    System.exit(0);
	} 
	catch(Exception e)
	    {
		System.out.println(e);
	    }

    }
    
}
