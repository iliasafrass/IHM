
import fr.lri.swingstates.canvas.Canvas ;
import fr.lri.swingstates.canvas.transitions.DragOnTag;
import fr.lri.swingstates.canvas.transitions.PressOnTag;
import fr.lri.swingstates.canvas.transitions.ReleaseOnTag;
import fr.lri.swingstates.canvas.CShape ;
import fr.lri.swingstates.canvas.CRectangle ;
import fr.lri.swingstates.canvas.CSegment ;
import fr.lri.swingstates.canvas.CTag ;
import fr.lri.swingstates.canvas.CExtensionalTag ;
import fr.lri.swingstates.canvas.CStateMachine ;
import fr.lri.swingstates.sm.State;
import fr.lri.swingstates.sm.Transition;
import fr.lri.swingstates.sm.transitions.* ;

import java.awt.Color ;
import java.awt.BasicStroke ;
import java.awt.geom.Point2D ;
import javax.swing.JFrame ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList ;

/**
 * @author AFRASS ILIAS
 * @author TAIBI ANAS
 *
 */
public class MagneticGuides extends JFrame {

    private Canvas canvas;
    private CExtensionalTag oTag ;
    private ArrayList<MagneticGuide> allMagH;
    private ArrayList<MagneticGuide> allMagV;
    private HashMap<CShape,MagneticGuide> horizontalMap;
    private HashMap<CShape,MagneticGuide> verticalMap;
    
