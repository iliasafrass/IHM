
import fr.lri.swingstates.canvas.CExtensionalTag;
import fr.lri.swingstates.canvas.CRectangle;
import fr.lri.swingstates.canvas.CStateMachine;
import fr.lri.swingstates.canvas.Canvas ;
import fr.lri.swingstates.canvas.CShape ;
import fr.lri.swingstates.canvas.CText ;
import fr.lri.swingstates.canvas.transitions.EnterOnShape;
import fr.lri.swingstates.canvas.transitions.LeaveOnShape;
import fr.lri.swingstates.canvas.transitions.PressOnShape;
import fr.lri.swingstates.canvas.transitions.ReleaseOnShape;
import fr.lri.swingstates.debug.StateMachineVisualization;
import fr.lri.swingstates.sm.State;
import fr.lri.swingstates.sm.StateMachine;
import fr.lri.swingstates.sm.Transition;
import fr.lri.swingstates.sm.transitions.Release;
import fr.lri.swingstates.sm.transitions.TimeOut;

import javax.swing.JFrame ;
import javax.swing.WindowConstants;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font ;
import java.awt.Paint;
import java.awt.event.WindowStateListener;



/**
 * @author AFRASS ILIAS
 * @author TAIBI ANAS
 *
 */

public class SimpleButton {

    private CText label;
    private CRectangle rectangle;
    private CExtensionalTag tag;

    SimpleButton(Canvas canvas, String text) {
    	rectangle = canvas.newRectangle(0, 0, 150, 50);
    	rectangle.setFillPaint(Color.WHITE);
    	tag = new CExtensionalTag(canvas) {};
    	rectangle.addTag(tag);
    	
    	CStateMachine stateMachine = initStateMachine();
    	StateMachineVisualization stateMachineVisualization = initStateMachineVisualization(stateMachine);
    	
    	JFrame frame = new JFrame();
    	frame.add(stateMachineVisualization);
    	frame.pack();
    	frame.setVisible(true);
    	frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    	
    	label = canvas.newText(0, 0, text, new Font("verdana", Font.PLAIN, 12));
    	label.addChild(rectangle);
    	label.above(rectangle);
    	
    	
    	stateMachine.attachTo(rectangle);
    }

    public void action() {
	   System.out.println("ACTION!");
    }

    public CShape getShape() {
	   return label;
    }
    