    public MagneticGuides(String title, int width, int height) {
	   super(title);
	   canvas = new Canvas(width, height);
	   canvas.setAntialiased(true);
	   getContentPane().add(canvas);
	   allMagH = new ArrayList<MagneticGuide>();
	   allMagV = new ArrayList<MagneticGuide>();
	   horizontalMap = new HashMap<CShape,MagneticGuide>();
	   verticalMap = new HashMap<CShape,MagneticGuide>();
	   oTag = new CExtensionalTag(canvas) {} ;
	   
	   CStateMachine sm = new CStateMachine() {

			 private Point2D p;
			 private CShape draggedShape;
			 private MagneticGuide mSelect;
			 private int cpt = 1;
			 
			 public MagneticGuide getHGuide(CShape shape){
			    	for (int k = 0; k < allMagH.size() ; k++){
			    		if (shape.getCenterY() == allMagH.get(k).getCenterY()){
			    			return allMagH.get(k);
			    		}
			    	}
			    	return null;
			 }
			 
			 public MagneticGuide getVGuide(CShape shape){
			    	for (int k = 0; k < allMagV.size() ; k++){
			    		if (shape.getCenterX() == allMagV.get(k).getCenterX()){
			    			return allMagV.get(k);
			    		}
			    	}
			    	return null;
			 }
			 
			 public State start = new State() {
				 	
				 	
				 
				 	Transition pressOnObject = new PressOnTag(oTag, BUTTON1, ">> oDrag") {
							  public void action() {
								 draggedShape = getShape() ;
								 p = getPoint();
								 
							  }
						   } ;
					
					Transition cacherAfficher = new Press(BUTTON2, ">> hide"){
						public void action(){
							for(MagneticGuide mag : allMagH){
								mag.getSeg().setTransparencyOutline(0);
							}
							for(MagneticGuide mag : allMagV){
								mag.getSeg().setTransparencyOutline(0);
							}
						}
					};
					
					
					Transition supprimer = new PressOnTag(MagneticGuide.class , BUTTON2){
						public void action(){
							MagneticGuide guideH = getHGuide(getShape());
							MagneticGuide guideV = getVGuide(getShape());
							if(guideH != null){
								for(CShape key : horizontalMap.keySet()){
									if(horizontalMap.get(key).getNb() == guideH.getNb()){
										horizontalMap.remove(key);
									}
								}
								allMagH.remove(guideH);
								guideH.getSeg().remove();
							}
							if(guideV != null){
								for(CShape key : verticalMap.keySet()){
									if(verticalMap.get(key).getNb() == guideV.getNb()){
										verticalMap.remove(key);
									}
								}
								allMagV.remove(guideV);
								guideV.getSeg().remove();
							}
						}
					};
						   
					Transition createSegH = new Press(BUTTON1){
						public void action(){
							MagneticGuide newMag = new MagneticGuide(canvas, getPoint(), cpt, "horizontal");
							cpt++;
							allMagH.add(newMag);
							
						}
					};
					
					Transition createSegV = new Press(BUTTON3){
						public void action(){
							MagneticGuide newMag = new MagneticGuide(canvas, getPoint(), cpt, "vertical");
							cpt++;
							allMagV.add(newMag);
							
						}
					};
						   
					Transition pressOnSeg = new PressOnTag(MagneticGuide.class , BUTTON1, ">> dragSeg"){
						public void action (){
							p = getPoint() ;
							draggedShape = getShape();
							MagneticGuide guideH = getHGuide(draggedShape);
							MagneticGuide guideV = getVGuide(draggedShape);
							if(guideH != null){
								mSelect = guideH;
							}
							else if(guideV != null){
								mSelect = guideV;
							}
							else{
								mSelect = null;
							}
						}
					};
				} ;

			 public State oDrag = new State() {
				 	Transition drag = new Drag(BUTTON1) {
						  public void action() {
							 Point2D q = getPoint() ;
							 draggedShape.translateBy(q.getX() - p.getX(), q.getY() - p.getY()) ;
							 p = q ;
							 
						  }
					   } ;
					
				    Transition release = new Release(BUTTON1, ">> start") {
				    		public void action(){
				    			
			    				for (int k = 0; k < allMagH.size() ; k++){
			    					int intersect = -1;
			    					int pointY = -1;
			    					int pointX = (int) draggedShape.getCenterX();
				    				for (int j = (int) draggedShape.getMinY(); j <= (int) draggedShape.getMaxY() ; j++){
				    					if(j <= (int) allMagH.get(k).getSeg().getMaxY() && j >= (int) allMagH.get(k).getSeg().getMinY()){
			    							intersect = k;
			    							pointY = j;
			    						}
				    				}
				    				if(intersect != -1){
				    					draggedShape.addTag(allMagH.get(k).getTag());
				    					horizontalMap.put(draggedShape, allMagH.get(k));
				    					draggedShape.translateTo(pointX,pointY);
				    					break;
				    				}
				    				else{
				    					draggedShape.removeTag(allMagH.get(k).getTag());
				    					horizontalMap.remove(draggedShape);
				    				}
		    					}
			    				
			    				for (int k = 0; k < allMagV.size() ; k++){
			    					int intersect = -1;
			    					int pointX = -1;
			    					int pointY = (int) draggedShape.getCenterY();
				    				for (int j = (int) draggedShape.getMinX(); j <= (int) draggedShape.getMaxX() ; j++){
				    					if(j <= (int) allMagV.get(k).getSeg().getMaxX() && j >= (int) allMagV.get(k).getSeg().getMinX()){
			    							intersect = k;
			    							pointX = j;
			    						}
				    				}
				    				if(intersect != -1){
				    					draggedShape.addTag(allMagV.get(k).getTag());
				    					verticalMap.put(draggedShape, allMagV.get(k));
				    					draggedShape.translateTo(pointX,pointY);
				    					break;
				    				}
				    				else{
				    					draggedShape.removeTag(allMagV.get(k).getTag());
				    					verticalMap.remove(draggedShape);
				    				}
		    					}
			    			
				    		}
				    } ;
				    
				    
				} ;
				
				public State hide = new State(){
					Transition ok = new Release(BUTTON2,">> start"){
						public void action(){
							for(MagneticGuide mag : allMagH){
								mag.getSeg().setTransparencyOutline(1);
							}
							for(MagneticGuide mag : allMagV){
								mag.getSeg().setTransparencyOutline(1);
							}
						}
					};
				};
				
				public State dragSeg = new State(){
					Transition drag = new Drag(BUTTON1) {
						  public void action() {
							 if (mSelect == null){
								 System.out.println("vous avez rien selecter!");
							 }
							 else if(mSelect.getType().equals("horizontal")){
								 for(CShape shape : horizontalMap.keySet()){
									 if((horizontalMap.get(shape)).getNb() == mSelect.getNb()){
										 Point2D q = getPoint() ;
										 shape.translateBy(0, q.getY() - p.getY()) ;
									 }
								 }
								 Point2D q = getPoint() ;
								 draggedShape.translateBy(0, q.getY() - p.getY()) ;
								 p = q;
								 
							 }
							 else if(mSelect.getType().equals("vertical")){
								 for(CShape shape : verticalMap.keySet()){
									 if((verticalMap.get(shape)).getNb() == mSelect.getNb()){
										 Point2D q = getPoint() ;
										 shape.translateBy(q.getX() - p.getX(), 0) ;
									 }
								 }
								 Point2D q = getPoint() ;
								 draggedShape.translateBy(q.getX() - p.getX(), 0) ;
								 p = q ;
							 }
							 
						  }
					   } ;
					   
				   Transition release = new Release(BUTTON1, ">> start"){
					   
				   };
				};

		  } ;
	   sm.attachTo(canvas);

	   pack() ;
	   setVisible(true) ;
	   canvas.requestFocusInWindow() ;
    }

    public void populate() {
	   int width = canvas.getWidth() ;
	   int height = canvas.getHeight() ;

	   double s = (Math.random()/2.0+0.5)*30.0 ;
	   double x = s + Math.random()*(width-2*s) ;
	   double y = s + Math.random()*(height-2*s) ;

	   int red = (int)((0.8+Math.random()*0.2)*255) ;
	   int green = (int)((0.8+Math.random()*0.2)*255) ;
	   int blue = (int)((0.8+Math.random()*0.2)*255) ;

	   CRectangle r = canvas.newRectangle(x,y,s,s) ;
	   r.setFillPaint(new Color(red, green, blue)) ;
	   r.addTag(oTag) ;
    }

    public static void main(String[] args) {
	   MagneticGuides guides = new MagneticGuides("Magnetic guides",600,600) ;
	   for (int i=0; i<20; ++i) guides.populate() ;
	   guides.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;
    }

}