    public CStateMachine initStateMachine() {
    	CStateMachine sm = new CStateMachine() {
    		Paint initColor;
    		int clicks = 0, demiClicks = 0;
    		boolean clickFirst = false, doubleClickHappened = false;
    		
    		// stat start
    		public State start = new State() {
    			public void enter(){
    				clicks = 0;
    				demiClicks = 0;
    				clickFirst = false;
    				doubleClickHappened = false;
    				rectangle.setFillPaint(Color.white);
    				rectangle.setOutlinePaint(Color.BLACK);
    				rectangle.setStroke(new BasicStroke(1));
    				label.setText("start");
    			}
    			
    			Transition overButton = new EnterOnShape(">> over") {};
    		};
    		
    		// stat over
    		public State over = new State() {
    			
    			@Override
    			public void enter(){
					if (clicks == 1 && demiClicks == 1 && clickFirst) {	/* premier type d'action, 1 clic et un demi click */
						rectangle.setFillPaint(Color.magenta);
						clicks = 0;
						demiClicks = 0;
						label.setText("1 clic et demi");

					} else if(doubleClickHappened){						/* un double click */
						rectangle.setFillPaint(Color.GREEN);
						doubleClickHappened = false;
						clicks = 0;
						demiClicks = 0;
						label.setText("Double click !");
					} else if (clicks == 0 && demiClicks == 1){			/* un demi click */
						clicks = 0;
						demiClicks = 0;
						label.setText(" 1/2 click!");
					} else {											/* aucun event */
						if(clicks > 2)
							clicks = 0;
						if(demiClicks > 1)
							demiClicks = 0;
						rectangle.setFillPaint(Color.white);
						rectangle.setStroke(new BasicStroke(2));
						label.setText("over");
					}
    			}
    			
    			Transition leaveButton = new LeaveOnShape(">> start"){};
    			
    			Transition pressButton = new PressOnShape(">> pressed") {};
    		};
    		
    		
    		// stat pressed
    		public State pressed = new State() {
    			
    			@Override
    			public void enter(){
    				rectangle.setStroke(new BasicStroke(1));
					rectangle.setFillPaint(Color.YELLOW);
					label.setText("Press");
    				armTimer(400, false);
    			}
    			
    			Transition release = new ReleaseOnShape(">> waitingDoubleClick") {
    				public void action() {
    					if(!clickFirst && demiClicks == 0 && clicks == 0)
    						clickFirst = true;
    					clicks++;
    				}
    			};
    			
    			Transition sortDuBouton = new LeaveOnShape(">> hold") {
    				public void action() {
    					rectangle.setFillPaint(Color.WHITE);
    					rectangle.setStroke(new BasicStroke(1));
    				}
    			};
    			
    			Transition halfClick = new TimeOut(">> halfClick");
    				
    		};
    		
    		// stat hold
    		public State hold = new State() {
    			
    			@Override
    			public void enter(){
    				rectangle.setFillPaint(Color.lightGray);
    				rectangle.setStroke(new BasicStroke(1));
    			}
    			
    			Transition rentre = new EnterOnShape(">> pressed") {};
    			
    			Transition releaseOutside = new Release(">> start") {};
    		};
    		
    		//attente d'un double clic
    		public State waitingDoubleClick = new State(){
    			@Override
    			public void enter(){
    				armTimer(200, false);
    				label.setText("Waiting double click...");
    			}
    			
    			Transition doubleClick = new PressOnShape(">> doublePressed"){};
    			
    			Transition sortDuBouton = new LeaveOnShape(">> start") {};
    			
    			Transition timeOut = new TimeOut(">> over");
    		};
    	
    		//double clic
    		public State doublePressed = new State() {
    			
    			@Override
    			public void enter(){
    				armTimer(400, false);
    			}
    			
    			Transition oneAndHalf = new TimeOut(">> oneAndHalf");
    			
    			Transition holdDouble = new LeaveOnShape(">> holdDouble") {};
    			
    			Transition doubleClick = new Release(">> over"){
    				@Override
    				public void action(){
    					clicks++;
    					doubleClickHappened = true;
    				}
    			};
    		
    		};
    		
    
    		//un double clic qui se transforme en 1 clic et demi
    		public State oneAndHalf = new State() {
    			@Override
    			public void enter(){
    				rectangle.setFillPaint(Color.BLUE);
    				rectangle.setStroke(new BasicStroke(1));
					rectangle.setOutlinePaint(Color.black);
					demiClicks++;
    			}
    			
    			Transition release = new Release(">> over"){};
    			
    			Transition exit = new LeaveOnShape(">> holdOneAndHalf"){};
    		};
    		
    		//holding le clic et demi
    		public State holdOneAndHalf = new State() {
    			@Override
    			public void enter(){
    				rectangle.setFillPaint(Color.ORANGE);
    				rectangle.setStroke(new BasicStroke(1));
					rectangle.setOutlinePaint(Color.red);
    			}
    			
    			Transition release = new Release(">> start"){};
    			
    			Transition enterAgain = new EnterOnShape(">> oneAndHalf"){};
			};
    		
			//un demi click
    		public State halfClick = new State() {
    			@Override
    			public void enter(){
    				rectangle.setFillPaint(Color.red);
    				rectangle.setStroke(new BasicStroke(1));
					rectangle.setOutlinePaint(Color.black);
    			}
    			
    			Transition release = new ReleaseOnShape(">> over"){
    				public void action(){
    					demiClicks++;
    				}
    			};
    			
    			Transition exitShape = new LeaveOnShape(">> holdHalfClick"){};
			};
			
			//holding un demi clic
			public State holdHalfClick = new State(){
				@Override
				public void enter(){
					rectangle.setStroke(new BasicStroke(1));
					rectangle.setFillPaint(Color.CYAN);
					rectangle.setOutlinePaint(Color.black);
				}
				
				Transition enterAgain = new EnterOnShape(">> halfClick"){};
				
				Transition cancelAction = new Release(">> start"){};
			};
	
    	};
    	
    	
		return sm;
    }
    
    public StateMachineVisualization initStateMachineVisualization(CStateMachine sm) {
    	StateMachineVisualization smv = new StateMachineVisualization(sm);
    	
    	return smv;
    }

    static public void main(String[] args) {
	   JFrame frame = new JFrame();
	   Canvas canvas = new Canvas(400,400);
	   frame.getContentPane().add(canvas);
	   frame.pack();
	   frame.setVisible(true);
	   frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	   SimpleButton simple = new SimpleButton(canvas, "simple button");
	   simple.getShape().translateBy(100,100);
    }

}